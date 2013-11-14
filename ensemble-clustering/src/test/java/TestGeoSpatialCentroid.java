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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.oculusinfo.ml.feature.spatial.GeoSpatialFeature;
import com.oculusinfo.ml.feature.spatial.centroid.GeoSpatialCentroid;


public class TestGeoSpatialCentroid {

	double epsilon = 0.00001;
	
	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	@Test
	public void testAddOne() {
		GeoSpatialFeature f = new GeoSpatialFeature("f1");
		f.setValue(10, 50);
		GeoSpatialCentroid centroid = new GeoSpatialCentroid();
		centroid.add(f);
		f = centroid.getCentroid();
		System.out.println(f);
		assertTrue(isEqual(f.getLatitude(), 10));
		assertTrue(isEqual(f.getLongitude(), 50));
	}
	
	@Test
	public void testAddOne2() {
		GeoSpatialFeature f = new GeoSpatialFeature("f1");
		f.setValue(37.68455,-97.34110);
		GeoSpatialCentroid centroid = new GeoSpatialCentroid();
		centroid.add(f);
		f = centroid.getCentroid();
		System.out.println(f);
		assertTrue(isEqual(f.getLatitude(), 37.68455));
		assertTrue(isEqual(f.getLongitude(), -97.34110));
	}
	
	@Test
	public void testAddOne3() {
		GeoSpatialFeature f = new GeoSpatialFeature("f1");
		f.setValue(-89,80);
		GeoSpatialCentroid centroid = new GeoSpatialCentroid();
		centroid.add(f);
		f = centroid.getCentroid();
		System.out.println(f);
		assertTrue(isEqual(f.getLatitude(), 89));
		assertTrue(isEqual(f.getLongitude(), -100));
	}

}
