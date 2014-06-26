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
package com.oculusinfo.ml.feature.numeric;

import java.util.ArrayList;
import java.util.List;

import com.oculusinfo.ml.feature.Feature;

/***
 * A NumericVectorFeature represents a vector of double precision numbers.
 * 
 * Useful for representing a large class of data that is numeric in nature or can be encoded as such
 * 
 * @author slangevin
 *
 */
public class NumericVectorFeature extends Feature {
	private static final long serialVersionUID = 4845380498652903996L;
	private double[] vector;
	
	private String vectorToString() {
		StringBuilder str = new StringBuilder();
		str.append("[");
		for (int i=0; i < vector.length; i++) {
			str.append(vector[i]);
			if (i < vector.length - 1) str.append(";");
		}
		str.append("]");
		return str.toString();
	}
	
	@Override
	public String toString() {
		return (this.getName() + ":" + vectorToString());
	}
	
	public NumericVectorFeature() {
		super();
	}
	
	public NumericVectorFeature(String name) {
		super(name);
	}

	public void setValue(double[] vector) {
		this.vector = vector;
	}
	
	public void setValue(List<Double> vector) {
		setValue(new ArrayList<Double>(vector));
	}
	
	public double[] getValue() {
		return this.vector;
	}
}
