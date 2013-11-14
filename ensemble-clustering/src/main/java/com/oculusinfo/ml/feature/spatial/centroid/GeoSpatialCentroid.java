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
package com.oculusinfo.ml.feature.spatial.centroid;

import java.util.Collection;
import java.util.Collections;

import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.feature.spatial.GeoSpatialFeature;

public class GeoSpatialCentroid implements Centroid<GeoSpatialFeature> {
	private static final long serialVersionUID = -5695057485949376693L;
	private String name;
	private double weight = 0.0;
	private double cx = 0, cy = 0, cz = 0;
	
	@Override
	public void add(GeoSpatialFeature feature) {
	    double addedWeight = feature.getWeight();
		double x = 0, y = 0, z = 0, lat = 0, lon = 0;
		
		// convert lat/lon to cartesian coordinates
		lat = Math.toRadians(feature.getLatitude());
		lon = Math.toRadians(feature.getLongitude());
		
		x = Math.sin(lat) * Math.cos(lon);
		y = Math.sin(lat) * Math.sin(lon);
		z = Math.cos(lat);
		
		// Increase the weight of this centroid according to what was added.
		weight += addedWeight;
		
		// revise the centroid cartesian coordinates
		cx += x * addedWeight;
		cy += y * addedWeight;
		cz += z * addedWeight;
	}
	
	@Override
	public void remove(GeoSpatialFeature feature) {
	    double removedWeight = feature.getWeight();
		double x = 0, y = 0, z = 0, lat = 0, lon = 0;
		
		// convert lat/lon to cartesian coordinates
		lat = Math.toRadians(feature.getLatitude());
		lon = Math.toRadians(feature.getLongitude());
		
		x = Math.sin(lat) * Math.cos(lon);
		y = Math.sin(lat) * Math.sin(lon);
		z = Math.cos(lat);
		
		// decrement the centroid cartesian coordinates
		cx -= x * removedWeight;
		cy -= y * removedWeight;
		cz -= z * removedWeight;

		// Decrease the weight of this centroid according to what was removed.
		weight = weight - removedWeight;
	}


    @Override
    public Collection<GeoSpatialFeature> getAggregatableCentroid () {
        return Collections.singleton(getCentroid());
    }

	@Override
	public GeoSpatialFeature getCentroid() {
		double lat = 0, lon = 0, hyp = 0;
		
		// calculate average x,y,z coords
		double ax = cx/weight, ay = cy/weight, az = cz/weight;
		
		// convert the centroid cartesian coordinates to lat/lon		
		lon = Math.toDegrees(Math.atan2(ay, ax));
		hyp = Math.sqrt(ax * ax + ay * ay);
		lat = Math.toDegrees(Math.atan2(hyp, az));
		
		// create the centroid geospatial feature set
		GeoSpatialFeature centroid = new GeoSpatialFeature(name);
		centroid.setValue(lat, lon);
		centroid.setWeight(weight);
	
		return centroid;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class<GeoSpatialFeature> getType() {
		return GeoSpatialFeature.class;
	}

	@Override
	public void reset() {
		weight = 0;
		cx = 0;
		cy = 0;
		cz = 0;
	}
}
