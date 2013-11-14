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
package com.oculusinfo.ml.tracks;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oculusinfo.geometry.cartesian.CubicBSpline;
import com.oculusinfo.geometry.geodesic.Position;
import com.oculusinfo.geometry.geodesic.PositionCalculationParameters;
import com.oculusinfo.geometry.geodesic.PositionCalculationType;
import com.oculusinfo.geometry.geodesic.Track;
import com.oculusinfo.geometry.geodesic.tracks.GeodeticTrack;
import com.oculusinfo.math.linearalgebra.Vector;
import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.spatial.TrackFeature;
import com.oculusinfo.ml.feature.spatial.centroid.TrackCentroid;
import com.oculusinfo.ml.feature.spatial.distance.TrackDistance;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;
import com.oculusinfo.ml.unsupervised.cluster.ClusterResult;
import com.oculusinfo.ml.unsupervised.cluster.kmeans.KMeans;
import com.oculusinfo.ml.validation.unsupervised.external.BCubed;

public class TestTrackCluster {
	protected static Logger log = LoggerFactory.getLogger("TestTrackCluster.class");

    private static PositionCalculationParameters GEODETIC_PARAMETERS =
            new PositionCalculationParameters(PositionCalculationType.Geodetic,
                                              0.0001, 1E-8, false);


	public void clusterRandomTracks (int N, int T, int P, boolean visible) {
        TrackFrame frame = new TrackFrame();

        DataSet ds = new DataSet();
        
        log.info("Creating {} track splines as bases", N);
        
        // Create N track bases
        Rectangle2D bounds = new Rectangle2D.Double(12.5, 6.172, 1.521, 2.5);
        frame.setDrawingBounds(bounds);
        CubicBSpline[] trackBases = new CubicBSpline[N];
        for (int i=0; i<N; ++i) {
            trackBases[i] = randomBoundedSpline(bounds);
            frame.addSpline(trackBases[i]);
        }
        
        log.info("Creating {} track per track bases", T);
        // Now create a hundred random tracks from each
        List<Track> tracks = new ArrayList<Track>();
        for (int j=0; j<T; ++j) {
            for (int i=0; i<N; ++i) {
                Track track = new GeodeticTrack(GEODETIC_PARAMETERS, randomPoints(trackBases[i], P));
                
                // add the track as an instance to the dataset
                Instance inst = new Instance();
                TrackFeature feature = new TrackFeature("track");
                feature.setValue(track);
                inst.addFeature(feature);
                inst.setClassLabel( Integer.toString(i));  // set the generating spline as the class label for verification
                ds.add(inst);
                
                tracks.add(track);
            }
        }
        
        log.info("Clustering {} tracks", ds.size());
        
        // Cluster the tracks in N clusters.
		KMeans clusterer = new KMeans(N, 10, false);
		clusterer.registerFeatureType(
				"track", 
				TrackCentroid.class, 
				new TrackDistance(1.0));

		ClusterResult clusterResults = clusterer.doCluster(ds);

		frame.addClusters(clusterResults);

		log.info("Validating track clusters");

		// validate the clustering
		BCubed validator = new BCubed();
		validator.validate(frame.getClusters());
		
		log.info("Precision: {} ", validator.getPrecision());
		log.info("Recall: {} ", validator.getRecall());
		log.info("BCubed: {} ", validator.getBCubed());

        if (visible)
            frame.showAndWait();
    }

    private CubicBSpline randomBoundedSpline (Rectangle2D bounds) {
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        double minX = bounds.getMinX();
        double minY = bounds.getMinY();
        int N = (int) Math.round(3 + Math.random() * 3);

        // Get our times
        double[] times = new double[N];
        for (int i=0; i<N; ++i) {
            if (0 == i) times[i] = 0.0;
            else times[i] = times[i-1] + Math.random();
        }
        double totalTime = times[N-1];

        List<Vector> points = new ArrayList<Vector>();
        for (int i = 0; i < N; ++i) {
            times[i] = times[i] / totalTime;
            points.add(new Vector(minX + Math.random() * width,
                                  minY + Math.random() * height));
        }

        return CubicBSpline.fit(times, points);
    }

