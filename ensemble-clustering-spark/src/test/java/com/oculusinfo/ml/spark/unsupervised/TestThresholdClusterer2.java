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
package com.oculusinfo.ml.spark.unsupervised;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import spark.api.java.JavaSparkContext;

import com.oculusinfo.ml.feature.bagofwords.centroid.BagOfWordsCentroid;
import com.oculusinfo.ml.feature.bagofwords.distance.EditDistance;
import com.oculusinfo.ml.spark.SparkDataSet;
import com.oculusinfo.ml.spark.unsupervised.cluster.threshold.ThresholdClusterer;

public class TestThresholdClusterer2 {

	public static void genTestData() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("test.txt", "UTF-8");
		    
			Random rnd = new Random();
		
			// randomly generate a dataset of lat, lon points
			for (int i = 0; i < 10000; i ++) {
				char[] val = new char[2];
				for (int j = 0; j < 2; j++) {
					val[j] = (char)(rnd.nextInt(26) + 97);
				}
				writer.println("aa" + new String(val));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		genTestData();
		
		JavaSparkContext sc = new JavaSparkContext("local[8]", "OculusML"); 
		SparkDataSet ds = new SparkDataSet(sc);
		ds.load("test.txt", new InstanceParser3() );

		ThresholdClusterer clusterer = new ThresholdClusterer(0.3);
		
		clusterer.registerFeatureType("point", BagOfWordsCentroid.class, new EditDistance(1.0));
		
		clusterer.doCluster(ds);
	}

}
