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

public class EuclideanDistance extends DistanceFunction<GeoSpatialFeature> {
	private static final long serialVersionUID = -123522038033912391L;
	private final static double EARTH_RADIUS = 6371.00; // Radius in Kilometers default
	private final static double normConst = 1 / Math.sqrt(Math.pow(360, 2) + Math.pow(180, 2)); // 1 / 12742.0;
		
	public EuclideanDistance(double weight) {
		super(weight);
	}
	
	@Override
	public double distance(GeoSpatialFeature x, GeoSpatialFeature y) {
		double lat1 = x.getLatitude();
		double lat2 = y.getLatitude();
		double lon1 = x.getLongitude();
		double lon2 = y.getLongitude();
		
		// return normalized euclidean distance [0,1]
		return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2)) * normConst;
	}
	
	public double distanceInCartesianPlane(GeoSpatialFeature x, GeoSpatialFeature y) {
		double lat1 = Math.toRadians(x.getLatitude());
		double lat2 = Math.toRadians(y.getLatitude());
		double lon1 = Math.toRadians(x.getLongitude());
		double lon2 = Math.toRadians(y.getLongitude());
		double x1 = EARTH_RADIUS * Math.cos(lat1) * Math.cos(lon1);
		double x2 = EARTH_RADIUS * Math.cos(lat2) * Math.cos(lon2);
		double y1 = EARTH_RADIUS * Math.cos(lat1) * Math.sin(lon2);
		double y2 = EARTH_RADIUS * Math.cos(lat1) * Math.sin(lon2);
		double z1 = EARTH_RADIUS * Math.sin(lat1);
		double z2 = EARTH_RADIUS * Math.sin(lat2);
		
		// return normalized euclidean distance [0,1]
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2)) * normConst;
	}
}
