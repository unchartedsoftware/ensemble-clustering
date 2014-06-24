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

import java.util.HashMap;
import java.util.Map;

import scala.Tuple2;
import org.apache.spark.api.java.function.Function;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterFactory;

public class FindBestClusterFunction implements Function<Tuple2<String, Instance>, Map<String, Instance>> {
	private static final long serialVersionUID = 1508254065822051773L;
	
	private double threshold = Double.MAX_VALUE;
	private DistanceFunction distFunc;
	private Map<String, Instance> clusters;
	private ClusterFactory clusterFactory;
	
	public FindBestClusterFunction(DistanceFunction distFunc, Map<String, Instance> clusters) {
		this.distFunc = distFunc;
		this.clusters = clusters;
	}
	
	public FindBestClusterFunction(DistanceFunction distFunc, Map<String, Instance> clusters, double threshold, ClusterFactory clusterFactory) {
		this.distFunc = distFunc;
		this.clusters = clusters;
		this.threshold = threshold;
		this.clusterFactory = clusterFactory;
	}


	@Override
	public Map<String, Instance> call(Tuple2<String, Instance> inst) throws Exception {
		Instance bestCluster = null;
		double bestScore = Double.MAX_VALUE;
		
		for (String clusterId : clusters.keySet()) {
			Instance cluster = clusters.get(clusterId);
			double d = distFunc.distance(inst._2, cluster);
		
			if (d < bestScore && d < threshold) {
				bestScore = d;
				bestCluster = cluster;
			}
		}
		Map<String, Instance> result = new HashMap<String, Instance>();
		if (bestCluster == null) {
			Cluster c = clusterFactory.create();
			c.add(inst._2);
			result.put(c.getId(), c);
		}
		else {
			result.put(bestCluster.getId(), bestCluster);
		}
		return result;
	}
}
