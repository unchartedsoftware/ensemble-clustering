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

import java.util.Map;

import scala.Tuple2;
import org.apache.spark.api.java.function.Function2;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;

public class AggregateClusterFunction implements Function2<Map<String, Instance>, Map<String, Instance>, Map<String, Instance>> {
	private static final long serialVersionUID = 3732157666163441695L;
	
	private double threshold;
	private DistanceFunction distFunc;
	
	public AggregateClusterFunction(DistanceFunction distFunc, double threshold) {
		this.distFunc = distFunc;
		this.threshold = threshold;
	}

	@Override
	public Map<String, Instance> call(Map<String, Instance> clusters1, Map<String, Instance> cluster2) throws Exception {
		BestClusterFunction bestClusterFunc = new BestClusterFunction( distFunc, clusters1, threshold );
		
		for (String key : cluster2.keySet()) {
			Instance instance = cluster2.get(key);
			Tuple2<String, Instance> result = bestClusterFunc.call(new Tuple2<String, Instance>(instance.getId(), instance));
			
			if (result == null) {
				clusters1.put(instance.getId(), instance);
			}
			else {
				Cluster cluster = (Cluster)clusters1.get(result._1); 
				cluster.add(instance);  // revise the cluster centroid
				cluster.getMembers().clear();  // no need to retain the member list
			}
		}
		return clusters1;
	}
}
