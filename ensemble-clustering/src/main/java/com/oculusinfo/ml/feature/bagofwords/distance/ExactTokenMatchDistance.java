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
package com.oculusinfo.ml.feature.bagofwords.distance;

import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.bagofwords.BagOfWordsFeature;
import com.oculusinfo.ml.stats.FeatureFrequency;

/***
 * A distance function that computes distance between two BagOfWordFeatures by 
 * the size of the intersection of their words divided the largest set of words
 * 
 * @author slangevin
 *
 */
public class ExactTokenMatchDistance extends DistanceFunction<BagOfWordsFeature> {
	private static final long serialVersionUID = -3651531184290382230L;

	public ExactTokenMatchDistance() {
		super(1);
	}
	
	public ExactTokenMatchDistance(double weight) {
		super(weight);
	}
	
	@Override
	public double distance(BagOfWordsFeature x, BagOfWordsFeature y) {
		double dist = 0;
		int m = x.getValues().size();
		int n = y.getValues().size();
		double norm = Math.max(m, n);
		
		// set a to be the largest nominal list
		BagOfWordsFeature a = x, b = y;
		if (m < n) {
			a = y;
			b = x;
		}
		
		for (FeatureFrequency xf : a.getValues()) {
			if (b.getCount(xf.feature.getName()) == null) {
				dist += 1;
			}
		}

		return dist / norm;
	}
}
