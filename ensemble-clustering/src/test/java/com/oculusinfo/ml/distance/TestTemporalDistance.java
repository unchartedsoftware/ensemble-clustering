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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.oculusinfo.ml.feature.temporal.TemporalFeature;
import com.oculusinfo.ml.feature.temporal.distance.TemporalDistance;


public class TestTemporalDistance {

	long MS_PER_DAY = 86400000;
	long MS_PER_WEEK = MS_PER_DAY * 7;
	long MS_PER_MONTH = MS_PER_DAY * 30;
	long MS_PER_YEAR = MS_PER_DAY * 365;
	
	double epsilon = 0.00001;
	
	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	@Test
	public void testIdenticalPoints() {
		Date date = (new GregorianCalendar(2010, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date, date);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date, date);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		assertTrue(isEqual(distance, 0));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric1() {
		Date date = (new GregorianCalendar(2010, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date, date);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date, date);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDisjointYearPoints() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2011, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date1);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date2, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		assertTrue(isEqual(distance, 1));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, 1));
	}
	
	@Test
	public void testSymmetric2() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2011, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date1);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date2, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDisjointMonthPoints() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2010, 02, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date1);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date2, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		assertTrue(isEqual(distance, 1));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, 1));
	}
	
	@Test
	public void testSymmetric3() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2010, 02, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date1);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date2, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDisjointDayPoints() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2010, 01, 02)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date1);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date2, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		assertTrue(isEqual(distance, 1));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, 1));
	}
	
	@Test
	public void testSymmetric4() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2010, 01, 02)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date1);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date2, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testIdenticalRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2011, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date1, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		assertTrue(isEqual(distance, 0));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, 0));
	}
	
	@Test
	public void testSymmetric5() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2011, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date1, date2);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testDisjointRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2011, 01, 01)).getTime();
		
		Date date3 = (new GregorianCalendar(2012, 01, 01)).getTime();
		Date date4 = (new GregorianCalendar(2013, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		assertTrue(isEqual(distance, 1));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, 1));
	}
	
	@Test
	public void testSymmetric6() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = (new GregorianCalendar(2011, 01, 01)).getTime();
		
		Date date3 = (new GregorianCalendar(2012, 01, 01)).getTime();
		Date date4 = (new GregorianCalendar(2013, 01, 01)).getTime();
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByDayInYearRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = date2;
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * MS_PER_DAY / (2.0 * MS_PER_YEAR));
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric7() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = date2;
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByDayInMonthRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = date2;
		Date date4 = new Date( date3.getTime() + MS_PER_MONTH );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * MS_PER_DAY / (2.0 * MS_PER_MONTH));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric8() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = date2;
		Date date4 = new Date( date3.getTime() + MS_PER_MONTH );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByDayInWeekRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_WEEK );
		
		Date date3 = date2;
		Date date4 = new Date( date3.getTime() + MS_PER_WEEK );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * MS_PER_DAY / (2.0 * MS_PER_WEEK));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric9() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_WEEK );
		
		Date date3 = date2;
		Date date4 = new Date( date3.getTime() + MS_PER_WEEK );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByWeekInYearRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - MS_PER_WEEK );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * MS_PER_WEEK / (2.0 * MS_PER_YEAR));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric10() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - MS_PER_WEEK );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByWeekInMonthRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = new Date( date2.getTime() - MS_PER_WEEK );
		Date date4 = new Date( date3.getTime() + MS_PER_MONTH );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * MS_PER_WEEK / (2.0 * MS_PER_MONTH));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric11() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = new Date( date2.getTime() - MS_PER_WEEK );
		Date date4 = new Date( date3.getTime() + MS_PER_MONTH );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByMonthInYearRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - MS_PER_MONTH );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * MS_PER_MONTH / (2.0 * MS_PER_YEAR));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric12() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - MS_PER_MONTH );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByFourMonthsInYearRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - 4 * MS_PER_MONTH );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * 4 * MS_PER_MONTH / (2.0 * MS_PER_YEAR));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric13() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - 4 * MS_PER_MONTH );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlappingByTenMonthsInYearRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - 10 * MS_PER_MONTH );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * 10 * MS_PER_MONTH / (2.0 * MS_PER_YEAR));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric14() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date3 = new Date( date2.getTime() - 10 * MS_PER_MONTH );
		Date date4 = new Date( date3.getTime() + MS_PER_YEAR );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testTotalCoverSixDaysInRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = new Date( date2.getTime() - 6 * MS_PER_DAY );
		Date date4 = new Date( date3.getTime() + 6 * MS_PER_DAY );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double distance = d.distance(t1, t2);
		double expected = 1.0 - (2.0 * 6 * MS_PER_DAY / (MS_PER_MONTH + 6 * MS_PER_DAY));
		
		assertTrue(isEqual(distance, expected));
		distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric15() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = new Date( date2.getTime() - 6 * MS_PER_DAY );
		Date date4 = new Date( date3.getTime() + 6 * MS_PER_DAY );
		
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		TemporalDistance d = new TemporalDistance(1);
		
		double d1 = d.distance(t1, t2);
		double d2 = d.distance(t2, t1);
		
		assertTrue(isEqual(d1, d2));
		d1 = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		d2 = d.aveMinDistance(Collections.singletonList(t2), Collections.singletonList(t1));
		assertTrue(isEqual(d1, d2));
	}
	
	@Test
	public void testOverlapEqualNumMultipleRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = (new GregorianCalendar(2011, 01, 01)).getTime();
		Date date4 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date5 = new Date( date2.getTime() - 6 * MS_PER_DAY );
		Date date6 = new Date( date5.getTime() + 6 * MS_PER_DAY );
		
		Date date7 = new Date( date4.getTime() - MS_PER_MONTH );
		Date date8 = new Date( date7.getTime() + 6 * MS_PER_MONTH );
		
		// Two identical points in time
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		List<TemporalFeature> set1 = new LinkedList<TemporalFeature>();
		set1.add(t1);
		set1.add(t2);
		
		TemporalFeature t3 = new TemporalFeature();
		t3.setValue(date5, date6);
		
		TemporalFeature t4 = new TemporalFeature();
		t4.setValue(date7, date8);
		
		List<TemporalFeature> set2 = new LinkedList<TemporalFeature>();
		set2.add(t3);
		set2.add(t4);
		
		TemporalDistance d = new TemporalDistance(1);

		double expected = 0.8278084714548803;
		double distance = d.aveMinDistance(set1, set2);
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric16() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = (new GregorianCalendar(2011, 01, 01)).getTime();
		Date date4 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date5 = new Date( date2.getTime() - 6 * MS_PER_DAY );
		Date date6 = new Date( date5.getTime() + 6 * MS_PER_DAY );
		
		Date date7 = new Date( date4.getTime() - MS_PER_MONTH );
		Date date8 = new Date( date7.getTime() + 6 * MS_PER_MONTH );
		
		// Two identical points in time
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		List<TemporalFeature> set1 = new LinkedList<TemporalFeature>();
		set1.add(t1);
		set1.add(t2);
		
		TemporalFeature t3 = new TemporalFeature();
		t3.setValue(date5, date6);
		
		TemporalFeature t4 = new TemporalFeature();
		t4.setValue(date7, date8);
		
		List<TemporalFeature> set2 = new LinkedList<TemporalFeature>();
		set2.add(t3);
		set2.add(t4);
		
		TemporalDistance d = new TemporalDistance(1);

		double d1 = d.aveMinDistance(set1, set2);
		double d2 = d.aveMinDistance(set2, set1);
		assertTrue(isEqual(d1, d2));
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testOverlapUnEqualNumMultipleRegions() {
		Date date1 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = (new GregorianCalendar(2011, 01, 01)).getTime();
		Date date4 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date5 = new Date( date2.getTime() - 6 * MS_PER_DAY );
		Date date6 = new Date( date5.getTime() + 6 * MS_PER_DAY );
		
		Date date7 = new Date( date4.getTime() - MS_PER_MONTH );
		Date date8 = new Date( date7.getTime() + 6 * MS_PER_MONTH );
		
		Date date9 = (new GregorianCalendar(2008, 01, 01)).getTime();
		Date date10 = new Date( date9.getTime() + MS_PER_MONTH );
		
		// Two identical points in time
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		Collection<TemporalFeature> set1 = new LinkedList<TemporalFeature>();
		set1.add(t1);
		set1.add(t2);
		
		TemporalFeature t3 = new TemporalFeature();
		t3.setValue(date5, date6);
		
		TemporalFeature t4 = new TemporalFeature();
		t4.setValue(date7, date8);
		
		TemporalFeature t5 = new TemporalFeature();
		t5.setValue(date9, date10);
		
		Collection<TemporalFeature> set2 = new LinkedList<TemporalFeature>();
		set2.add(t3);
		set2.add(t4);
		set2.add(t5);
		
		TemporalDistance d = new TemporalDistance(1);

		double expected = 0.8622467771639043;
		long start = System.currentTimeMillis();
		double distance = 0;
		for (int i=0; i < 300000*30000; i++) {
//			distance = d.distance(t1, t2);
//			distance = d.aveMinDistance(set1, set2);
			distance = d.aveMinDistance(Collections.singletonList(t1), Collections.singletonList(t2));
		}
		double distanceTime = System.currentTimeMillis() - start;
		System.out.println("Time: " + distanceTime/1000);
		
//		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testDisjointUnEqualNumMultipleRegions() {
		Date date1 = (new GregorianCalendar(2007, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = (new GregorianCalendar(2008, 01, 01)).getTime();
		Date date4 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date5 = (new GregorianCalendar(2009, 01, 01)).getTime();
		Date date6 = new Date( date5.getTime() + 6 * MS_PER_DAY );
		
		Date date7 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date8 = new Date( date7.getTime() + 6 * MS_PER_MONTH );
		
		Date date9 = (new GregorianCalendar(2011, 01, 01)).getTime();
		Date date10 = new Date( date9.getTime() + MS_PER_MONTH );
		
		// Two identical points in time
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		List<TemporalFeature> set1 = new LinkedList<TemporalFeature>();
		set1.add(t1);
		set1.add(t2);
		
		TemporalFeature t3 = new TemporalFeature();
		t3.setValue(date5, date6);
		
		TemporalFeature t4 = new TemporalFeature();
		t4.setValue(date7, date8);
		
		TemporalFeature t5 = new TemporalFeature();
		t5.setValue(date9, date10);
		
		List<TemporalFeature> set2 = new LinkedList<TemporalFeature>();
		set2.add(t3);
		set2.add(t4);
		set2.add(t5);
		
		TemporalDistance d = new TemporalDistance(1);

		double expected = 1;
		double distance = d.aveMinDistance(set1, set2);
		assertTrue(isEqual(distance, expected));
	}
	
	@Test
	public void testSymmetric17() {
		Date date1 = (new GregorianCalendar(2007, 01, 01)).getTime();
		Date date2 = new Date( date1.getTime() + MS_PER_MONTH );
		
		Date date3 = (new GregorianCalendar(2008, 01, 01)).getTime();
		Date date4 = new Date( date1.getTime() + MS_PER_YEAR );
		
		Date date5 = (new GregorianCalendar(2009, 01, 01)).getTime();
		Date date6 = new Date( date5.getTime() + 6 * MS_PER_DAY );
		
		Date date7 = (new GregorianCalendar(2010, 01, 01)).getTime();
		Date date8 = new Date( date7.getTime() + 6 * MS_PER_MONTH );
		
		Date date9 = (new GregorianCalendar(2011, 01, 01)).getTime();
		Date date10 = new Date( date9.getTime() + MS_PER_MONTH );
		
		// Two identical points in time
		TemporalFeature t1 = new TemporalFeature();
		t1.setValue(date1, date2);
		
		TemporalFeature t2 = new TemporalFeature();
		t2.setValue(date3, date4);
		
		List<TemporalFeature> set1 = new LinkedList<TemporalFeature>();
		set1.add(t1);
		set1.add(t2);
		
		TemporalFeature t3 = new TemporalFeature();
		t3.setValue(date5, date6);
		
		TemporalFeature t4 = new TemporalFeature();
		t4.setValue(date7, date8);
		
		TemporalFeature t5 = new TemporalFeature();
		t5.setValue(date9, date10);
		
		List<TemporalFeature> set2 = new LinkedList<TemporalFeature>();
		set2.add(t3);
		set2.add(t4);
		set2.add(t5);
		
		TemporalDistance d = new TemporalDistance(1);

		double d1 = d.aveMinDistance(set1, set2);
		double d2 = d.aveMinDistance(set2, set1);
		assertTrue(isEqual(d1, d2));
	}
}
