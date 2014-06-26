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
package com.oculusinfo.ml.feature.bagofwords;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.oculusinfo.ml.feature.Feature;
import com.oculusinfo.ml.feature.string.StringFeature;
import com.oculusinfo.ml.stats.FeatureFrequency;
import com.oculusinfo.ml.stats.FeatureFrequencyTable;

/***
 * A BagOfWordsFeature represents a Set of Words each of which is associated with a frequency count.
 * 
 * Useful for representing Document or other Text fields in a DataSet.
 * 
 * @author slangevin
 *
 */
public class BagOfWordsFeature extends Feature {
	private static final long serialVersionUID = 6927104885425283254L;
	private FeatureFrequencyTable freqTable = new FeatureFrequencyTable();
	
	public BagOfWordsFeature() {
		super();
	}
	
	public BagOfWordsFeature(String name) {
		super(name);
	}
	
	public void setCount(FeatureFrequency freq) {
		freqTable.remove(freq);
		freqTable.add(freq);
	}
	
	public void setCount(String term, int count) {
		FeatureFrequency freq = new FeatureFrequency(new StringFeature(term));
		freq.frequency = count;
		setCount(freq);
	}
	
	public void incrementValue(String term) {
		freqTable.add(new StringFeature(term));
	}
	
	public void decrementValue(String value) {
		freqTable.decrement(new StringFeature(value));
	}
	
	public FeatureFrequency getCount(String term) {
		return freqTable.get(new StringFeature(term));
	}
	
	@JsonIgnore
	public Collection<FeatureFrequency> getValues() {
		return freqTable.getAll();
	}
	
	public FeatureFrequencyTable getFreqTable() {
		return this.freqTable;
	}
	
	public void setFreqTable(FeatureFrequencyTable table) {
		freqTable = table;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.getName() + ":[");
		int i=1;
		for (FeatureFrequency f : freqTable.getAll()) {
			str.append(f.feature.getName() + "=" + f.frequency);
			if (i < freqTable.getAll().size()) str.append(";");
			i++;
		}
		str.append("]");	
		
		return str.toString();
	}
}
