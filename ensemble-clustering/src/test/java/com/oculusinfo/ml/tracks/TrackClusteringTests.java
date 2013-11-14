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
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.oculusinfo.geometry.cartesian.CubicBSpline;
import com.oculusinfo.geometry.geodesic.Position;
import com.oculusinfo.geometry.geodesic.PositionCalculationParameters;
import com.oculusinfo.geometry.geodesic.PositionCalculationType;
import com.oculusinfo.geometry.geodesic.Track;
import com.oculusinfo.geometry.geodesic.tracks.Cartesian3DTrack;
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

public class TrackClusteringTests {
    private static final Logger LOGGER = Logger.getLogger(TrackClusteringTests.class.getName());
    private static final PositionCalculationParameters GEODETIC_PARAMETERS =
            new PositionCalculationParameters(PositionCalculationType.Geodetic, 0.01, 1E-10, false);
    private static final PositionCalculationParameters CARTESIAN_PARAMETERS =
            new PositionCalculationParameters(PositionCalculationType.Cartesian3D, 0.01, 1E-10, false);


    @Test
    public void testIncrementalMean () {
        TrackCentroid centroid = new TrackCentroid();
        Track A = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                       new Position(6000001, 0, 0, true),
                                       new Position(6000002, 0, 0, true),
                                       new Position(6000003, 1, 0, true));
        TrackFeature fA = new TrackFeature("a");
        fA.setValue(A);

        Track B = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                       new Position(6000001, 1, 0, true),
                                       new Position(6000002, 1, 0, true),
                                       new Position(6000003, 2, 0, true));
        TrackFeature fB = new TrackFeature("b");
        fB.setValue(B);

        Track C = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                       new Position(6000001, 2, 0, true),
                                       new Position(6000002, 2, 0, true),
                                       new Position(6000003, 3, 0, true));
        TrackFeature fC = new TrackFeature("c");
        fC.setValue(C);

        Track AB = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                        new Position(6000001, 0.5, 0, true),
                                        new Position(6000002, 0.5, 0, true),
                                        new Position(6000003, 1.5, 0, true));
        Track BC = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                        new Position(6000001, 1.5, 0, true),
                                        new Position(6000002, 1.5, 0, true),
                                        new Position(6000003, 2.5, 0, true));


        centroid.add(fA);
        Assert.assertEquals(A, centroid.getCentroid().getValue());

        centroid.add(fB);
        Assert.assertEquals(AB, centroid.getCentroid().getValue());

        centroid.add(fC);
        Assert.assertEquals(B, centroid.getCentroid().getValue());

        centroid.remove(fA);
        Assert.assertEquals(BC, centroid.getCentroid().getValue());

        centroid.remove(fB);
        Assert.assertEquals(C, centroid.getCentroid().getValue());

        centroid.remove(fC);
        Assert.assertNull(centroid.getCentroid().getValue());
    }

    @Test
    public void testIncrementalMeansWithOnePointTrack () {
        TrackCentroid centroid = new TrackCentroid();
        Track A = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                       new Position(6000001, 0, 0, true),
                                       new Position(6000002, 0, 0, true),
                                       new Position(6000003, 0, 0, true));
        TrackFeature fA = new TrackFeature("a");
        fA.setValue(A);

        Track B = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                       new Position(6000002, 3, 0, true));
        TrackFeature fB = new TrackFeature("b");
        fB.setValue(B);

        Track C = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                       new Position(6000000, 0, 0, true),
                                       new Position(6000002, 0, 0, true),
                                       new Position(6000004, 0, 0, true));
        TrackFeature fC = new TrackFeature("c");
        fC.setValue(C);

        Track AB = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                        new Position(6000001.5, 1.5, 0, true),
                                        new Position(6000002.0, 1.5, 0, true),
                                        new Position(6000002.5, 1.5, 0, true));
        Track BC = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                        new Position(6000001, 1.5, 0, true),
                                        new Position(6000002, 1.5, 0, true),
                                        new Position(6000003, 1.5, 0, true));
        Track ABC = new Cartesian3DTrack(CARTESIAN_PARAMETERS,
                                         new Position(6000001.0, 1.0, 0, true),
                                         new Position(6000002.0, 1.0, 0, true),
                                         new Position(6000003.0, 1.0, 0, true));


        centroid.add(fA);
        Assert.assertEquals(A, centroid.getCentroid().getValue());

        centroid.add(fB);
        Assert.assertEquals(AB, centroid.getCentroid().getValue());

        centroid.add(fC);
        Assert.assertEquals(ABC, centroid.getCentroid().getValue());

        centroid.remove(fA);
        Assert.assertEquals(BC, centroid.getCentroid().getValue());

        centroid.remove(fB);
        Assert.assertEquals(C, centroid.getCentroid().getValue());

        centroid.remove(fC);
        Assert.assertNull(centroid.getCentroid().getValue());
    }

    @Ignore
    @Test
    public void interactiveClusteringTest () {
        clusterRandomTracks(5, 10, 30, true);
    }

    public void clusterRandomTracks (int N, int T, int P, boolean visible) {
        TrackFrame frame = new TrackFrame();

        DataSet ds = new DataSet();
        
        LOGGER.info("Creating "+N+" track splines as bases");
        
        // Create N track bases
        Rectangle2D bounds = new Rectangle2D.Double(12.5, 6.172, 1.521, 2.5);
        frame.setDrawingBounds(bounds);
        CubicBSpline[] trackBases = new CubicBSpline[N];
        for (int i=0; i<N; ++i) {
            trackBases[i] = randomBoundedSpline(bounds);
            frame.addSpline(trackBases[i]);
        }
        
        LOGGER.info("Creating "+T+" track per track bases");
        
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
                inst.setClassLabel(Integer.toString(i));  // set the generating spline as the class label for verification
                ds.add(inst);
                
                tracks.add(track);
            }
        }

        LOGGER.info("Clustering "+ds.size()+" tracks");

        // Cluster the tracks in N clusters.
        KMeans clusterer = new KMeans(N, 10, false);
        clusterer.registerFeatureType(
                "track", 
                TrackCentroid.class, 
                new TrackDistance(1.0));
        
        ClusterResult clusterResults = clusterer.doCluster(ds);
        
        frame.addClusters(clusterResults);
        
        LOGGER.info("Validating track clusters");

        // validate the clustering
        BCubed validator = new BCubed();
        validator.validate(frame.getClusters());
        
        LOGGER.info("Precision: "+validator.getPrecision());
        LOGGER.info("Recall: "+validator.getRecall());
        LOGGER.info("BCubed: "+validator.getBCubed());

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
        private List<Cluster>      _clusters;
        private Rectangle2D        _drawingBounds;

        public TrackFrame () {
            _splines = new ArrayList<CubicBSpline>();
            _clusters = new ArrayList<Cluster>();
            _drawingBounds = null;
        }

        public List<Cluster> getClusters () {
            return _clusters;
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
                    List<?> points = track.getPoints();
                    for (int j=0; j<points.size(); ++j) {
                        Position p = (Position) points.get(j);

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
}
