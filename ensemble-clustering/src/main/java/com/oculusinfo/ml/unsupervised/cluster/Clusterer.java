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
package com.oculusinfo.ml.unsupervised.cluster;

import java.util.List;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;

public interface Clusterer {
	
	/***
	 * Method to initialize the clusterer - useful for clusterers that need to allocated resources such as thread pools
	 * 
	 */
	public void init();
	
	/***
	 * Method to terminate the clusterer - useful for clusterers that need to cleanup resources such as thread pools
	 * 
	 */
	public void terminate();
	
	/***
	 * Cluster the instances in ds using the clusters provided
	 * 
	 * @param ds is the DataSet containing the new instances to cluster
	 * @param clusters is the list of clusters to use during clustering 
	 * @return the list of clusters that were modified
	 */
	public ClusterResult doIncrementalCluster(DataSet ds, List<Cluster> clusters);
	
	/***
	 * Generate clusters given the provided DataSet ds
	 * 
	 * @param ds is the DataSet containing the instances to cluster
	 * @return the list of clusters created
	 */
	public ClusterResult doCluster(DataSet ds);
	
	/***
	 * Calculate the distance of the two instances using the distance measures 
	 * associated with this clusterer.
	 * 
	 * @param inst1
	 * @param inst2
	 * @return a double value representing the distance between inst1 and inst2
	 */
	public double distance(Instance inst1, Instance inst2);
}
