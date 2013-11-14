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

import com.oculusinfo.ml.feature.bagofwords.BagOfWordsFeature;
import com.oculusinfo.ml.feature.bagofwords.distance.CosineDistance;

public class TestCosineDistance {
double epsilon = 0.00001;
	
	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
//	@Test
//	public void testTiming() {
//		NominalFeature t1 = new NominalFeature();
//		t1.incrementValue("dog");
//		t1.incrementValue("food");
//		t1.incrementValue("house");
//		t1.incrementValue("walk");
//		t1.incrementValue("yard");
//		
//		NominalFeature t2 = new NominalFeature();
//		t2.incrementValue("cat");
//		t2.incrementValue("food");
//		t2.incrementValue("house");
//		t2.incrementValue("sand");
//		t2.incrementValue("box");
//		
//		CosineDistance d = new CosineDistance();
////		EuclideanDistance d = new EuclideanDistance(1);
//		
//		long start = System.currentTimeMillis();
//		double distance = 0;
//
//		for (int i=0; i < 300000*50 ; i++) {
////			distance = d.distance(t1, t2);
//			distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
////			Concept cx = taxonomy.findConcept(t1.getConcept());
////			Concept cy = taxonomy.findConcept(t2.getConcept());
////			Concept lca = cx.findCommonAncestor(cy);
//		}
//		double distanceTime = System.currentTimeMillis() - start;
//		System.out.println("Time: " + distanceTime/1000);
//	}
	
	@Test
	public void testIdentical() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		t1.incrementValue("food");
		t1.incrementValue("house");
		t1.incrementValue("walk");
		t1.incrementValue("yard");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("dog");
		t2.incrementValue("food");
		t2.incrementValue("house");
		t2.incrementValue("walk");
		t2.incrementValue("yard");
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testIdentical2() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("dog");
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testIdentical3() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.setCount("dog", 10);
		t1.setCount("food", 5);
		t1.setCount("house", 1);
		t1.setCount("walk", 6);
		t1.setCount("yard", 8);
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.setCount("dog", 10);
		t2.setCount("food", 5);
		t2.setCount("house", 1);
		t2.setCount("walk", 6);
		t2.setCount("yard", 8);
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("dog");
		
		CosineDistance d = new CosineDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		 
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testSymmetric2() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.setCount("dog", 10);
		t1.setCount("food", 5);
		t1.setCount("house", 1);
		t1.setCount("walk", 6);
		t1.setCount("yard", 8);
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.setCount("dog", 10);
		t2.setCount("food", 5);
		t2.setCount("house", 1);
		t2.setCount("walk", 6);
		t2.setCount("yard", 8);
		
		CosineDistance d = new CosineDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDisjoint() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 1.0));
	}
	
	@Test
	public void testDisjoint2() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		t1.incrementValue("shepard");
		t1.incrementValue("lab");
		t1.incrementValue("poodle");
		t1.incrementValue("yorkie");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		t2.incrementValue("siamese");
		t2.incrementValue("burmese");
		t2.incrementValue("bengal");
		t2.incrementValue("persian");
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 1.0));
	}
	
	@Test
	public void testDisjoint3() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		t1.incrementValue("shepard");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		t2.incrementValue("siamese");
		t2.incrementValue("burmese");
		t2.incrementValue("bengal");
		t2.incrementValue("persian");
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 1.0));
	}
	
	
	@Test
	public void testSymmetric3() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		
		CosineDistance d = new CosineDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testSymmetric4() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		t1.incrementValue("shepard");
		t1.incrementValue("lab");
		t1.incrementValue("poodle");
		t1.incrementValue("yorkie");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		t2.incrementValue("siamese");
		t2.incrementValue("burmese");
		t2.incrementValue("bengal");
		t2.incrementValue("persian");
		
		CosineDistance d = new CosineDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testSymmetric5() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		t1.incrementValue("shepard");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		t2.incrementValue("siamese");
		t2.incrementValue("burmese");
		t2.incrementValue("bengal");
		t2.incrementValue("persian");
		
		CosineDistance d = new CosineDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistance() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		t1.incrementValue("food");
		t1.incrementValue("house");
		t1.incrementValue("walk");
		t1.incrementValue("yard");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		t2.incrementValue("food");
		t2.incrementValue("house");
		t2.incrementValue("sand");
		t2.incrementValue("box");
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0.6));
	}
	
	@Test
	public void testSymmetric6() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.incrementValue("dog");
		t1.incrementValue("food");
		t1.incrementValue("house");
		t1.incrementValue("walk");
		t1.incrementValue("yard");
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.incrementValue("cat");
		t2.incrementValue("food");
		t2.incrementValue("house");
		t2.incrementValue("sand");
		t2.incrementValue("box");
		
		CosineDistance d = new CosineDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		
		Assert.assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistance2() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.setCount("dog", 5);
		t1.setCount("food", 10);
		t1.setCount("house", 3);
		t1.setCount("walk", 2);
		t1.setCount("yard", 1);
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.setCount("cat", 5);
		t2.setCount("food", 10);
		t2.setCount("house", 3);
		t2.setCount("sand", 5);
		t2.setCount("box", 1);
		
		CosineDistance d = new CosineDistance();
		double distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		Assert.assertTrue(isEqual(distance, 0.269098));
	}
	
	@Test
	public void testSymmetric7() {
		BagOfWordsFeature t1 = new BagOfWordsFeature();
		t1.setCount("dog", 5);
		t1.setCount("food", 10);
		t1.setCount("house", 3);
		t1.setCount("walk", 2);
		t1.setCount("yard", 1);
		
		BagOfWordsFeature t2 = new BagOfWordsFeature();
		t2.setCount("cat", 5);
		t2.setCount("food", 10);
		t2.setCount("house", 3);
		t2.setCount("sand", 5);
		t2.setCount("box", 1);
		
		CosineDistance d = new CosineDistance();
		double d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		double d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		
		Assert.assertTrue(isEqual(d1, d2));
	}
}
