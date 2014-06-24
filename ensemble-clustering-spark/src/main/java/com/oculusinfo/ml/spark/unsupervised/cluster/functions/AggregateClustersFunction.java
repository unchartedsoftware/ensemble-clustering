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

public class AggregateClustersFunction implements Function2<Map<String, Instance>, Map<String, Instance>, Map<String, Instance>> {
	private static final long serialVersionUID = 1L;

	private double threshold;
	private DistanceFunction distFunc;
	
	public AggregateClustersFunction(DistanceFunction distFunc, double threshold) {
		this.distFunc = distFunc;
		this.threshold = threshold;
	}
	
	@Override
	public Map<String, Instance> call(Map<String, Instance> clusterList1, Map<String, Instance> clusterList2) throws Exception {
		
		if (clusterList1.isEmpty()) {
			clusterList1.putAll(clusterList2);
			return clusterList1;
		}
			
		for (String id : clusterList2.keySet()) {
			Instance c = clusterList2.get(id);
			BestClusterFunction bestClusterFunc = new BestClusterFunction( distFunc, clusterList1, threshold );
			Tuple2<String, Instance> result = bestClusterFunc.call(new Tuple2<String, Instance>(c.getId(), c));
					
			if (result._1 == null) {
				clusterList1.put(c.getId(), c);
			}
			else {
				((Cluster)clusterList1.get(result._1)).add(c);
			}
		}
		return clusterList1;
	}
}
