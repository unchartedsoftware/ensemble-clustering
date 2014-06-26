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
 * of the entity.  Each instance can be provided a distinguishing "label" that can be used for classification.
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
	
	/***
	 * Return whether this Instance has the specified label
	 * 
	 * @param label label to test
	 * @return true if the Instance has a matching class label
	 */
	public boolean hasClassLabel(String label) {
		return classLabel.equalsIgnoreCase(label);
	}
	
	/***
	 * Set the class label for the Instance
	 * @param label label to apply to the Instance
	 */
	public void setClassLabel(String label) {
		classLabel = label;
	}
	
	/***
	 * Return the class label given to this Instance or null if none 
	 * @return class label or null if none
	 */
	public String getClassLabel() {
		return classLabel;
	}
	
	/***
	 * The unique id for this Instance
	 * @return the Instance id
	 */
	public String getId() {
		return id;
	}
	
	/***
	 * Set the unique Instance id
	 * @param id id of Instance
	 */
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
	
	/***
	 * Add a Feature to this Instance.  Features are typed data attributes that describe the Instance.
	 * 
	 * Each Feature must have a unique id to distinguish them.
	 * 
	 * @param feature feature to add to the Instance
	 */
	public void addFeature(Feature feature) {
		features.put(feature.getId(), feature);
	}

	/***
	 * Add a collection of Features to this Instance.  Features are typed data attributes that describe the Instance.
	 * 
	 * Each Feature must have a unique id to distinguish them.
	 * 
	 * @param features features to add to the Instance
	 */
	public void addFeatures(Collection<? extends Feature> features) {
		for (Feature f : features) {
			addFeature(f);
		}
	}

	/***
	 * Return true if this Instance has a Feature with the specified name
	 * @param featureName featureName to check
	 * @return true if this Instance contain a Feature with name
	 */
	public boolean containsFeature(String featureName) {
		return features.containsKey(featureName);
	}
	
	/***
	 * Return the Feature with the specified name or null if the Instance doen't have a matching Feature
	 * @param featureName name of the Feature
	 * @return the Feature with the specified name or null if the Instance don't have a matching Feature
	 */
	public Feature getFeature(String featureName) {
		return features.get(featureName);
	}

	/***
	 * Return true if the Instance contains no Features
	 * @return true if the Instance contains no Features
	 */
	@JsonIgnore
	public boolean isEmpty() {
		return features.isEmpty();
	}

	/***
	 * Return all the Features associated with this Instance
	 * @return collection of Features
	 */
	@JsonIgnore
	public Collection<Feature> getAllFeatures() {
		return features.values();
	}

	/***
	 * Return a map of the Features associated with this Instance keyed by Feature name
	 * @return a Map of Features
	 */
	public Map<String, Feature> getFeatures() {
		return features;
	}

	/***
	 * Add all Features in a Map to the Instance whether the Map is keyed by Feature name
	 * @param feature Map to add
	 */
	public void setFeatures(Map<String, Feature> features) {
		features.putAll(features);
	}

	/***
	 * Remove the Feature with the specified name from the Instance
	 * @param name of the Feature to remove
	 */
	public void removeFeature(String featureName) {	
		features.remove(featureName);
	}

	/***
	 * Remove all the Features associated with this Instance
	 */
	public void removeAllFeatures() {
		features.clear();
	}

	/***
	 * Return the number of Features associated with this Instance
	 * @return the number of Features
	 */
	public int numFeatures() {
		return features.size();
	}	
}
