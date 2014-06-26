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
package com.oculusinfo.ml.feature.string.distance;

import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.string.StringFeature;

/***
 *  A distance function that compares two StringFeatures and returns 1 when they are equal 0 otherwise
 * 
 * @author slangevin
 *
 */
public class ExactTokenMatchDistance extends DistanceFunction<StringFeature> {
	private static final long serialVersionUID = -3651531184290382230L;

	public ExactTokenMatchDistance() {
		super(1);
	}
	
	public ExactTokenMatchDistance(double weight) {
		super(weight);
	}
	
	@Override
	public double distance(StringFeature x, StringFeature y) {
		return (x.getValue().equalsIgnoreCase(y.getValue())) ? 0 : 1;
	}
}
