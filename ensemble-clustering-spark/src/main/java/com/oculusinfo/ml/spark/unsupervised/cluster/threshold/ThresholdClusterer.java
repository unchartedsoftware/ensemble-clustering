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
package com.oculusinfo.ml.spark.unsupervised.cluster.threshold;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.spark.SparkDataSet;
import com.oculusinfo.ml.spark.unsupervised.cluster.SparkClusterResult;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.AggregateClusterFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.BestClusterFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.DistanceFunction;
import com.oculusinfo.ml.spark.unsupervised.cluster.functions.InstanceToClusterFunction;
import com.oculusinfo.ml.unsupervised.cluster.BaseClusterer;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterFactory;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;

/***
 * This class implements a distributed version of a single pass threshold clustering algorithm.
 * 
 * The algorithm is defined as:
 *    set clusters = []
 *    for each instance
 *    	 best cluster = none
 *    	 best score = infinity
 *       for each cluster in clusters
 *          if distance(instance, cluster) < threshold and < score
 *          	best cluster = cluster
 *          	best score = distance(instance, cluster)
 *           
 *       if best cluster is not none
 * 		 	add instance to best cluster
 *		 else
 *			create new cluster with instance as a member
 *          add cluster to clusters
 *          
 * This clusterer is useful when multiple passes over the data are undesirable or the number of clusters is unknown.
 * Tuning is required to choose an appropriate threshold that controls when new clusters can be created. 
 * 
 * Note that the order that clustering is sensitive to the order data is processed. 
 *  
 * @author slangevin
 *
 */
public class ThresholdClusterer extends BaseClusterer {
	private double threshold;
	private String centroidsPath = null;
	private String clustersPath = null;
	private DistanceFunction distFunc;
	protected static Logger log = LoggerFactory.getLogger("com.oculusinfo");
	
	public ThresholdClusterer(double threshold) {
		this(threshold, null, null);
	}
	
	public ThresholdClusterer(double threshold, String centroidsPath, String clustersPath) {
		super(true);
		this.threshold = threshold;
		this.centroidsPath = centroidsPath;
		this.clustersPath = clustersPath;
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public void init() {
		// TODO do any initialization here
	}

	@Override
	public void terminate() {
		// TODO Do any cleanup here
	}

	@Override
	public ClusterResult doIncrementalCluster(DataSet ds, List<Cluster> clusters) {
		throw new RuntimeException("doIncrementalCluster is not implemented for Spark ThresholdClusterer");
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
	public SparkClusterResult doCluster(DataSet ds) {
		// SparkDataSet needs to be passed in
		SparkDataSet rdd = (SparkDataSet)ds;
		
		// cache dataset in memory
//		rdd.getRDD().cache();
		
		distFunc = new DistanceFunction(this.typeDefs);
		ClusterFactory clusterFactory = new ClusterFactory(this.typeDefs, this.onlineUpdate);
		
		log.info("Starting threshold clusterer with threshold {}", threshold);
		
		// TODO look at using a reduce function 
		// Idea is the first step is a map<Instance, List<Instance>> that converts each instance to a single "cluster"
		// second step is a reduce where input is a List<Instances> and produces a List<Instances>
		// this step would merge clusters within threshold
		
		JavaPairRDD<String, Instance> instances = rdd.getRDD();
		instances.cache();
		
		// convert each instance into a singleton cluster
		JavaRDD<Map<String, Instance>> singletons = rdd.getRDD().map( new InstanceToClusterFunction(clusterFactory) );
		//singletons.cache();
		
		log.info("Generated initial singleton clusters");
		
		// merge clusters together
		Map<String, Instance> clusters = singletons.reduce( new AggregateClusterFunction(distFunc, threshold) );
		
		log.info("Merging clusters completed with {} clusters", clusters.size());
		
		// find the best cluster for each instance
		JavaPairRDD<String, Instance> bestCluster = instances.mapToPair( new BestClusterFunction(distFunc, clusters) );
		
		log.info("Output results");
		
		if (clusters != null && centroidsPath != null) rdd.getContext().parallelize(new ArrayList<Instance>(clusters.values())).saveAsTextFile(centroidsPath);
	
		if (bestCluster != null && clustersPath != null) bestCluster.saveAsTextFile(clustersPath);
		
		log.info("Threshold clusterer completed");
		
		// return the cluster membership rdd
		return new SparkClusterResult(bestCluster);
	}

	@Override
	public double distance(Instance inst1, Instance inst2) {
		return distFunc.distance(inst1, inst2);
	}

}
