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
package com.oculusinfo.ml.unsupervised.cluster.kmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.AbstractClusterer;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.InMemoryClusterResult;

/***
 * The class implements the K-Means clustering algorithm using the K++ method for initializing the k-means
 * 
 * @author slangevin
 *
 */
public class KMeans extends AbstractClusterer {
	
	protected int k;
	protected int maxIterations;
	protected boolean debugClusters;
	
	public KMeans(int k, int maxIterations, boolean penalizeMissingFeatures) {
		super(false, false, penalizeMissingFeatures);
		this.k = k;
		this.maxIterations = maxIterations;
		this.debugClusters = false;
	}

	public void debugClusters () {
	    debugClusters = true;
	}

	public int getK() {
		return k;
	}
	
	public int getMaxIterations() {
		return maxIterations;
	}
	
	/***
	 * K++ method for selecting initial k-means
	 * 
	 * The exact algorithm is as follows:
	 * 1) Choose one center uniformly at random from among the data points.
	 * 2) For each data point x, compute D(x), the distance between x and the nearest center that has already been chosen.
	 * 3) Choose one new data point at random as a new center, using a weighted probability distribution where a point x is chosen with probability proportional to D(x)2.
	 * 4) Repeat Steps 2 and 3 until k centers have been chosen.
	 * 
	 * @param ds
	 * @return the initial kmeans
	 */
	private List<Cluster> initKMeans(DataSet ds) {
		final int MAX_ATTEMPT = 3;
		List<Cluster> kmeans = new LinkedList<Cluster>();
		
		int ki = (ds.size() < k) ? ds.size(): k;
		
		// randomly pick k instances as the initial k means
		ArrayList<String> keys = new ArrayList<String>(ds.getKeys());
		
	    // select first cluster with a uniform distribution
	    Collections.shuffle(keys);
	    
	    Cluster cluster = this.createCluster();
	    cluster.add( ds.get(keys.get(0)) );
	    cluster.updateCentroid();
    	kmeans.add(cluster);
    	
	    int iteration = 0;
	    
    	while (kmeans.size() < ki && iteration < MAX_ATTEMPT) {
    		for (int i = 1; i < keys.size(); i++) {
    			String key = keys.get(i);
    			if (key == null) continue;  // skip over already used keys
    			Instance inst = ds.get(key);
    			double min = Double.MAX_VALUE;
    			
    			for (Cluster c : kmeans) {
    				double d = this.distance(c, inst);
    				if (d < min) min = d;
    			}
    			if (Math.random() < Math.pow(min, 2)) {
    				cluster = this.createCluster();
    				cluster.add( ds.get(key) );
    				cluster.updateCentroid();
    				kmeans.add(cluster);
    				keys.set(i, null);  // set key to null to signal it has been used
    			}
    			if (kmeans.size() == ki) break; // we have select the kmeans
	    	}
    		iteration++;
	    }
    	
    	if (kmeans.size() < k) {
    		log.info("Couldn't find k centroids to initialize kMeans++.  Using " + kmeans.size() + " centroids.");
    	}
    	
	    return kmeans;
	}
	
	@Override
	public ClusterResult doCluster(DataSet ds) {	
		List<Cluster> kmeans = initKMeans(ds);
		
		double start = System.currentTimeMillis();
		
		int iteration = 0;
		
		while (iteration < maxIterations) {
			log.info("K-Means iteration {}", (iteration+1));
			
			for (Cluster mean : kmeans) {
				mean.reset();
			}
			
			ClusterResult modified = super.doCluster(ds, kmeans);

			if (debugClusters) {
			    log.info("Post iteration {};", iteration+1);
			    int c=0;
			    for (Cluster cluster: kmeans)
			        log.info("\t "+c+": "+cluster.getIterationDebugInfo());
			}

			if (modified.isEmpty()) break;
			
			iteration++;
		}
		log.info("K-Means completed with {} iterations", iteration);
		
		double clusterTime = System.currentTimeMillis() - start;
		log.info("Clustering time (s): {}", clusterTime / 1000);
		
		return new InMemoryClusterResult(kmeans);
	}

	@Override
	protected boolean isCandidate(Instance inst, Cluster candidate,
			double score, Cluster best, double bestScore) {
		return true; //(score < bestScore);
	}

}
