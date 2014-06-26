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
package com.oculusinfo.ml.feature.bagofwords.centroid;

import java.util.Collection;
import java.util.Collections;
import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.feature.bagofwords.BagOfWordsFeature;
import com.oculusinfo.ml.stats.FeatureFrequency;
import com.oculusinfo.ml.stats.FeatureFrequencyTable;

/***
 * A Centroid for BagOfWordsFeatures that represents the centroid as the top 10 words with the highest frequency
 * 
 * @author slangevin
 *
 */
public class BagOfWordsCentroid implements Centroid<BagOfWordsFeature> {
	private static final long serialVersionUID = -5723416814427314073L;
	private String name;
	private static final int MAX_CENTROID_FEATURES = 10;
	protected final FeatureFrequencyTable freqTable = new FeatureFrequencyTable();
	
	@Override
	public void add(BagOfWordsFeature feature) {
		for (FeatureFrequency nom : feature.getValues()) {
			freqTable.add(nom);
		}
	}
	
	@Override
	public void remove(BagOfWordsFeature feature) {
		for (FeatureFrequency nom : feature.getValues()) {
			freqTable.decrementBy(nom.feature, nom.frequency);
		}
	}

	@Override
	public Collection<BagOfWordsFeature> getAggregatableCentroid() {
        BagOfWordsFeature rawCounts = new BagOfWordsFeature(name);
        rawCounts.setFreqTable(freqTable);
        return Collections.singleton(rawCounts);
    }

    @Override
	public BagOfWordsFeature getCentroid() {
		// centroid is the top N most frequent words
		Collection<FeatureFrequency> freqs = freqTable.getTopN(MAX_CENTROID_FEATURES);
		BagOfWordsFeature medoid = new BagOfWordsFeature(name);
		for (FeatureFrequency freq : freqs) {
			medoid.setCount(freq);
		}
		return medoid;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class<BagOfWordsFeature> getType() {
		return BagOfWordsFeature.class;
	}
	
	public FeatureFrequencyTable getFreqTable() {
		return this.freqTable;
	}

	@Override
	public void reset() {
		freqTable.clear();
	}
}
