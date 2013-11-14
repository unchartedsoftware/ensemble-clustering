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

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import org.junit.Test;

import com.oculusinfo.ml.feature.spatial.GeoSpatialFeature;
import com.oculusinfo.ml.feature.spatial.distance.HaversineDistance;

public class TestHaversineDistance {
	
	double epsilon = 0.00001;
	
	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	@Test
	public void testIdenticalPoints() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		double lat = 43.650514, lon = -79.363672;
		t1.setValue(lat, lon);
		
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(lat, lon);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
		assertTrue(isEqual(distance, 0));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		System.out.println(distance);
		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric1() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		double lat = 43.650514, lon = -79.363672;
		t1.setValue(lat, lon);
		
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(lat, lon);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistanceSameCity() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Oculus -79.3636716??W, 43.6505143N
		t1.setValue(43.6505143, -79.3636716);
		
		// Etobicoke -79.5440954??W, 43.6389057??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(43.6389057, -79.5440954);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
		System.out.println(distance);
		assertTrue(isEqual(distance, 7.281985816507403E-4));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, 7.281985816507403E-4));
	}
	
	@Test
	public void testSymmetric2() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Oculus -79.3636716??W, 43.6505143N
		t1.setValue(43.6505143, -79.3636716);
		
		// Etobicoke -79.5440954??W, 43.6389057??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(43.6389057, -79.5440954);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistanceSameProvince() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Toronto -79.3831843??W, 43.6532260??N
		t1.setValue(43.6532260, -79.3831843);
		
		// Waterloo -80.5204096??W, 43.4642578??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(43.4642578, -80.5204096);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
		System.out.println(distance);
//		assertTrue(isEqual(distance, 0));
//		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
//		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric3() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Toronto -79.3831843??W, 43.6532260??N
		t1.setValue(43.6532260, -79.3831843);
		
		// Waterloo -80.5204096??W, 43.4642578??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(43.4642578, -80.5204096);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistanceDifferentProvinces() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Ontario -85.3232139??W, 51.2537750??N
		t1.setValue(51.2537750,  -85.3232139);
		
		// BC -127.6476206??W, 53.7266683??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(53.7266683, -127.6476206);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
		System.out.println(distance);
//		assertTrue(isEqual(distance, 0));
//		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
//		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric4() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Ontario -85.3232139??W, 51.2537750??N
		t1.setValue(51.2537750,  -85.3232139);
		
		// BC -127.6476206??W, 53.7266683??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(53.7266683, -127.6476206);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistanceDifferentContintent() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Canada -106.3467710??W, 56.1303660??N
		t1.setValue(56.1303660, -106.3467710);
		
		// Africa 34.5085230??E, -8.7831950??S
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(-8.7831950, 34.5085230);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
		System.out.println(distance);
//		assertTrue(isEqual(distance, 0));
//		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
//		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric5() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Canada -106.3467710??W, 56.1303660??N
		t1.setValue(56.1303660, -106.3467710);
		
		// Africa 34.5085230??E, -8.7831950??S
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(-8.7831950, 34.5085230);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistanceNearPoles() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Antarctica -135.0000000??W, -82.8627519??S
		t1.setValue(-82.8627519, -135.0000000);
		
		// Arctic 45??E, 82.8627519??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(82.8627519, 45);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
		System.out.println(distance);
//		assertTrue(isEqual(distance, 0));
//		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
//		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric6() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// Antarctica -135.0000000??W, -82.8627519??S
		t1.setValue(-82.8627519, -135.0000000);
		
		// Arctic 45??E, 82.8627519??N
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(82.8627519, 45);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDistanceOppposite() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// -106.3467710??W, 56.1303660??N
		t1.setValue(56.1303660, -106.3467710);
		
		// 73.653229??E, -56.130366??S
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(-56.130366, 73.653229);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
		System.out.println(distance);
//		assertTrue(isEqual(distance, 0));
//		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
//		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric7() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// -106.3467710??W, 56.1303660??N
		t1.setValue(56.1303660, -106.3467710);
		
		// 73.653229??E, -56.130366??S
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(-56.130366, 73.653229);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testTiming() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// -106.3467710??W, 56.1303660??N
		t1.setValue(56.1303660, -106.3467710);
		
		// 73.653229??E, -56.130366??S
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(-56.130366, 73.653229);
		
		HaversineDistance d = new HaversineDistance(1);
//		EuclideanDistance d = new EuclideanDistance(1);
		
		long start = System.currentTimeMillis();
		double distance = 0;
		for (int i=0; i < 30000*3000; i++) {
//			distance = d.distance(t1, t2);
			distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		}
		double distanceTime = System.currentTimeMillis() - start;
		System.out.println("Time: " + distanceTime/1000);
	}
	
	
	@SuppressWarnings("unused")
	@Test
	public void testDistance() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// -73.995008, 40.752842
		t1.setValue(-73.995008, 40.752842);
		
		// -73.994905, 40.752798
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(-73.994905, 40.752798);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double distance = d.distance(t1, t2);
//		assertTrue(isEqual(distance, 0));
//		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
//		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric8() {
		GeoSpatialFeature t1 = new GeoSpatialFeature();
		// -73.995008, 40.752842
		t1.setValue(-73.995008, 40.752842);
		
		// -73.994905, 40.752798
		GeoSpatialFeature t2 = new GeoSpatialFeature();
		t2.setValue(-73.994905, 40.752798);
		
		HaversineDistance d = new HaversineDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
}
