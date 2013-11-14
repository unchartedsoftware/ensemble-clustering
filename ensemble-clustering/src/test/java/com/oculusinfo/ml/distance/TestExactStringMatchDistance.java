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
package com.oculusinfo.ml.distance;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;

import com.oculusinfo.ml.feature.string.StringFeature;
import com.oculusinfo.ml.feature.string.distance.ExactTokenMatchDistance;

public class TestExactStringMatchDistance {
double epsilon = 0.00001;
	
	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	@Test
	public void testIdentical() {
		StringFeature t1 = new StringFeature();
		t1.setValue("dog");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("dog");
		
		ExactTokenMatchDistance d = new ExactTokenMatchDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0));
		
		distance = d.distance(t1, t2);
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0));
	}

	
	@Test
	public void testSymmetric() {
		StringFeature t1 = new StringFeature();
		t1.setValue("dog");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("dog");
		
		ExactTokenMatchDistance d = new ExactTokenMatchDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		 
		Assert.assertTrue(isEqual(d1, d2));
		
		d1 = d.distance(t1, t2);
		d2 = d.distance(t2, t1);
		 
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDisjoint() {
		StringFeature t1 = new StringFeature();
		t1.setValue("dog");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("cat");
		
		ExactTokenMatchDistance d = new ExactTokenMatchDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 1.0));
		
		distance = d.distance(t1, t2);
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 1.0));
	}
}
