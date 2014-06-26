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
 * A distance function that computes the normalized Haversine (Great Circle) distance between two GeoSpatialFeatures
 * 
 * @author slangevin
 *
 */
public class HaversineDistance extends DistanceFunction<GeoSpatialFeature> {
	private static final long serialVersionUID = -779446291214228343L;
	private final static double EARTH_RADIUS = 6371.00; // Radius in Kilometers default
	
	public HaversineDistance(double weight) {
		super(weight);
	}
	
	@Override
	public double distance(GeoSpatialFeature x, GeoSpatialFeature y) {
		double lat1 = x.getLatitude();
		double lat2 = y.getLatitude();
		double lon1 = x.getLongitude();
		double lon2 = y.getLongitude();
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLng = Math.toRadians(lon2 - lon1);
	    
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng / 2) * Math.sin(dLng / 2);

	    double normDist = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) / Math.PI;
	    return normDist;
	}
	
	public double distanceInKM(GeoSpatialFeature x, GeoSpatialFeature y) {
		double lat1 = x.getLatitude();
		double lat2 = y.getLatitude();
		double lon1 = x.getLongitude();
		double lon2 = y.getLongitude();
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = EARTH_RADIUS * c;
        return dist;
	}
}
