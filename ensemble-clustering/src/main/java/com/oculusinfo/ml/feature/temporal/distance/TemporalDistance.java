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
package com.oculusinfo.ml.feature.temporal.distance;

import java.util.Date;

import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.temporal.TemporalFeature;

public class TemporalDistance extends DistanceFunction<TemporalFeature> {
	private static final long serialVersionUID = 1910227375920657644L;
	private final static double MS_PER_DAY = 86400000;
	
	public TemporalDistance(double weight) {
		super(weight);
	}
	
	@Override
	public double distance(TemporalFeature x, TemporalFeature y) {
		Date start1 = x.getStart();
		Date start2 = y.getStart();
		Date end1 = x.getEnd();
		Date end2 = y.getEnd();
		
		Date s = null, e = null;
		
		// TODO sanity check inputs to make sure: start1 <= end1 and start2 <= end2
		
		// check if the date intervals overlap - return 1 if they don't
		if (start2.after(end1) || start1.after(end2)) return 1;
		
		// these are identical time regions
		if (start1.equals(start2) && end1.equals(end2)) return 0;
		
		// calculate the overlapping interval [s,e]
		s = (start2.after(start1) ? start2 : start1);
		e = (end2.before(end1) ? end2 : end1);
		
		// normalize distance
		double normDist = 1 - (2 * durationInMS(s, e) / (durationInMS(start1, end1) + durationInMS(start2, end2)));
		return normDist;
	}
	
	private double durationInMS(Date start, Date end) {
		double duration = end.getTime() - start.getTime();
		// handle edge case so single points are not zero
		return (duration == 0 ? MS_PER_DAY : duration); 
	}

}
