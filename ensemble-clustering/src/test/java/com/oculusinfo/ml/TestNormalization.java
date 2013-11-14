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
package com.oculusinfo.ml;

import org.junit.Test;

import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;

public class TestNormalization {

	@Test
	public void test() {
		DataSet ds = new DataSet();
		
		for (int i=0; i < 5; i++) {
			Instance inst = new Instance();
			NumericVectorFeature v = new NumericVectorFeature("v");
			v.setValue(new double[] {i, i*10, i*100});
			inst.addFeature(v);
			ds.add(inst);
		}
		
		ds.normalizeInstanceFeature("v");
		
		for (Instance inst : ds) {
			NumericVectorFeature v = (NumericVectorFeature)inst.getFeature("v");
			double[] vals = v.getValue();
			for (int i=0; i < vals.length; i++) {
				System.out.print(vals[0] + ", ");
			}
			System.out.println();
		}
		
	}

}
