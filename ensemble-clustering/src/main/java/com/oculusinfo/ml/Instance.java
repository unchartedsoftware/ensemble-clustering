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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonIgnore;
import com.oculusinfo.ml.feature.Feature;

/***
 * Instance represents one "row" or "group" of data describing one entity in a data set.
 * The instances are associated with a collection of typed features that represent the data values
 * of the entity.  Each instance can be provided a distinguishing "label" to can be used for classification.
 * 
 * Each instance is assumed to be provided a unique identifier.  If none is provided, then one will be generated.
 * 
 * @author slangevin
 *
 */
public class Instance implements Serializable {
	private static final long serialVersionUID = -8781788906032267606L;
	
	protected String 	id;
	protected String	classLabel;
	protected Map<String, Feature> features = new LinkedHashMap<String, Feature>();
	
	public Instance() { 
		this(UUID.randomUUID().toString());
	}
	
	/***
	 * Constructor to specify your own id's
	 * 
	 * NOTE: id's MUST be unique!  It's is up to the caller to ensure this.
	 * 
	 * @param id
	 */
	public Instance(String id) {
		this.id = id;
	}
	
	public boolean hasClassLabel(String label) {
		return classLabel.equalsIgnoreCase(label);
	}
	
	public void setClassLabel(String label) {
		classLabel = label;
	}
	
	public String getClassLabel() {
		return classLabel;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\"id:" + id + "\",");
		int i = 1;
		for (Feature feature : features.values()) {
			str.append("\"" + feature.toString() + "\"");
			if (i < features.size()) str.append(",");
			i++;
		}
		return str.toString();
	}
	
	public void addFeature(Feature feature) {
		features.put(feature.getId(), feature);
	}

	public void addFeatures(Collection<? extends Feature> features) {
		for (Feature f : features) {
			addFeature(f);
		}
	}

	public boolean containsFeature(String featureName) {
		return features.containsKey(featureName);
	}
	
	public Feature getFeature(String featureName) {
		return features.get(featureName);
	}

	@JsonIgnore
	public boolean isEmpty() {
		return features.isEmpty();
	}

	@JsonIgnore
	public Collection<Feature> getAllFeatures() {
		return features.values();
	}

	public Map<String, Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, Feature> features) {
		features.putAll(features);
	}

	public void removeFeature(String featureName) {	
		features.remove(featureName);
	}

	public void removeAllFeatures() {
		features.clear();
	}

	public int numFeatures() {
		return features.size();
	}	
}