    private List<Position> randomPoints (CubicBSpline basis, int N) {
        N = (int) Math.round(N + N*(Math.random()-Math.random())/2.0);
        double[] times = new double[N];
        for (int i=0; i<N; ++i) {
            if (0 == i) times[i] = 0;
            else times[i] = times[i-1]+Math.random();
        }
        double totalTime = times[N-1];

        List<Position> points = new ArrayList<Position>(N);
        for (int i=1; i<N-1; ++i) {
            double t = times[i]/totalTime;
            Vector point = basis.getPoint(t);
            points.add(new Position(point.coord(0), point.coord(1)));
        }
        return points;
    }



    private class TrackFrame extends TestFrame {
        private static final long serialVersionUID = 1L;



        private List<CubicBSpline> _splines;
        private List<Cluster>    _clusters;
        private Rectangle2D        _drawingBounds;

        public TrackFrame () {
            _splines = new ArrayList<CubicBSpline>();
            _clusters = new ArrayList<Cluster>();
            _drawingBounds = null;
        }

        public void setDrawingBounds (Rectangle2D bounds) {
            _drawingBounds = bounds;
        }

        public void addSpline (CubicBSpline spline) {
            _splines.add(spline);
        }
        public void addClusters (Iterable<Cluster> clusters) {
            for (Cluster cluster: clusters) {
                _clusters.add(cluster);
            }
        }
        public List<Cluster> getClusters () {
            return _clusters;
        }

        private Rectangle2D getDrawingBounds () {
            if (null != _drawingBounds)
                return _drawingBounds;

            double minX = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;

            // For now, brute-force it; we'll put in max/min functions later.
            for (CubicBSpline s: _splines) {
                for (double t=0; t<=1.0; t += 0.01) {
                    Vector v = s.getPoint(t);
                    if (v.coord(0) < minX) minX = v.coord(0);
                    if (v.coord(0) > maxX) maxX = v.coord(0);
                    if (v.coord(1) < minY) minY = v.coord(1);
                    if (v.coord(1) > maxY) maxY = v.coord(1);
                }
            }

            return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
        }

        @Override
        public void paint (Graphics g) {
        	
        	
            Color[] colors = new Color[_splines.size()];
            Random rnd = new Random();
            
            // randomly associate a color with each spline
            for (int i = 0; i < colors.length; i++) {
            	colors[i] = new Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
            }
            
            Graphics2D g2 = (Graphics2D) g;
            Dimension size = getSize();
            Rectangle2D bounds = getDrawingBounds();
            bounds = new Rectangle2D.Double(bounds.getMinX()-bounds.getWidth()/10.0,
                                            bounds.getMinY()-bounds.getHeight()/10.0,
                                            bounds.getWidth()*1.2,
                                            bounds.getHeight()*1.2);
            double pixelsPerUnit = Math.min(size.width/bounds.getWidth(),
                                            size.height/bounds.getHeight());
            double zeroX = bounds.getMinX();
            double zeroY = bounds.getMaxY();

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, size.width, size.height);

            // draw the ground truth splines for reference
            for (int i=0; i<_splines.size(); ++i) {
                g2.setColor( colors[i] );
                g2.setStroke(new BasicStroke(4.0f));
                CubicBSpline spline = _splines.get(i);
                spline.draw(g2, zeroX, zeroY, pixelsPerUnit, 100);
            }

            int i = 0;
            // draw the actual tracks - color by cluster they are assigned to
            for (Cluster cluster : _clusters) {
            	Color color = colors[i];
            	i++;
            	
            	for (Instance inst: cluster.getMembers()) {
            		Track track = ((TrackFeature)inst.getFeature("track")).getValue();
            		
            		g2.setColor(color.darker());
            		g2.setStroke(new BasicStroke(1.0f));

            		Point ptLast = null;
            		for (Position p: track.getPoints()) {
            			Point pt = new Point((int) Math.round((p.getLongitude()-zeroX)*pixelsPerUnit),
                                         (int) Math.round((zeroY-p.getLatitude())*pixelsPerUnit));
            			if (null != ptLast)
            				g.drawLine(ptLast.x, ptLast.y, pt.x, pt.y);
            			ptLast = pt;
            		}
            	}
            }
        }
    }
    
    public static void main(String[] args) {
    	TestTrackCluster test = new TestTrackCluster();
    	test.clusterRandomTracks(5, 10, 30, true);
    }
}
