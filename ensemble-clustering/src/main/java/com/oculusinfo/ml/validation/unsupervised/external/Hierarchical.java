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
package com.oculusinfo.ml.validation.unsupervised.external;

import java.util.Collection;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;

public class Hierarchical {
	@SuppressWarnings("unused")
	private double precision = 0, recall = 0, f = 0, alpha = 0.5;

	
	public Hierarchical() {
		this(0.5);  // default to harmonic mean of precision and recall
	}
	
	public Hierarchical(double alpha) {
		this.alpha = alpha;
	}
	
	private class Score {
		public double precision = 0;
		public double recall = 0;
		public double f = 0;
	}
	
	public double getPrecision() {
		return precision;
	}
	
	public double getRecall() {
		return recall;
	}
	
	public double getFMeasure() {
		return f;
	}
	
	public Score bestFmeasure(String label, double labelCount, Collection<? extends Instance> group) {
		Score score = new Score(), best = new Score();
		double lcount = 0, count = 0;
		
		for (Instance i : group) {	
			if (i instanceof Cluster) {
				// calculate stats for this cluster
				lcount += labelCount(label, ((Cluster) i).getMembers());
				count += instanceCount(((Cluster) i).getMembers());
				// calculate child cluster's best f measure
				score = bestFmeasure(label, labelCount, ((Cluster)i).getMembers());
				// update the best f measure found so far
				if (score.f > best.f) best = score;
			}
		}
		// calculate f measure for this cluster
		score.precision = (lcount / count);		// precision
		score.recall = (lcount / labelCount);	// recall
		score.f = 2 * score.precision * score.recall / (score.precision + score.recall);	// f measure
		
		// update the best f measure found
		if (score.f > best.f) {
			best = score;
		}
		
		// return best f measure for this class label
		return best;
	}
	
	public int labelCount(String label, Collection<? extends Instance> group) {
		int count = 0;
		
		for (Instance i : group) {
			if (i instanceof Cluster) {
				count += labelCount(label, ((Cluster)i).getMembers());
			}
			else if (i.hasClassLabel(label)) {
				count++;
			}
		}
		return count;
	}
	
	public int instanceCount(Collection<? extends Instance> group) {
		int count = 0;
		
		for (Instance i : group) {
			if (i instanceof Cluster) {
				count += instanceCount(((Cluster)i).getMembers());
			}
			else {
				count++;
			}
		}
		return count;
	}
	
	public double validate(Collection<Cluster> clusters, Collection<String> labels) {
		double count = instanceCount(clusters);
		
		for (String label : labels) {
			double lcount = labelCount(label, clusters);
			Score best = bestFmeasure(label, lcount, clusters);
			System.out.println(label + " f: " + best.f + " p: " + best.precision + " r: " + best.recall);
			double weight = (lcount / count);
			this.f += weight * best.f;
			this.precision += weight * best.precision;
			this.recall += weight * best.recall;
		}
		return this.f;
	}
}
