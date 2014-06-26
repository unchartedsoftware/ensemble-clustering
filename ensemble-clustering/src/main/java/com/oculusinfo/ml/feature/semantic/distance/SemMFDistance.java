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
package com.oculusinfo.ml.feature.semantic.distance;

import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.semantic.SemanticFeature;

/***
 * A distance function that computes the distance between two SemanticFeatures
 * 
 * In order to use this distance function you must provide a taxonomy that describes the Semantic Class hierarchy as a tree
 * 
 * The distance calculation finds the lowest common ancestor between the two SemanticFeatures and computes
 * the distance using the algorithm described in:
 * 
 * Oldakowski, R. and Bizer, C., SemMF: a framework for calculating semantic similarity of objects represented as RDF graphs. 4th International Semantic Web Conference (ISWC 2005).
 * 
 * @author slangevin
 *
 */
public class SemMFDistance extends DistanceFunction<SemanticFeature> {
	private static final long serialVersionUID = -7485093350764491674L;
	private Concept taxonomy;
	
	public SemMFDistance(Concept taxonomy) {
		this(taxonomy, 1);
	}
	
	public SemMFDistance(Concept taxonomy, double weight) {
		super(weight);
		this.taxonomy = taxonomy;
	}
	
	private double distance(Concept x, Concept y) {
		int xlvl = x.getDepth();
		int ylvl = y.getDepth();
		return 0.5 / Math.pow(2, ylvl) -  0.5 / Math.pow(2, xlvl);
	}
	
	@Override
	public double distance(SemanticFeature x, SemanticFeature y) {
		double dist = 1;

		Concept cx = taxonomy.findConcept(x.getConcept());
		Concept cy = taxonomy.findConcept(y.getConcept());
		Concept lca = cx.findCommonAncestor(cy);
		
		// No common ancestor exists - return max distance
		if (lca != null) {
			dist = distance(cx, lca) + distance(cy, lca);
		}
		
		return dist;
	}

}
