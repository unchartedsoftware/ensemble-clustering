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
package com.oculusinfo.ml.stats;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oculusinfo.geometry.geodesic.Track;
import com.oculusinfo.math.statistics.StatTracker;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.Feature;
import com.oculusinfo.ml.feature.spatial.TrackFeature;
import com.oculusinfo.ml.unsupervised.cluster.Cluster;

/**
 * This class takes a cluster of tracks, and compiles the various statistics
 * etc. needed on it for actual use.
 * 
 * It is meant to be used only after clustering is complete.
 * 
 * @author nathan
 */
public class TrackClusterWrapper {
    private static final Logger      LOGGER  = LoggerFactory.getLogger(TrackClusterWrapper.class);
    private static final double      EPSILON = 1E-12;


    // The tracks in this cluster, from the closes
    private List<Track>              _tracks;
    // The mean of everything, ignoring nothing
    private Track                    _trueMean;
    // The distance of each track from the true mean
    private Map<Track, Double>       _distancesFromTrueMean;
    // The proportion of outliers to ignore
    private double                   _outlierIgnoreRatio;
    // The mean, ignoring outliers
    private Track                    _practicalMean;
    // The distance of each track from the practical mean
    private Map<Track, Double>       _distanceFromPracticalMean;
    // Standard deviation of tracks from the practical (not the true) mean.
    private double                   _standardDeviation;

    // A (user-set) color to be associated with this cluster
    private Color                    _clusterColor;
    // A (user-set) name to be associated with this cluster
    private String                   _clusterName;

    // Statistics kept on member tracks
    private Map<String, StatTracker> _statistics;

    public TrackClusterWrapper (Cluster cluster) {
        this(Collections.singleton(cluster));
    }

    public TrackClusterWrapper (Collection<Cluster> clusters) {
        _statistics = new HashMap<String, StatTracker>();
        _clusterName = null;
        _clusterColor = null;

        initializeTracks(clusters);

        setOutlierIgnoreRatio(0.2);

        compileStatistics();
    }

    private void initializeTracks (Collection<Cluster> clusters) {
        _tracks = new ArrayList<Track>();
        _trueMean = null;

        // Pull all tracks out of the cluster, and calculate their true mean
        for (Cluster cluster: clusters) {
            for (Instance instance : cluster.getMembers()) {
                for (Feature f : instance.getAllFeatures()) {
                	if (f instanceof TrackFeature) {
                		TrackFeature tf = (TrackFeature) f;
                		Track track = tf.getValue();
    
                		if (null == _trueMean) {
                			_trueMean = track;
                		} else {
                			_trueMean = _trueMean.weightedAverage(track, _tracks.size(), 1);
                		}
    
                		_tracks.add(track);
                    }
                }
            }
        }

        // Sort them in order of distance from the true mean
        _distancesFromTrueMean = new HashMap<Track, Double>();
        for (Track track: _tracks) {
            _distancesFromTrueMean.put(track, track.getDistance(_trueMean));
        }

        Collections.sort(_tracks, new Comparator<Track>() {
            @Override
            public int compare (Track t1, Track t2) {
                return _distancesFromTrueMean.get(t1).compareTo(_distancesFromTrueMean.get(t2));
            }
        });
    }



    /**
     * Causes the given proportion of tracks farthest from the true mean to be
     * ignored when calculating the practical mean.
     * 
     * @param ratio
     *            A number in the range [0.0, 1.0). Floor(proportion times
     *            number of tracks) will be ignored, so that at least one track
     *            is always used, so the practical mean is never null.
     */
    public void setOutlierIgnoreRatio (double ratio) {
        if (ratio < 0.0) {
            LOGGER.warn("Illegal outlier ignore ratio {}: Must be >= 0.0", ratio);
            return;
        }
        if (ratio >= 1.0) {
            LOGGER.warn("Illegal outlier ignore ratio {}: Must be < 1.0", ratio);
            return;
        }
        if (Math.abs(_outlierIgnoreRatio-ratio) < EPSILON)
            // No change
            return;


        _outlierIgnoreRatio = ratio;


        // Recalculate our practical mean
        int toKeep = (int) Math.ceil(_tracks.size()*(1.0-_outlierIgnoreRatio));
        _practicalMean = null;
        for (int i=0; i<toKeep; ++i) {
            Track track = _tracks.get(i);
            if (0 == i) {
                _practicalMean = track;
            } else {
                _practicalMean = _practicalMean.weightedAverage(track, i, 1);
            }
        }

        // Precalculate distances from practical mean and total standard deviation
        _distanceFromPracticalMean = new HashMap<Track, Double>();
        double variance = 0.0;
        for (Track track: _tracks) {
            double distance = track.getDistance(_practicalMean);
            _distanceFromPracticalMean.put(track, distance);
            variance += distance*distance;
        }
        variance /= _tracks.size();
        _standardDeviation = Math.sqrt(variance);
    }


    private void compileStatistics () {
        for (Track track: _tracks) {
            Map<String, Double> trackStats = track.getStatistics();
            for (Entry<String, Double> entry: trackStats.entrySet()) {
                String statName = entry.getKey();
                double statVal = entry.getValue();
                if (!_statistics.containsKey(statName))
                    _statistics.put(statName, new StatTracker());
                _statistics.get(statName).addStat(statVal);
            }
        }
    }


    public double getDistance (Track track) {
        if (_distanceFromPracticalMean.containsKey(track))
            return _distanceFromPracticalMean.get(track);
        else return track.getDistance(_practicalMean);
    }

    /**
     * retrieves a list of all tracks in this cluster
     */
    public List<Track> getTracks () {
        return Collections.unmodifiableList(_tracks);
    }

    /**
     * Retrieves the mean of the cluster, as calculated from those tracks most
     * towards the mean (@see {@link #setOutlierIgnoreRatio(double)})
     */
    public Track getMean () {
        return _practicalMean;
    }

    /**
     * Retrieves the standard deviation of this cluster from its mean, where the
     * mean is calculated ignoring the furthest tracks from the mean.
     */
    public double getStandardDeviation () {
        return _standardDeviation;
    }

    public Map<String, StatTracker> getStatistics () {
        return _statistics;
    }


    public void setClusterName (String name) {
        _clusterName = name;
    }

    public String getClusterName () {
        return _clusterName;
    }

    public void setClusterColor (Color c) {
        _clusterColor = c;
    }

    public Color getClusterColor () {
        return _clusterColor;
    }


    /**
     * Gets a short description of this cluster, including name, number of
     * members, length of mean, and standard deviation
     */
    public String getClusterDescription () {
        String description = String.format("Cluster %s: %d items, %.1f long, std. dev.=%.4f",
                                           _clusterName, _tracks.size(), _practicalMean.getLength(), _standardDeviation);
        for (String statName: _statistics.keySet()) {
            StatTracker stat = _statistics.get(statName);
            description += String.format("\n\t%s:%.4f\n\t    [%.4f to %4f],\n\t     sd=%.4f",
                                         statName,
                                         stat.mean(), stat.min(), stat.max(), stat.standardDeviation());
        }
        return description;
    }
}
