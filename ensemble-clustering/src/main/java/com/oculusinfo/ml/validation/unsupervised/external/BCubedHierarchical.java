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
import java.util.LinkedList;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;

/***
 * An external hierarchical clustering validation implementation of BCubed
 * @author slangevin
 *
 */
public class BCubedHierarchical {
	private double precision = 0, recall = 0, f = 0;

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public double getF() {
		return f;
	}
	
	private boolean contains(Instance inst, Cluster c) {
		for (Instance i : c.getMembers()) {
			if (i instanceof Cluster) {
				if ( contains(inst, (Cluster)i) ) return true;
			}
			else if (i == inst) return true;
 		}
		return false;
	}
	
	private Collection<Cluster> getClusters(Instance inst, Collection<? extends Instance> clusters) {
		Collection<Cluster> iclusters = new LinkedList<Cluster>();
		
		for (Instance i : clusters) {
			Cluster c = (Cluster)i;
			if (contains(inst, c)) iclusters.add(c);
		}
		return iclusters;
	}
	
	public double avePrecision(Instance inst, Collection<Cluster> clusters) {
		double p = 0;
		
		Collection<Cluster> iclusters = getClusters(inst, clusters);
		
		for (Cluster c : iclusters) {
			p += (double)labelCount(inst.getClassLabel(), c.getMembers()) / instanceCount(c.getMembers());
		}
		return (p / iclusters.size());
	}
	
	public double aveRecall(Instance inst, Collection<Cluster> clusters) {
		double r = 0, labelCount = labelCount(inst.getClassLabel(), clusters);
		
		Collection<Cluster> iclusters = getClusters(inst, clusters);
		
		for (Cluster c : iclusters) {
			r += (double)labelCount(inst.getClassLabel(), c.getMembers()) / labelCount;
		}
		
		// count will never be zero since we include inst itself
		return (r / iclusters.size());
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

	public double validate(DataSet ds, Collection<Cluster> clusters) {
		double p = 0, r = 0, count = ds.size();
		
		for (Instance inst : ds) {
			p += avePrecision(inst, clusters);
			r += aveRecall(inst, clusters);
		}
		
		precision = p / count;
		recall = r / count;
		
		f = 2 * precision * recall / (precision + recall);
		
		return f;
	}
}
