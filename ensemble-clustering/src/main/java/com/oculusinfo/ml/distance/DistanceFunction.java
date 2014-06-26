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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/***
 * DistanceFunction is an abstract base class that all Distance functions must extend. 
 * 
 * A distance function calculates a double precision dissimilarity score for a pair of objects or 
 * two collections of objects of the same type.
 * 
 * @author slangevin
 *
 * @param <T>
 */
public abstract class DistanceFunction<T> implements Serializable {
	private static final long serialVersionUID = -3942771973916051670L;
	protected double weight;
	
	public DistanceFunction() {
		this(1);
	}
	
	/***
	 * Each distance function produces a distance score
	 * 
	 * @param weight weight is a scaling factor for the distance score
	 */
	public DistanceFunction(double weight) {
		this.weight = weight;
	}
	
	/**
	 * Return the scaling factor for this distance function
	 * 
	 * @return weight
	 */
	public double getWeight() {
		return weight;
	}
	
	/***
	 * Abstract distance method that all sub-classes must implement.
	 * 
	 * @param x
	 * @param y
	 * @return a double precision dissimilarity score for the pair of objects x, y
	 */
	public abstract double distance(T x, T y);
	
	private double[][] createCoverMatrix(Collection<T> x, Collection<T> y) {
		double cover[][] = new double[x.size()][y.size()];
		
		int i = 0;
		for (T itemx : x) {
			int j = 0;
			for (T itemy : y) {
				cover[i][j] = distance(itemx, itemy);
				j++;
			}
			i++;
		}
		return cover;
	}
	
	/***
	 * Method to calculate the average minimum distance between two collections of objects.
	 * 
	 * @param x
	 * @param y
	 * @return a double precision dissimilarity score
	 */
	public double aveMinDistance(List<T> x, List<T> y) {
		// covermatrix is an expensive operation - it is only necessary when feature collections are greater than 1
		if (x.size() == 1 && y.size() == 1) return distance(x.get(0), y.get(0));
		
		double distance = 0;
		double cover[][] = createCoverMatrix(x, y);
		
		for (int i = 0; i < x.size(); i++) {
			double min = 1;
			for (int j = 0; j < y.size(); j++) {
				if (cover[i][j] < min) min = cover[i][j];
			}
			distance += min;
		}
		for (int j = 0; j < y.size(); j++) {
			double min = 1;
			for (int i = 0; i < x.size(); i++) {
				if (cover[i][j] < min) min = cover[i][j];
			}
			distance += min;
		}
		distance /= (x.size() + y.size());
		
		return distance;
	}
	
	/***
	 * Method to calculate the average maximum distance between two collections of objects.
	 * 
	 * @param x
	 * @param y
	 * @return a double precision dissimilarity score
	 */
	public double aveMaxDistance(List<T> x, List<T> y) {
		// covermatrix is an expensive operation - it is only necessary when feature collections are greater than 1
		if (x.size() == 1 && y.size() == 1) return distance(x.get(0), y.get(0));
		
		double distance = 0;
		double cover[][] = createCoverMatrix(x, y);
		
		for (int i = 0; i < x.size(); i++) {
			double max = 0;
			for (int j = 0; j < y.size(); j++) {
				if (cover[i][j] > max) max = cover[i][j];
			}
			distance += max;
		}
		for (int j = 0; j < y.size(); j++) {
			double max = 0;
			for (int i = 0; i < x.size(); i++) {
				if (cover[i][j] > max) max = cover[i][j];
			}
			distance += max;
		}
		distance /= (x.size() + y.size());
		
		return distance;	
	}
}
