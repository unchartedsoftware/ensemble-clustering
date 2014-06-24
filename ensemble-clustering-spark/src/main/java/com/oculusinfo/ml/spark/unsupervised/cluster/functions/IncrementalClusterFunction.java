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
import java.util.Iterator;
import java.util.Map;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterFactory;

import scala.Tuple2;
import org.apache.spark.api.java.function.FlatMapFunction;

public class IncrementalClusterFunction implements FlatMapFunction<Iterator<Tuple2<String, Instance>>, Instance> {
	private static final long serialVersionUID = 5096750219795665262L;

	private double threshold;
	private ClusterFactory clusterFactory;
	private DistanceFunction distFunc;
	
	public IncrementalClusterFunction(DistanceFunction distFunc, ClusterFactory clusterFactory, double threshold) {
		this.threshold = threshold;
		this.distFunc = distFunc;
		this.clusterFactory = clusterFactory;
	}
	
	@Override
	public Iterable<Instance> call(Iterator<Tuple2<String, Instance>> instances) throws Exception {
		Map<String, Instance> clusters = new HashMap<String, Instance>();
		
		BestClusterFunction bestClusterFunc = new BestClusterFunction(distFunc, clusters, threshold);
		
		while (instances.hasNext()) {
			Tuple2<String, Instance> inst = instances.next();
			
			Tuple2<String, Instance> result = bestClusterFunc.call(inst);
		
			Cluster c;
			if (result._1 == null) {
				c = clusterFactory.create();
				clusters.put(c.getId(), c);
			}
			else {
				c = (Cluster)clusters.get(result._1);
			}
			c.add(inst._2);
		}
		return clusters.values();
	}

}
