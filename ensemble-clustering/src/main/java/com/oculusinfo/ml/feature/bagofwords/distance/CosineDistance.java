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
 * A distance function that computes 1 - Cosine Similarity between two BagOfWordFeatures
 * 
 * @author slangevin
 *
 */
public class CosineDistance extends DistanceFunction<BagOfWordsFeature> {
	private static final long serialVersionUID = -635994591459075095L;

	public CosineDistance() {
		this(1);
	}
	
	public CosineDistance(double weight) {
		super(weight);
	}
	
	@Override
	public double distance(BagOfWordsFeature x, BagOfWordsFeature y) {
		double dotprod = 0, xlength = 0, ylength = 0;
		
		for (FeatureFrequency xf : x.getValues()) {
			xlength += xf.frequency * xf.frequency;
			FeatureFrequency yf = y.getCount(xf.feature.getName());
			if (yf != null) dotprod += xf.frequency * yf.frequency;
		}
		for (FeatureFrequency yf : y.getValues()) {
			ylength += yf.frequency * yf.frequency;
		}
		// if both are empty then distance is max
		if (xlength == 0 || ylength == 0) return 1.0;
		
		return 1.0 - (dotprod / ( Math.sqrt(xlength) * Math.sqrt(ylength) ));
	}

}
