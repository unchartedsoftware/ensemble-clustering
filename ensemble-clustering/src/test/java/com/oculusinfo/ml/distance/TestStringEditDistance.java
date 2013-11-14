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

import com.oculusinfo.ml.feature.string.distance.EditDistance;
import com.oculusinfo.ml.feature.string.StringFeature;

public class TestStringEditDistance {
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
		
		EditDistance d = new EditDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0));
		
		distance = d.distance(t1, t2);
		Assert.assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric() {
		StringFeature t1 = new StringFeature();
		t1.setValue("dog");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("doggie");
		
		EditDistance d = new EditDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		 
		Assert.assertTrue(isEqual(d1, d2));
		
		d1 = d.distance(t1, t2);
		d2 = d.distance(t2, t1);
		 
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistance1() {
		StringFeature t1 = new StringFeature();
		t1.setValue("dog");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("cat");
		
		EditDistance d = new EditDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 1.0));
		
		distance = d.distance(t1, t2);
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 1.0));
	}
	
	@Test
	public void testDistance2() {
		StringFeature t1 = new StringFeature();
		t1.setValue("dogs");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("dog");
		
		EditDistance d = new EditDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0.25));
		
		distance = d.distance(t1, t2);
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0.25));
	}
	
	@Test
	public void testDistance3() {
		StringFeature t1 = new StringFeature();
		t1.setValue("cats");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("dogs");
		
		EditDistance d = new EditDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0.75));
		
		distance = d.distance(t1, t2);
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0.75));
	}
	
	@Test 
	public void testDistance4() {
		StringFeature t1 = new StringFeature();
		t1.setValue("cats");
		
		StringFeature t2 = new StringFeature();
		t2.setValue("cat");
		
		EditDistance d = new EditDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		distance = d.distance(t1, t2);
		System.out.println(distance);
		
		t1.setValue("caats");
		t2.setValue("caat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("caaats");
		t2.setValue("caaat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("caaats");
		t2.setValue("caaat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("caaaats");
		t2.setValue("caaaat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("caaaaats");
		t2.setValue("caaaaat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("caaaaaats");
		t2.setValue("caaaaaat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("caaaaaaats");
		t2.setValue("caaaaaaat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("caaaaaaaats");
		t2.setValue("caaaaaaaat");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		
		t1.setValue("Reno/Tahoe and Visiting Las Vegas");
		t2.setValue("Sacramento");
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
	}
}
