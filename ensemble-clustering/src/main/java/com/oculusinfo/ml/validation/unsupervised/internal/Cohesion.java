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
package com.oculusinfo.ml.validation.unsupervised.internal;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.Clusterer;

/***
 * An internal clustering validation implementation of cluster cohesion
 * 
 * @author slangevin
 *
 */
public class Cohesion {
	
	public static double cohesion(Clusterer clusterer, Cluster cluster) {
		double norm = cluster.size();
		double cohesion = 0;
		for (Instance inst : cluster.getMembers()) {
			if (inst instanceof Cluster) {
				cohesion += cohesion(clusterer, (Cluster)inst);
			}
			else {
				cohesion += clusterer.distance(cluster, inst);
			}
		}
		return cohesion / norm;
	}

	public static double validate(Clusterer clusterer, ClusterResult clusters) {
		double cohesion = 0;
		double norm = clusters.size();
		for (Cluster c : clusters) {
			cohesion += cohesion(clusterer, c);
		}
		return cohesion / norm;
	}
}
