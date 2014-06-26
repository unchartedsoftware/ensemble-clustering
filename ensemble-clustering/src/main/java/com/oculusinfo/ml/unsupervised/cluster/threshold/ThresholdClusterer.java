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
package com.oculusinfo.ml.unsupervised.cluster.threshold;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.AbstractClusterer;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;

/***
 * This class implements a single pass threshold clustering algorithm.
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
 * To speed up the clustering even faster, the firstCandidate option can be set to true which will halt the 
 * search for the best cluster after the first candidate is found.
 *  
 * @author slangevin
 *
 */
public class ThresholdClusterer extends AbstractClusterer {
	protected double threshold = 0.5; // default threshold
	
	public ThresholdClusterer() {
		super(false, true, true);
	}
	
	public ThresholdClusterer(boolean firstCandidate, boolean penalizeMissingFeatures) {
		super(firstCandidate, true, penalizeMissingFeatures);
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	@Override
	protected boolean isCandidate(Instance inst, Cluster candidate,
			double score, Cluster best, double bestScore) {
		
		return (score < threshold && score < bestScore);  // lower score less than threshold is better
	}
}
