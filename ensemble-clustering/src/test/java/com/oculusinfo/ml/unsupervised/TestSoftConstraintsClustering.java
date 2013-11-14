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
package com.oculusinfo.ml.unsupervised;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.bagofwords.BagOfWordsFeature;
import com.oculusinfo.ml.feature.bagofwords.centroid.BagOfWordsCentroid;
import com.oculusinfo.ml.feature.bagofwords.distance.EditDistance;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.threshold.ThresholdClusterer;

public class TestSoftConstraintsClustering {

	private static final String FEATURE_NAME = "tokens";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataSet ds = new DataSet();
		
		Instance inst = new Instance();
		BagOfWordsFeature feature = new BagOfWordsFeature(FEATURE_NAME);
		feature.setCount("felines", 2);
		feature.setCount("cat", 5);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance();
		feature = new BagOfWordsFeature(FEATURE_NAME);
		feature.setCount("dog", 1);
		feature.setCount("wolf", 3);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance();
		feature = new BagOfWordsFeature(FEATURE_NAME);
		feature.setCount("cat", 2);
		feature.setCount("tiger", 4);
		inst.addFeature(feature);
		ds.add(inst);
		
		ThresholdClusterer clusterer = new ThresholdClusterer();
		clusterer.registerFeatureType(
				FEATURE_NAME, 
				BagOfWordsCentroid.class, 
				new EditDistance(1));
		
		clusterer.setThreshold(0.6);
		
		ClusterResult clusters = clusterer.doCluster(ds);
		System.out.println(clusters);
	}
}
