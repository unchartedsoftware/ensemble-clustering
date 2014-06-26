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
package com.oculusinfo.ml.feature.bagofwords.distance;

import java.util.Collection;

import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.bagofwords.BagOfWordsFeature;
import com.oculusinfo.ml.stats.FeatureFrequency;

/**
 * A distance function that computes the normalized Levenshtein (edit) distance between two BagOfWordFeatures.
 * 
 * The distance is computed by summing the minimum edit distance for each word in the BagOfWordFeatures 
 * divided by the number of words
 * 
 * @author slangevin
 *
 */
public class EditDistance extends DistanceFunction<BagOfWordsFeature> {
	private static final long serialVersionUID = -1270784860823146795L;

	public EditDistance() {
		super(1);
	}
	
	public EditDistance(double weight) {
		super(weight);
	}
	
	@Override
	public double distance(BagOfWordsFeature x, BagOfWordsFeature y) {
		double dist = 0;
		
		Collection<FeatureFrequency> xWords = x.getValues();
		Collection<FeatureFrequency> yWords = y.getValues();
		int m = xWords.size();
		int n = yWords.size();
		double norm = Math.max(m, n);
		
		// set a to be the largest nominal list
		Collection<FeatureFrequency> a = xWords, b = yWords;
		if (m < n) {
			a = yWords;
			b = xWords;
		}
		
		for (FeatureFrequency xf : a) {
			double best = 1.0;
			for (FeatureFrequency yf : b) {
				double d = getNormLevenshteinDistance(xf.feature.getName(), yf.feature.getName());
				if (d < best) best = d;
			}
			dist += best;
		}
	
		return dist / norm;
	}
	
	// Levenshtein Distance Algorithm based on implementation from Apache Jakarta Commons Project, 
	// implementation from here:  http://www.merriampark.com/ldjava.htm
	public static double getNormLevenshteinDistance(String s, String t) {
		if (s == null || t == null) throw new IllegalArgumentException("Strings must not be null");
				
		/*
	     	The difference between this impl. and the previous is that, rather 
	     	than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
	     	we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
	     	is the 'current working' distance array that maintains the newest distance cost
	     	counts as we iterate through the characters of String s.  Each time we increment
	     	the index of String t we are comparing, d is copied to p, the second int[].  Doing so
	     	allows us to retain the previous cost counts as required by the algorithm (taking 
	     	the minimum of the cost count to the left, up one, and diagonally up and to the left
	     	of the current cost count being calculated).  (Note that the arrays aren't really 
	     	copied anymore, just switched...this is clearly much better than cloning an array 
	     	or doing a System.arraycopy() each time  through the outer loop.)

	     	Effectively, the difference between the two implementations is this one does not 
	     	cause an out of memory condition when calculating the LD over two very large strings.  		
		 */		
			
		int n = s.length(); // length of s
		int m = t.length(); // length of t
			
		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int p[] = new int[n+1]; //'previous' cost array, horizontally
		int d[] = new int[n+1]; // cost array, horizontally
		int _d[]; //placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i<=n; i++) {
			p[i] = i;
		}
			
		for (j = 1; j<=m; j++) {
			t_j = t.charAt(j-1);
			d[0] = j;
			
			for (i=1; i<=n; i++) {
				cost = s.charAt(i-1)==t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left and up +cost				
				d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);  
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		} 
			
		// our last action in the above loop was to switch d and p, so p now 
		// actually has the most recent cost counts
		// distance is normalized to [0, 1]
		return ( (double) p[n] / Math.max(n, m) );
	}	
}
