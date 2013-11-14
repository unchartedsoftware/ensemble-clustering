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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;
import com.oculusinfo.ml.feature.numeric.centroid.MeanNumericVectorCentroid;
import com.oculusinfo.ml.feature.numeric.distance.EuclideanDistance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.threshold.ThresholdClusterer;

public class TestThresholdClusterer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9073044772934024066L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataSet ds = new DataSet();
		
		Random rnd = new Random();
		
		// randomly generate a dataset of lat, lon points
		for (int i = 0; i < 100000; i ++) {
			Instance inst = new Instance();
			NumericVectorFeature v = new NumericVectorFeature("point");
		
			double x = rnd.nextDouble();
			double y = rnd.nextDouble();
			v.setValue( new double[] { x, y } );
		
			inst.addFeature(v);
			ds.add(inst);
		}
		
		ThresholdClusterer clusterer = new ThresholdClusterer();
		clusterer.setThreshold(0.4);
		clusterer.registerFeatureType(
				"point", 
				MeanNumericVectorCentroid.class, 
				new EuclideanDistance(1.0));
		
		final ClusterResult clusters = clusterer.doCluster(ds);
		
		System.out.println(clusters.size());
		
//		System.out.println(clusters);
		
		TestThresholdClusterer t = new TestThresholdClusterer();
        t.add(new JComponent() {
			private static final long serialVersionUID = 2843707175134112058L;

			public void paintComponent(Graphics g) {            	
                Graphics2D g2 = (Graphics2D) g;
                g2.setBackground(Color.black);
                g2.clearRect(0, 0, 400, 400);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                Random rnd = new Random();

                for (Cluster cluster : clusters) {
                	Color c = new Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
                	for (Instance inst : cluster.getMembers()) {
                		g.setColor ( c );
                		NumericVectorFeature v = (NumericVectorFeature)inst.getFeature("point");
                		Ellipse2D l = new Ellipse2D.Double(v.getValue()[0] * 400.0, v.getValue()[1] * 400.0, 5, 5);
                		g2.draw(l);
                	}
                }
            }
        });

        t.setDefaultCloseOperation(EXIT_ON_CLOSE);
        t.setSize(400, 400);
        t.setVisible(true);
	}

}
