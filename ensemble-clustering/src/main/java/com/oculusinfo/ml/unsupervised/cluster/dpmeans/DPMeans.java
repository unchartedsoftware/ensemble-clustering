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
package com.oculusinfo.ml.unsupervised.cluster.dpmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.kmeans.KMeans;

/***
 * The class implements the DP-Means clustering algorithm:
 * 
 * Kulis, B. and Jordan, M.I., Revisiting k-means: New algorithms via Bayesian nonparametrics, 2011.
 * 
 * This clusterer is useful when the number of clusters is unknown.  
 * Tuning is required to choose an appropriate threshold that controls when new clusters can be created. 
 * 
 * @author slangevin
 *
 */
public class DPMeans extends KMeans {
	protected double threshold = 0.5; // default threshold
	protected double convergeTest = 0.001; // default test of convergence
	
	public DPMeans(int maxIterations, boolean penalizeMissingFeatures) {
		super(1, maxIterations, penalizeMissingFeatures);
	}
	
	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	private List<Cluster> initKMeans(DataSet ds) {
		List<Cluster> kmeans = new LinkedList<Cluster>();
		
		int ki = (ds.size() < k) ? ds.size(): k;
		
		// randomly pick k instances as the initial k means
		ArrayList<String> indexes = new ArrayList<String>(ds.size()); 
		ArrayList<String> keys = new ArrayList<String>(ds.getKeys());
	    for (int i = 0; i < keys.size(); i++) {
	        indexes.add( keys.get(i) );
	    }
	    Collections.shuffle(indexes);
	    for (int i = 0; i < ki; i++) {
	    	Cluster c = this.createCluster();
	    	c.add( ds.get(indexes.get(i)) );
	    	c.updateCentroid();
	    	kmeans.add(c);
	    }
	    return kmeans;
	}
	
	private double error(ClusterResult clusters) {
		double error = 0;
		
		for (Cluster c : clusters) {
			for (Instance i : c.getMembers()) {
				error += this.distance(c, i);
			}
		}
		return error;
	}
	private boolean isConverged(double oldError, double newError) {
		return ( (oldError - newError) < convergeTest );
	}
	
	@Override
	public ClusterResult doCluster(DataSet ds) {
		boolean converged = false;
		List<Cluster> kmeans = initKMeans(ds);
		ClusterResult clusters = null;
		double prevError = Double.MAX_VALUE;
		
		double start = System.currentTimeMillis();
		
		int iteration = 0;
		
		while (!converged && iteration < maxIterations) {
			log.info("DP-Means iteration {}", (iteration+1));
			
			for (Cluster mean : kmeans) {
				mean.reset();
			}
			
			clusters = super.doCluster(ds, kmeans);
			
			if (clusters.isEmpty()) break;
		
			// compute new error
			double newError = error(clusters);
			
			// test for convergence
			converged = isConverged(prevError, newError);
				
			// save the current error
			prevError = newError;
		
			iteration++;
		}
		log.info("DP-Means completed with {} iterations", iteration);
		
		double clusterTime = System.currentTimeMillis() - start;
		log.info("Clustering time (s): {}", clusterTime / 1000);
		
		return clusters; //new InMemoryClusterResult(kmeans);
	}

	@Override
	protected boolean isCandidate(Instance inst, Cluster candidate,
			double score, Cluster best, double bestScore) {
		return (score < threshold && score < bestScore);  // lower score less than threshold is better
	}

}
