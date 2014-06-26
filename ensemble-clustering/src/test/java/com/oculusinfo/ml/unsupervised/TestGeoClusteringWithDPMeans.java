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
import com.oculusinfo.ml.feature.spatial.GeoSpatialFeature;
import com.oculusinfo.ml.feature.spatial.centroid.GeoSpatialCentroid;
import com.oculusinfo.ml.feature.spatial.distance.HaversineDistance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.dpmeans.DPMeans;

public class TestGeoClusteringWithDPMeans {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataSet ds = new DataSet();
		
		Instance inst = new Instance("1");
		GeoSpatialFeature feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("2");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("3");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("4");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("5");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("6");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("7");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("8");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("9");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		inst = new Instance("10");
		feature = new GeoSpatialFeature("geo");
		feature.setValue(-5.0, 120.0);
		inst.addFeature(feature);
		ds.add(inst);
		
		DPMeans clusterer = new DPMeans(3, true);
		clusterer.setThreshold(0.2);
		clusterer.registerFeatureType("geo", GeoSpatialCentroid.class, new HaversineDistance(1.0));
		
		ClusterResult clusters = clusterer.doCluster(ds);
		for (Cluster c : clusters) {
			System.out.println(c.toString(true));
		}
		clusterer.terminate();
	}
}
