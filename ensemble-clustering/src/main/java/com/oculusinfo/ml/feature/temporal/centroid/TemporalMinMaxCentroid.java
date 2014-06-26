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
package com.oculusinfo.ml.feature.temporal.centroid;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.feature.temporal.TemporalFeature;

/***
 * A Centroid for TemporalFeatures that represents the centroid by the min start and max end date
 * 
 * @author slangevin
 *
 */
public class TemporalMinMaxCentroid implements Centroid<TemporalFeature> {
	private static final long serialVersionUID = 845737125746792593L;
	private String name;
	private long cstart = 0, cend = 0;
	
	@Override
	public void add(TemporalFeature feature) {
		long start = 0, end = 0;
		
		start = feature.getStart().getTime();
		end = feature.getEnd().getTime();
		
		// revise the centroid start and end
		cstart = (cstart == 0 ? start : Math.min(start, cstart));
		cend = (cend == 0 ? end : Math.max(end, cend));
	}
	
	@Override
	public void remove(TemporalFeature feature) {
		// TODO Should give the second smallest start and second largest end to revise
	}

	@Override
	public Collection<TemporalFeature> getAggregatableCentroid () {
	    return Collections.singleton(getCentroid());
	}

	@Override
	public TemporalFeature getCentroid() {
		// create the centroid temporal feature set
		TemporalFeature centroid = new TemporalFeature(name);
		centroid.setValue(new Date(cstart), new Date(cend));
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
	public Class<TemporalFeature> getType() {
		return TemporalFeature.class;
	}

	@Override
	public void reset() {
		cstart = 0;
		cend = 0;
	}
}
