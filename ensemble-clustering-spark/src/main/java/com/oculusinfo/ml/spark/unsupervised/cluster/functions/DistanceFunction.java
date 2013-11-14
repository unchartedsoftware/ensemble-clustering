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

import java.io.Serializable;
import java.util.Map;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.Feature;
import com.oculusinfo.ml.unsupervised.cluster.FeatureTypeDefinition;

public class DistanceFunction implements Serializable {
	private static final long serialVersionUID = 5075251184563041056L;
	private Map<String, FeatureTypeDefinition> typeDefs;
	
	public DistanceFunction(Map<String, FeatureTypeDefinition> typeDefs) {
		this.typeDefs = typeDefs;
	}
	
	@SuppressWarnings("unchecked")
	public double distance(Instance inst1, Instance inst2) {
		double totalDist = 0;

		try {
			for (FeatureTypeDefinition typedef : typeDefs.values()) {
				if (typedef.distFunc.getWeight() < 0.00001) continue;  // skip if weight is near zero
				
				Feature f1 = inst1.getFeature(typedef.featureName);
				Feature f2 = inst2.getFeature(typedef.featureName);
				if (f1 == null || f2 == null) continue;
									
				totalDist += typedef.distFunc.distance(f1, f2)  * typedef.distFunc.getWeight(); 	
			}
		}
		catch (Exception e) {
			// TODO - handle exception
		}
		return totalDist;
	}
}