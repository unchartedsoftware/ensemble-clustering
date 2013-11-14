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
package com.oculusinfo.ml.stats;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.oculusinfo.ml.feature.Feature;

public class FeatureFrequencyTable implements Serializable {
	private static final long serialVersionUID = 2702669296496944069L;
	private Map<String, FeatureFrequency> table = new HashMap<String, FeatureFrequency>();
	
	public FeatureFrequencyTable() { }
	
	public boolean containsFeature(Feature feature) {
		return table.containsKey(feature.getId());
	}
	
	public FeatureFrequency remove(Feature feature) {
		return table.remove(feature.getId());
	}
	
	public FeatureFrequency remove(FeatureFrequency freq) {
		return table.remove(freq.feature.getId());
	}
	
	public FeatureFrequency add(FeatureFrequency freq) {
		if (containsFeature(freq.feature) == false) {
			FeatureFrequency f = new FeatureFrequency(freq.feature);
			f.frequency = freq.frequency;
			return table.put(freq.feature.getId(), f);
		}
		return incrementBy(freq.feature, freq.frequency);
	}
	
	public FeatureFrequency add(Feature feature) {
		if (containsFeature(feature) == false) {
			return table.put(feature.getId(), new FeatureFrequency(feature));
		}
		return increment(feature);
	}
	
	public FeatureFrequency get(Feature feature) {
		return table.get(feature.getId());
	}
	
	public void clear() {
		table.clear();
	}
	
	@JsonIgnore
	public Collection<FeatureFrequency> getAll() {
		return table.values();
	}
	
	public FeatureFrequency incrementBy(Feature feature, int increment) {
		FeatureFrequency freq = null;
		if (containsFeature(feature)) {
			freq = get(feature);
			freq.frequency += increment;
		}
		return freq;
	}
	
	public FeatureFrequency increment(Feature feature) {
		return incrementBy(feature, 1);
	}
	
	public FeatureFrequency decrementBy(Feature feature, int decrement) {
		FeatureFrequency freq = null;
		if (containsFeature(feature)) {
			freq = get(feature);
			freq.frequency-= decrement;
			if (freq.frequency < 0) freq.frequency = 0;
		}
		return freq;
	}
	
	public FeatureFrequency decrement(Feature feature) {
		return decrementBy(feature, 1);
	}
	
	public Map<String, FeatureFrequency> getTable() {
		return table;
	}
	
	public void setTable(Map<String, FeatureFrequency> table) {
		this.table = table;
	}
	
	public Collection<FeatureFrequency> getTopN(int n) {
		Collection<FeatureFrequency> topN = new LinkedList<FeatureFrequency>();
		PriorityQueue<FeatureFrequency> freq = new PriorityQueue<FeatureFrequency>(11, new FeatureFreqComparator());
		
		// sort features by decreasing frequency
		for (FeatureFrequency f : table.values()) {
			freq.add(f);
		}
		
		// return the first n elements in PQ
		for (int i=0; i < n; i++) {
			if (freq.isEmpty()) break;
			topN.add(freq.poll());
		}
		return topN;
	}
}
