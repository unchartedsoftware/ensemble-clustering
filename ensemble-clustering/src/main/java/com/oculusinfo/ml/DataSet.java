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
package com.oculusinfo.ml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.oculusinfo.ml.feature.Feature;
import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;

/***
 * DataSet represents a structured collection of data instances that is the input 
 * to oculus machine learning tools.  
 * 
 * @author slangevin
 *
 */
public class DataSet implements Serializable, Iterable<Instance> {
	private static final long serialVersionUID = -544003860939601075L;
	
	private final Map<String, Instance> map = new LinkedHashMap<String, Instance>();

	private class DataSetIterator implements Iterator<Instance> {
		Iterator<Entry<String, Instance>> iterator;
		
		public DataSetIterator(Map<String, Instance> map) {
			this.iterator = map.entrySet().iterator();
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		@Override
		public Instance next() {
			return iterator.next().getValue();
		}
		@Override
		public void remove() {
			iterator.remove();
		}
	};
	
	public Set<String> getKeys() {
		return map.keySet();
	}
	
	public boolean add(Instance inst) {
		return (map.put(inst.getId(), inst) != null);
	}
	
	public Instance remove(Instance inst) {
		return map.remove(inst.getId());
	}

	public Instance get(String id) {
		return map.get(id);
	}
	
	public int size() {
		return map.size();
	}
	
	public DataSet sample(double fraction) {
		DataSet sample = new DataSet();
		
		// max fraction is 1
		if (fraction > 1) fraction = 1;
		
		// no samples are requested - return an empty data set
		if (fraction <= 0) return sample;
		
		long numToSample = Math.round( map.size() * fraction );
		
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		
		// randomly pick k instances as the initial k means
		ArrayList<String> indexes = new ArrayList<String>(keys.size()); 
		for (int i = 0; i < indexes.size(); i++) {
			indexes.add( keys.get(i) );
		}
		Collections.shuffle(indexes);  // permutate the indexes
		for (int i = 0; i < numToSample; i++) {
			sample.add( get(indexes.get(i)) );
		}
		return sample;
	}
	
	private void swap(Instance a, Instance b) {
		Instance tmp = a;
		a = b;
		b = tmp;
	}
	
	private void shuffle(Instance[] array) {
		Random rnd = new Random();
		int n = array.length;

		while (n > 1) {
			int k = rnd.nextInt(n);
			n--;
			swap(array[n], array[k]);
		}
	}
	
	public List<DataSet> nFolds(int n) {
		// Make sure n is valid: each fold must have at least one instance!
		if (n > size() || n < 1) return null;
		
		List<DataSet> folds = new LinkedList<DataSet>();
		
		Instance[] instances = new Instance[size()];
		instances = map.values().toArray(instances);
		shuffle(instances);
	
		int sliceSize = size() / n;
		int extra = size() % n;
		
		// create n folds
		for (int i=0; i < n; i++) {
			DataSet fold = new DataSet();
			folds.add(fold);
			
			int start = i*sliceSize;
			int end = start + sliceSize;
			for (int j=start; j < end; j++) {
				fold.add(instances[j]);
			}
		}
		
		// evenly distribute any extra instances
		for (int i=0; i < extra; i++) {
			int offset = instances.length - extra + i;
			folds.get(i).add(instances[offset]);
		}
		
		return folds;
	}
	
	public void normalizeInstanceFeature(String featureName) {
		List<Feature> allFeatures = new ArrayList<Feature>();
		
		// gather up all matching features
		for (Instance inst : this) {
			if (inst.containsFeature(featureName)) {
				allFeatures.add( inst.getFeature(featureName) );
			}
		}
		
		if (allFeatures.isEmpty()) return;
		
		double N = allFeatures.size();
		
		// currently only support normalizing numeric vector features
		if ( (allFeatures.get(0) instanceof NumericVectorFeature) == false ) return;
		
		double[] meanVector = ((NumericVectorFeature)allFeatures.get(0)).getValue().clone();
		
		// compute mean of feature
		for (int i=1; i < allFeatures.size(); i++) {
			NumericVectorFeature v = (NumericVectorFeature)allFeatures.get(i);
			double[] vals = v.getValue();
			
			for (int j=0; j < meanVector.length; j++) {
				meanVector[j] += vals[j];
			}
		}
		for (int i=0; i < meanVector.length; i++) {
			meanVector[i] /= N;
		}
		
		double[] stdevVector = new double[meanVector.length];
		
		// compute stdev of feature
		for (int i=0; i < allFeatures.size(); i++) {
			NumericVectorFeature v = (NumericVectorFeature)allFeatures.get(i);
			double[] vals = v.getValue();
			
			for (int j=0; j < meanVector.length; j++) {
				stdevVector[j] += (vals[j] - meanVector[j])*(vals[j] - meanVector[j]);
			}
		}
		for (int i=0; i < stdevVector.length; i++) {
			stdevVector[i] = Math.sqrt( stdevVector[i] / (N-1) );
		}
		
		// normalize each feature vector
		for (int i=0; i < allFeatures.size(); i++) {
			NumericVectorFeature v = (NumericVectorFeature)allFeatures.get(i);
			double[] vals = v.getValue();
			
			for (int j=0; j < vals.length; j++) {
				vals[j] = (vals[j] - meanVector[j]) / stdevVector[j];
			}
		}
	}
	 
	@Override
	public Iterator<Instance> iterator() {
		return new DataSetIterator(map);
	}

	public boolean addAll(Collection<Instance> c) {
		boolean altered = false;
		
		for (Instance i : c) {
			if ( add(i) ) altered = true;
		}
		return altered;
	}

	public void clear() {
		map.clear();
	}

	public boolean contains(Instance i) {
		return map.containsKey(i);
	}

	public boolean containsAll(Collection<Instance> c) {
		for (Instance i : c) {
			if (map.containsKey(i.getId()) == false) return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}
}
