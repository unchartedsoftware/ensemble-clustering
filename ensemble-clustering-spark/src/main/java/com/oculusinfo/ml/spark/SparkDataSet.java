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
package com.oculusinfo.ml.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;

/***
 * This class represents a structured collection of data Instances that is backed by a Spark RDD.
 * 
 * @author slangevin
 *
 */
public class SparkDataSet extends DataSet {

	private static final long serialVersionUID = -1274820594060755626L;

	JavaSparkContext sc;
	JavaPairRDD<String, Instance> instances;

	/***
	 * The constructor must be passed a Spark Context used to communicate with the Spark installation.
	 * 
	 * @param sc - the Spark context
	 */
	public SparkDataSet(JavaSparkContext sc) {
		this.sc = sc;
	}
	
	/***
	 * The SparkDataSet loads data using this method to populate the DataSet with Instances.
	 *  
	 * @param path the location of the data to load (filesystem or HDFS path)
	 * @param parser is the object that converts each line in the data into an Instance
	 * @param minSplits determine the number of Spark partitions to split the data into
	 */
	public void load(String path, SparkInstanceParser parser, int minSplits) {
		try {    
			JavaRDD<String> lines = sc.textFile(path, minSplits); 
			instances = lines.mapToPair( parser );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * The SparkDataSet loads data using this method to populate the DataSet with Instances.
	 *  
	 * @param path the location of the data to load (filesystem or HDFS path)
	 * @param parser is the object that converts each line in the data into an Instance
	 */
	public void load(String path, SparkInstanceParser parser) {		
		try {
			JavaRDD<String> lines = sc.textFile(path); 
			instances = lines.mapToPair( parser );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/***
	 * The SparkDataSet can be directly provided a pre-defined RDD of Instances 
	 *  
	 * @param rdd the RDD of Instances
	 */
	public void load(JavaPairRDD<String, Instance> rdd) {
		this.instances = rdd;
	}
	
	/***
	 * Return the underlying Spark RDD containing hte DataSet Instances
	 * @return the RDD
	 */
	public JavaPairRDD<String, Instance> getRDD() {
		return this.instances;
	}
	
	/***
	 * Return the Spark context this DataSet is bound to
	 * 
	 * @return the JavaSparkContext
	 */
	public JavaSparkContext getContext() {
		return this.sc;
	}
}
