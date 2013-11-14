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

public class FastGeoSpatialCentroid implements Centroid<GeoSpatialFeature> {
    private static final long serialVersionUID = 538283509674357135L;

    private String name;
	private double weight = 0.0;
	private double clat = 0, clon;
	
	@Override
	public void add(GeoSpatialFeature feature) {
	    double addedWeight = feature.getWeight();
	    double newWeight = weight + addedWeight;

	    // incrementally revise the centroid coordinates
		clat = (clat * weight + feature.getLatitude() * addedWeight) / newWeight;  
		clon = (clon * weight + feature.getLongitude() * addedWeight) / newWeight;
		weight = newWeight;
	}
	
	@Override
	public void remove(GeoSpatialFeature feature) {
	    double removedWeight = feature.getWeight();
	    double newWeight = weight - removedWeight;

	    if (weight <= 0.0) {
	        System.out.println("Attempt to remove from empty GeoSpatialCentroid");
	    } else {
	        clat = (clat * weight - feature.getLatitude() * removedWeight) / newWeight;
	        clon = (clon * weight - feature.getLongitude() * removedWeight) / newWeight;
	        weight = newWeight;
	    }
	}

	@Override
	public Collection<GeoSpatialFeature> getAggregatableCentroid () {
	    return Collections.singleton(getCentroid());
	}

	@Override
	public GeoSpatialFeature getCentroid() {
		// create the centroid geospatial feature set
		GeoSpatialFeature centroid = new GeoSpatialFeature(name);
		centroid.setValue( (clat), (clon) );  // return average lat, lon - very crude method of determining centroid for geo
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
		clat = 0;
		clon = 0;
	}
}
