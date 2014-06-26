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
package com.oculusinfo.ml.feature.semantic.centroid;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.feature.semantic.SemanticFeature;
import com.oculusinfo.ml.feature.string.StringFeature;
import com.oculusinfo.ml.stats.FeatureFrequency;
import com.oculusinfo.ml.stats.FeatureFrequencyTable;

/***
 * A Centroid for SemanticFeatures that represents the centroid as the top 10 semantic features with the highest frequency
 * 
 * @author slangevin
 *
 */
public class SemanticCentroid implements Centroid<SemanticFeature> {
	private static final long serialVersionUID = -2797239783660367088L;
	private String name;
	private static final int MAX_CENTROID_FEATURES = 5;
	private static final int MAX_ENTITIES_PER_FEATURE = 3;
	
	protected final FeatureFrequencyTable freqTable = new FeatureFrequencyTable();
	protected final HashMap<String, FeatureFrequencyTable> entityFreqTable = new HashMap<String, FeatureFrequencyTable>();
	
	@Override
	public void add(SemanticFeature feature) {
		StringFeature property = new StringFeature(feature.getName());
		freqTable.add(property);
		if (!entityFreqTable.containsKey(property.getId())) {
			entityFreqTable.put(property.getId(), new FeatureFrequencyTable());
		}
		SemanticFeature sem = new SemanticFeature(feature.getName());
		sem.setValue(feature.getConcept(), feature.getUri());
		entityFreqTable.get(property.getId()).add(sem);
	}
	
	@Override
	public void remove(SemanticFeature feature) {
		StringFeature property = new StringFeature(feature.getName());
		freqTable.decrement(property);
		if (entityFreqTable.containsKey(property.getId())) {
			entityFreqTable.get(property.getId()).decrement(feature);
		}
	}


	@Override
	public Collection<SemanticFeature> getAggregatableCentroid () {
        Collection<SemanticFeature> rawCounts = new LinkedList<SemanticFeature>();
        
        Collection<FeatureFrequency> freqs = freqTable.getAll();
        for (FeatureFrequency freq : freqs) {
            Collection<FeatureFrequency> topEntities = entityFreqTable.get(freq.feature.getId()).getAll();
            for (FeatureFrequency topEntity : topEntities) {
                rawCounts.add((SemanticFeature) topEntity.feature);
            }
        }
        
        return rawCounts;
    }

    @Override
	public SemanticFeature getCentroid() {
		Collection<SemanticFeature> medoid = new LinkedList<SemanticFeature>();
		
		// semantic medoid is the top N most frequent semantic features
		Collection<FeatureFrequency> freqs = freqTable.getTopN(MAX_CENTROID_FEATURES);
		for (FeatureFrequency freq : freqs) {
			Collection<FeatureFrequency> topEntities = entityFreqTable.get(freq.feature.getId()).getTopN(MAX_ENTITIES_PER_FEATURE);
			for (FeatureFrequency topEntity : topEntities) {
				medoid.add((SemanticFeature) topEntity.feature);
			}
		}
		
		// TODO this needs to be refactored!
		return null;
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
	public Class<SemanticFeature> getType() {
		return SemanticFeature.class;
	}
	
	public FeatureFrequencyTable getFreqTable() {
		return this.freqTable;
	}
	
	public HashMap<String, FeatureFrequencyTable> getEntityFreqTable() {
		return this.entityFreqTable;
	}

	@Override
	public void reset() {
		freqTable.clear();
		entityFreqTable.clear();
	}
}
