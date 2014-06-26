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
package com.oculusinfo.ml.feature.spatial.distance;

import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.spatial.GeoSpatialFeature;

/***
 * A distance function that computes the equitangular distance between two GeoSpatialFeatures 
 * 
 * @author slangevin
 *
 */
public class EquitangularDistance extends DistanceFunction<GeoSpatialFeature> {
	private static final long serialVersionUID = -1226497733338508060L;
	
	public EquitangularDistance(double weight) {
		super(weight);
	}

	@Override
	public double distance(GeoSpatialFeature x, GeoSpatialFeature y) {
		double lat1 = Math.toRadians(x.getLatitude());
		double lat2 = Math.toRadians(y.getLatitude());
		double lon1 = Math.toRadians(x.getLongitude());
		double lon2 = Math.toRadians(y.getLongitude());
		
		double a = (lon2-lon1) * Math.cos((lat1+lat2)/2);
		double b = (lat2-lat1);
		double d = Math.sqrt(a*a + b*b);
		
		return d;
	}
}
