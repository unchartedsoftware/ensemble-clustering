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
package com.oculusinfo.ml.spark.unsupervised.cluster.functions;

import org.apache.spark.api.java.function.Function2;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.feature.Feature;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterFactory;

public class ComputeCentroidFunction implements Function2<Instance, Instance, Instance> {
	private static final long serialVersionUID = -3281886552602674327L;
	
	private ClusterFactory clusterFactory;
	
	public ComputeCentroidFunction(ClusterFactory clusterFactory) {
		this.clusterFactory = clusterFactory;
	}
	
	@SuppressWarnings("unchecked")
	private void updateCluster(Instance inst, Cluster cluster) {
		if (inst instanceof Cluster) {  // merge the clusters
			Cluster c = (Cluster)inst;
			for (String key : c.getCentroids().keySet()) {
				Centroid<Feature> update = c.getCentroids().get(key);
				Centroid<Feature> centroid = cluster.getCentroids().get(key);
								
				// get all the aggregate feature values associated with update
				for (Feature f : update.getAggregatableCentroid()) {
				    centroid.add(f);
				}
			}
			
			// after merging cluster we should manually update the resulting centroid
			cluster.updateCentroid();
			
			// TODO should handle merging the cluster members
		}
		else {  // simply add the instance to the cluster
			cluster.add(inst);
		}
	}
	
	@Override
	public Instance call(Instance inst1, Instance inst2) throws Exception {
		Cluster c = clusterFactory.create();
		
		// aggregate inst1 and inst2 in a cluster
		updateCluster(inst1, c);
		updateCluster(inst2, c);
	
		return c;
	}
	
}