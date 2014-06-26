/**
 * Copyright (c) 2013 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oculusinfo.ml.spark.unsupervised.cluster.dpmeans;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.spark.SparkDataSet;
import com.oculusinfo.ml.spark.unsupervised.cluster.SparkClusterResult;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.AggregateClusterFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.BestClusterFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.ComputeCentroidFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.DistanceFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.FindBestClusterFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.InstanceToClusterFunction;
import com.oculusinfo.ml.unsupervised.cluster.BaseClusterer;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterFactory;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;

/***
 * The class implements a distributed version of DP-Means clustering algorithm:
 * 
 * Kulis, B. and Jordan, M.I., Revisiting k-means: New algorithms via Bayesian nonparametrics, 2011.
 * 
 * This clusterer is useful when the number of clusters is unknown.  
 * Tuning is required to choose an appropriate threshold that controls when new clusters can be created. 
 * 
 * @author slangevin
 *
 */
public class DPMeansClusterer extends BaseClusterer {
	private double threshold;
	private int maxIterations;
	private String centroidsPath = null;
	private String clustersPath = null;
	private double convergenceTest;
	private DistanceFunction distFunc;
	protected static Logger log = LoggerFactory.getLogger("com.oculusinfo");
	
	public DPMeansClusterer(double threshold, int maxIterations, double convergenceTest) {
		this(threshold, maxIterations, convergenceTest, null, null);
	}
	
	public DPMeansClusterer(double threshold, int maxIterations, double convergenceTest, String centroidsPath, String clustersPath) {
		super(true);
		this.threshold = threshold;
		this.maxIterations = maxIterations;
		this.convergenceTest = convergenceTest;
		this.centroidsPath = centroidsPath;
		this.clustersPath = clustersPath;
	}
	
	@Override
	public void init() {
		// TODO do any initialization here
	}

	@Override
	public void terminate() {
		// TODO Do any cleanup here
	}
	
	public void setOutputPaths(String centroidsPath, String clustersPath) {
		this.centroidsPath = centroidsPath;
		this.clustersPath = clustersPath;
	}
	
	public String getCentroidsPath() {
		return this.centroidsPath;
	}
	
	public String getClustersPath() {
		return this.clustersPath;
	}

	@Override
	public ClusterResult doIncrementalCluster(DataSet ds, List<Cluster> clusters) {
		throw new RuntimeException("doIncrementalCluster is not implemented for Spark DPMeansClusterer");
	}
	
	private Map<String, Instance> initKMeans(SparkDataSet ds) {		
		JavaRDD<Map<String, Instance>> singletons = ds.getRDD().map( new InstanceToClusterFunction(clusterFactory) );
			
		Map<String, Instance> kmeans = singletons.reduce( new AggregateClusterFunction(distFunc, Double.MAX_VALUE) );
		
		return kmeans;
	}

	@Override
	public SparkClusterResult doCluster(DataSet ds) {
		distFunc = new DistanceFunction(this.typeDefs);
		clusterFactory = new ClusterFactory(this.typeDefs, this.onlineUpdate);
		
		// SparkDataSet needs to be passed in
		SparkDataSet rdd = (SparkDataSet)ds;
		
		// cache dataset in memory
		rdd.getRDD().cache();
		
		// generate the initial points for kmeans
		Map<String, Instance> curKmeans = initKMeans(rdd);
		
		double distance = 1.0;
		int iteration = 0;
		
		JavaRDD<Map<String, Instance>> clusters = null;
		JavaPairRDD<String, Instance> bestCluster = null;
		JavaPairRDD<String, Instance> kmeans = null;
		
		
		while (iteration < maxIterations && distance > this.convergenceTest) {
			log.info("DP-Means iteration {}", (iteration+1));
			
			// find the best kmeans for each instance
			clusters = rdd.getRDD().map( new FindBestClusterFunction( distFunc, curKmeans, threshold, clusterFactory ) );
			
			// retrieve the unique clusters
			clusters = clusters.distinct();
			
			// merge the unique clusters
			curKmeans = clusters.reduce( new AggregateClusterFunction(distFunc, threshold) );
			
			// assign each instance to best cluster
			bestCluster = rdd.getRDD().mapToPair( new BestClusterFunction( distFunc, curKmeans ) );
			
			// compute the new kmeans
			kmeans = bestCluster.reduceByKey( new ComputeCentroidFunction(clusterFactory) );
			
			// collect the new kmeans
			List<Tuple2<String, Instance>> newKmeans = kmeans.collect();
		
			// compute distance of old means to new means for convergence test
			distance = 0;
			for (Tuple2<String, Instance> pair : newKmeans) {
				double bestDist = Double.MAX_VALUE;
				for (String key : curKmeans.keySet()) {
					Instance oldMean = curKmeans.get(key);
					Instance newMean = pair._2;
					double dist = distFunc.distance(oldMean, newMean);
					if (dist < bestDist) bestDist = dist;
				}
				distance += bestDist;
			}
			
			// update the kmeans
			curKmeans.clear();
			for (Tuple2<String, Instance> pair : newKmeans) {
				curKmeans.put(pair._1, pair._2);
			}
			
			iteration++;
		}		
		
		// training is done - assign each instance to a cluster
		bestCluster = rdd.getRDD().mapToPair( new BestClusterFunction( distFunc, curKmeans ) );
		
		log.info("Output results");
		
		if (bestCluster != null && clustersPath != null) bestCluster.saveAsTextFile(clustersPath);
		if (kmeans != null && centroidsPath != null) kmeans.saveAsTextFile(centroidsPath);
		
		log.info("DP-Means completed with {} iterations", iteration);
		
		// return the cluster membership rdd
		return new SparkClusterResult(bestCluster);
	}
	
	@Override
	public double distance(Instance inst1, Instance inst2) {
		return distFunc.distance(inst1, inst2);
	}

}
