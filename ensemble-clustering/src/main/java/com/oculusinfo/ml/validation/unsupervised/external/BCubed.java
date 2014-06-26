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

/***
 * External clustering validation implementation of BCubed
 * 
 * @author slangevin
 *
 */
public class BCubed {
	private double precision = 0, recall = 0, f = 0;
	
	/***
	 * BCubed recall is the proportion of instances with class label are in this cluster.
	 * @param label is the class label we are evaluating
	 * @param cluster that we are evaluating
	 * @param clusters is a collection of all the clusters
	 * @return the proportion of instances with class label that are members of cluster
	 */
	public double recall(String label, Cluster cluster, Collection<Cluster> clusters) {
		double count = 0, total = 0, recall = 0;
		
		// first determine how many instances have class label in this cluster
		for (Instance i : cluster.getMembers()) {
			if (i.hasClassLabel(label)) count++;
		}
		
		// find out how many instances in total have this class label
		for (Cluster c : clusters) {
			for (Instance i : c.getMembers()) {
				if (i.hasClassLabel(label)) total++;
			}
		}
		
		if (total > 0) recall = count / total;
		
		return recall;
	}
	
	/***
	 * BCubed precision is the proportion of instances in a cluster that share the same label.
	 * 
	 * @param label is the class label we are evaluating
	 * @param cluster that we are evaluating
	 * @return the proportion of instances in cluster that have the specified label
	 */
	public double precision(String label, Cluster cluster) {
		double count = 0, precision = 0;
		
		for (Instance i : cluster.getMembers()) {
			if (i.hasClassLabel(label)) count++; 
		}
		
		if (count > 0) precision = count / cluster.getMembers().size(); 
		
		return precision;
	}
	
	public double validate(Collection<Cluster> clusters) {
		int num = 0;
		double p = 0, r = 0;
		
		for (Cluster c : clusters) {
			for (Instance inst : c.getMembers()) {
				p += precision(inst.getClassLabel(), c) ;
				r += recall(inst.getClassLabel(), c, clusters);
				num++;
			}
		}
		
		// calculate average precision and recall
		precision = p / num;
		recall = r / num;
		
		// calculate and return the f score
		f = 2 * precision * recall / (precision + recall);
		
		return f;
	}
	
	public double getPrecision() {
		return precision;
	}
	
	public double getRecall() {
		return recall;
	}
	
	public double getBCubed() {
		return f;
	}
}
