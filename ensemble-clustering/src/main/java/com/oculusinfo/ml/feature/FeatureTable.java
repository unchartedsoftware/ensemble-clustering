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
package com.oculusinfo.ml.feature;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

public class FeatureTable implements Serializable {
	private static final long serialVersionUID = -5842770909462107078L;
	private Map<String, List<Feature>> table = new HashMap<String, List<Feature>>();
	
	public FeatureTable() { super(); }
	
	public boolean containsFeature(String name) {
		return table.containsKey(name);
	}
	
	public void addFeature(Feature f) {
		if (table.containsKey(f.getName()) == false) {
			table.put(f.getName(), new LinkedList<Feature>());
		}
		Collection<Feature> featureList = table.get(f.getName());
		featureList.add(f);
	}
	
	public void addFeatures(Collection<Feature> features) {
		for (Feature f : features) {
			addFeature(f);
		}
	}
	
	public void addFeatures(FeatureTable copyTable) {
		for (Collection<Feature> list : copyTable.getFeatures()) {
			addFeatures(list);
		}
	}
	
	public List<Feature> removeFeature(String name) {
		return table.remove(name);
	}
	
	public List<Feature> getFeature(String name) {
		return table.get(name);
	}
	
	@JsonIgnore
	public Collection<String> getFeatureNames() {
		return table.keySet();
	}
	
	public Map<String, List<Feature>> getTable() {
		return table;
	}
	
	public void setTable(Map<String, List<Feature>> table) {
		this.table = table;
	}
	
	public int numFeatures() {
		return table.size();
	}
	
	@JsonIgnore
	public Collection<List<Feature>> getFeatures() {
		return table.values();
	}
	
	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String prefix) {
		StringBuilder str = new StringBuilder();
		for (Collection<Feature> features : table.values()) {
			int i = 1;
			for (Feature f : features) {
				str.append(prefix + f);
				if (i < features.size()) str.append(",");
				i++;
			}
		}
		return str.toString();
	}
}
