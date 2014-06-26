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
package com.oculusinfo.ml.spark.unsupervised.cluster.kmeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.spark.SparkDataSet;
import com.oculusinfo.ml.spark.unsupervised.cluster.SparkClusterResult;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.BestClusterFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.ComputeCentroidFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.DistanceFunction;
import com.oculusinfo.ml.unsupervised.cluster.BaseClusterer;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterFactory;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;

/***
 * The class implements a distributed version of the K-Means clustering algorithm.
 * 
 * @author slangevin
 *
 */
public class KMeansClusterer extends BaseClusterer {
	private int k;
	private int maxIterations;
	private String centroidsPath = null;
	private String clustersPath = null;
	private double convergenceTest;
	private DistanceFunction distFunc;
	protected static Logger log = LoggerFactory.getLogger("com.oculusinfo");
	
	public KMeansClusterer(int k, int maxIterations, double convergenceTest) {
		this(k, maxIterations, convergenceTest, null, null);
	}
	
	public KMeansClusterer(int k, int maxIterations, double convergenceTest, String centroidsPath, String clustersPath) {
		super(true);
		this.k = k;
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
		throw new RuntimeException("doIncrementalCluster is not supported in Spark KMeansClusterer");
	}
	
	private Map<String, Instance> initKMeans(SparkDataSet ds) {
		Map<String, Instance> kmeans = new HashMap<String, Instance>(k);
		List<Tuple2<String, Instance>> kpoints = ds.getRDD().takeSample(false, k, (new Random()).nextInt());
		
		for (Tuple2<String, Instance> point : kpoints) {
			Cluster c = this.createCluster();
			c.add(point._2);
			kmeans.put(c.getId(), c);
		}
		return kmeans;
	}

	@Override
	public SparkClusterResult doCluster(DataSet ds) {
		// SparkDataSet needs to be passed in
		SparkDataSet rdd = (SparkDataSet)ds;
		
		// cache dataset in memory
//		rdd.getRDD().persist(StorageLevel.MEMORY_AND_DISK());
//		rdd.getRDD().cache();
		
		// generate the initial points for kmeans
		Map<String, Instance> curKmeans = initKMeans(rdd);
		ClusterFactory clusterFactory = new ClusterFactory(this.typeDefs, this.onlineUpdate);
		
		double distance = 1.0;
		int iteration = 0;
		
		distFunc = new DistanceFunction(this.typeDefs);
		
		JavaPairRDD<String, Instance> bestCluster = null;
		JavaPairRDD<String, Instance> kmeans = null;
		
		while (iteration < maxIterations && distance > this.convergenceTest) {
			log.info("K-Means iteration {}", (iteration+1));
			
			// find the best kmeans for each instance
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
				if ((pair._2 instanceof Cluster) == false) {
					Cluster c = this.createCluster();
					c.add(pair._2);
					curKmeans.put(pair._1, c);
				}
				else {
					curKmeans.put(pair._1, pair._2);
				}
			}
			
			iteration++;
		}		
		
		// training is done - assign each instance to a cluster
		bestCluster = rdd.getRDD().mapToPair( new BestClusterFunction( distFunc, curKmeans ) );
		
		log.info("Output results");
		
		if (bestCluster != null && clustersPath != null) bestCluster.saveAsTextFile(clustersPath);
		if (kmeans != null && centroidsPath != null) kmeans.saveAsTextFile(centroidsPath);
		
		log.info("K-Means completed with {} iterations", iteration);
		
		// return the cluster membership rdd
		return new SparkClusterResult(bestCluster);
	}

	@Override
	public double distance(Instance inst1, Instance inst2) {
		return distFunc.distance(inst1, inst2);
	}

}
