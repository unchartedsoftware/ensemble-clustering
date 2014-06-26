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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;
import com.oculusinfo.ml.feature.numeric.centroid.MeanNumericVectorCentroid;
import com.oculusinfo.ml.feature.numeric.distance.EuclideanDistance;
import com.oculusinfo.ml.spark.SparkDataSet;
import com.oculusinfo.ml.spark.SparkInstanceParser;
import com.oculusinfo.ml.spark.unsupervised.cluster.kmeans.KMeansClusterer;

public class TestKMeans extends JFrame {
	private static final long serialVersionUID = -7287997469823771918L;

	public static void genTestData(int k) {
		PrintWriter writer;
		try {
			writer = new PrintWriter("test.txt", "UTF-8");
		    
			// each class size is equal 
			int classSize = 1000000 / k;
			
			double stdDev = 30.0;
			
			// generate k classes of data points using a normal distribution with random means and fixed std deviation
			for (int i=0; i < k; i++) {
				Random rnd = new Random();
		
				double meanX = rnd.nextDouble() * 400.0;
				double meanY = rnd.nextDouble() * 400.0;
				
				// randomly generate a dataset of x, y points
				for (int j = 0; j < classSize; j++) {
					double x = rnd.nextGaussian()*stdDev + meanX;
					double y = rnd.nextGaussian()*stdDev + meanY;
			
					writer.println(x + "," + y);
				}
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
	
	public static List<double[]> readInstances() throws Exception {
		ArrayList<double[]> instances = new ArrayList<double[]>();
		
		File folder = new File("output/clusters");
		File[] files = folder.listFiles(); 
		  
		int index = 0;
		Map<String, Integer> clusters = new HashMap<String, Integer>();
		
		for (File file : files) {
			if (file.getName().startsWith(".")) continue;
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			try {
				String line = br.readLine();

				while (line != null) {
					if (line == "") continue;
					String cluster = line.substring(1, line.indexOf(","));
	            
					if (!clusters.containsKey(cluster)) {
						clusters.put(cluster, index);
						index++;
					}
	            
					String[] coords = line.substring(line.indexOf("point") + "point:[".length(), line.lastIndexOf("]")).split(";");
					double x = Double.parseDouble(coords[0]);
					double y = Double.parseDouble(coords[1]);
					instances.add(new double[] {clusters.get(cluster), x, y});
					line = br.readLine();
				}
			} finally {
				br.close();
			}
		}
		
		return instances;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int k = 5;
		
		try {
			FileUtils.deleteDirectory( new File("output/clusters") );
			FileUtils.deleteDirectory( new File("output/centroids") );
		} catch (IOException e1) { /* ignore (*/ }	
		
		genTestData(k);
		
		JavaSparkContext sc = new JavaSparkContext("local", "OculusML");  
		SparkDataSet ds = new SparkDataSet(sc);
		ds.load("test.txt", new SparkInstanceParser() {
			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<String, Instance> call(String line) throws Exception {
				Instance inst = new Instance();
				
				String tokens[] = line.split(",");
				
				NumericVectorFeature v = new NumericVectorFeature("point");
				
				double x = Double.parseDouble(tokens[0]);
				double y = Double.parseDouble(tokens[1]);
				v.setValue( new double[] { x, y } );
				
				inst.addFeature(v);
				
				return new Tuple2<String, Instance>(inst.getId(), inst);
			}
		});

		KMeansClusterer clusterer = new KMeansClusterer(k, 10, 0.001, "output/centroids", "output/clusters");
		
		clusterer.registerFeatureType("point", MeanNumericVectorCentroid.class, new EuclideanDistance(1.0));
		
		clusterer.doCluster(ds);
		
		try {
			final List<double[]> instances = readInstances();
			
			final Color[] colors = {Color.red, 
					Color.blue, 
					Color.green, 
					Color.magenta, 
					Color.yellow, 
					Color.black, 
					Color.orange, 
					Color.cyan,
					Color.darkGray,
					Color.white};
			
			TestKMeans t = new TestKMeans();
	        t.add(new JComponent() {
				private static final long serialVersionUID = 2059497051387104848L;

				public void paintComponent(Graphics g) {
	                Graphics2D g2 = (Graphics2D) g;
	                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                                    RenderingHints.VALUE_ANTIALIAS_ON);
	               
	                for (double[] inst : instances) {
	                	int color = (int)inst[0];
	                	g.setColor ( colors[color] );
	                	
	                	Ellipse2D l = new Ellipse2D.Double(inst[1], inst[2], 5, 5);
	                	g2.draw(l);
	                }
	            }
	        });

	        t.setDefaultCloseOperation(EXIT_ON_CLOSE);
	        t.setSize(400, 400);
	        t.setVisible(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
