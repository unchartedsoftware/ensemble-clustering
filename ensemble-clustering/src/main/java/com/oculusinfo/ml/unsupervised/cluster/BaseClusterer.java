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
package com.oculusinfo.ml.unsupervised.cluster;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.distance.DistanceFunction;
import com.oculusinfo.ml.feature.Feature;

public abstract class BaseClusterer implements Clusterer {

	protected ClusterFactory clusterFactory;
	protected final boolean onlineUpdate;
	protected final Map<String, FeatureTypeDefinition> typeDefs = new HashMap<String, FeatureTypeDefinition>();
	
	public BaseClusterer(boolean onlineUpdate) {
		this.onlineUpdate = onlineUpdate;
	}

	/***
	 * Method to remove all feature types registered with this clusterer.
	 *
	 */
	public void clearFeatureTypes() {
		typeDefs.clear();
	}
	
	/***
	 * Method to register a feature type, centroid class and the distance function associated with the feature type.
	 * 
	 * @param name is an internal name for the centroid value in each cluster 
	 * @param centroidClass is the class that represents the central values of the instance features associated with this feature group
	 * @param distFunc is the distance function to be used for calculating distance for this feature type
	 */
	@SuppressWarnings("rawtypes")
	public void registerFeatureType(String name, Class<? extends Centroid> centroidClass, DistanceFunction distFunc) {
		typeDefs.put(name, new FeatureTypeDefinition(name, centroidClass, distFunc));
	}
	
	/***
	 * Method to return the associated distance function for feature name.
	 * 
	 * @param the name of the feature
	 * @return the distance function
	 */
	@SuppressWarnings("unchecked")
	public DistanceFunction<Feature> getDistanceFunction(String featureName) {
		DistanceFunction<Feature> func = null;
		if (typeDefs.containsKey(featureName)) {
			func = typeDefs.get(featureName).distFunc;
		}
		return func;
	}
	
	/***
	 * Method to return the associated centroid class for feature name.
	 * 
	 * @param the name of the feature
	 * @return the centroid class
	 */
	@SuppressWarnings("rawtypes")
	public Class<? extends Centroid> getCentroidClass(String featureName) {
		Class<? extends Centroid> centroidClass = null;
		if (typeDefs.containsKey(featureName)) {
			centroidClass = typeDefs.get(featureName).centroidClass;
		}
		return centroidClass;
	}
	
	/***
	 * Method to return feature types registered with this clusterer.
	 * 
	 * @return a collection of feature type definitions
	 */
	public Collection<FeatureTypeDefinition> getTypeDefs() {
		return typeDefs.values();
	}
	
	/***
	 * Public method for creating a new cluster instance. The new cluster is associated with
	 * the centroids for each feature.
	 * 
	 * @return the new cluster instance
	 */
	public Cluster createCluster() {
		return (new ClusterFactory(this.typeDefs, onlineUpdate)).create();
	}
}
