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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oculusinfo.ml.DataSet;
import com.oculusinfo.ml.Instance;
import com.oculusinfo.ml.feature.Feature;

/***
 * 
 * Abstract base class that provides many useful features a typical clusterer needs.
 *   
 * Most clusterers will extend this class directly.  
 * The only method that is required to be implemented by sub-classes is isCandidate() for
 * determining whether an instance can be added to a cluster during clustering.
 * 
 * @author slangevin
 *
 */
public abstract class AbstractClusterer extends BaseClusterer {
	protected final static int DEFAULT_THREAD_POOL = Runtime.getRuntime().availableProcessors();

	protected final boolean penalizeMissingFeatures;
	protected final boolean firstCandidate;
	
	protected double maxDistance = 1.0;
	
	protected static Logger log = LoggerFactory.getLogger("com.oculusinfo");
	protected ExecutorService exec; // = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL); //.newSingleThreadExecutor();;
	
	@Override
	public void init() {
		exec = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Clusterer Pool");
			}
		}); //.newSingleThreadExecutor();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				terminate();
			}
		});
	}
	
	@Override
	public void terminate() {
		if (exec == null) return;

		try {
			log.debug("Clusterer thread pool starting shutdown");
			exec.shutdown();
			try {
				if (!exec.awaitTermination(10, TimeUnit.SECONDS)) {
					log.error("Clusterer thread pool did not shut down gracefully");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			exec.shutdownNow();
//			log.info("Clusterer thread pool shutdown");
		}
		catch (Exception e) {
			log.error("Clusterer thread pool did not shut down gracefully");
		}
	}
	
	protected class DistanceResult {
		public final Instance i;
		public final Cluster c;
		public final double distance;
		
		public DistanceResult(Instance i, Cluster c, double distance) {
			this.i = i;
			this.c = c;
			this.distance = distance;
		}
		
		public boolean isNull() {
			return (c == null);
		}
	}
	
	/***
	 * Method for determining whether a cluster is a better candidate than the previous best candidate cluster.
	 * 
	 * @param inst the instance being clustered
	 * @param candidate is the cluster
	 * @param score is the double precision value of the distance score of inst to candidate
	 * @param best is the current best cluster the inst can be added to
	 * @param bestScore is the current best score
	 * @return true if candidate is a better cluster for inst than best
	 */
	protected abstract boolean isCandidate(Instance inst, Cluster candidate, double score, Cluster best, double bestScore);
	
	public AbstractClusterer() {
		this(false, false, true);
	}
	
	public AbstractClusterer(boolean firstCandidate) {
		this(firstCandidate, false, true);
	}
	
	public AbstractClusterer(boolean firstCandidate, boolean onlineUpdate, boolean penalizeMissingFeatures) {
		super(onlineUpdate);
		this.firstCandidate = firstCandidate;
		this.penalizeMissingFeatures = penalizeMissingFeatures;
	}
	
	/***
	 * Method to override the logger for the clusterer to use for output.
	 * 
	 * @param logger
	 */
	public void setLogger(Logger logger) {		
		log = logger;
	}
	
	/***
	 * Return the executor service the clusterer is using for parallelization.
	 * 
	 * @return the executor service
	 */
	public ExecutorService getExecutor() {
		return exec;
	}
	
	@Override
	public ClusterResult doIncrementalCluster(DataSet ds, List<Cluster> clusters) {
		return doCluster(ds, clusters);
	}
	
	@Override
	public ClusterResult doCluster(DataSet ds) {
		return doCluster(ds, new LinkedList<Cluster>());
	}
	
	private List<List<? extends Instance>> createBlocks(List<? extends Instance> clusters, int blocksize) {
		List<List<? extends Instance>> blocks = new LinkedList<List<? extends Instance>>();
		
		int sIdx = 0;
		int eIdx = 0;
		while (eIdx < clusters.size()) {
			eIdx = (int)Math.min(sIdx+blocksize, clusters.size());
			blocks.add(new LinkedList<Instance>(clusters.subList(sIdx, eIdx)));
			sIdx = eIdx;
		}
		return blocks;
	}

	/***
	 * Public method to find the best cluster for inst to be a member.
	 * 
	 * The method parallelizes the distance calculations between inst and the clusters
	 * using the executor service. The cluster that is the best candidate for the inst is returned.
	 * 
	 * @param inst is the instance being considered
	 * @param clusters is a collection of clusters to search
	 * @return the best cluster
	 */
	public DistanceResult bestCluster(final Instance inst, final List<List<? extends Instance>> clusterBlocks) {
		double bestScore 		= Double.MAX_VALUE;
		Cluster bestCluster 	= null;
		CompletionService<DistanceResult> batch = new ExecutorCompletionService<DistanceResult>(getExecutor());
		
	
		for (final List<? extends Instance> clusters : clusterBlocks) {
			batch.submit(new Callable<DistanceResult>() {
				@Override
				public DistanceResult call() {
					double bestDist = Double.MAX_VALUE;
					Instance bestMatch = null;
					
					for (Instance c : clusters) {
						double d = distance(inst, c); 
						if (d < bestDist) {
							bestDist = d;
							bestMatch = c;
						}
					}
					return new DistanceResult(inst, (Cluster)bestMatch, bestDist);
				}
			});
		}
		for (int i=0; i < clusterBlocks.size(); i++) {
			try {
 				DistanceResult result = batch.take().get();
 			
				if (isCandidate(result.i, result.c, result.distance, bestCluster, bestScore)) {
					bestScore = result.distance;
					bestCluster = result.c;
					if (firstCandidate) break; // best is the first candidate found
				} 
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				log.error("Error executing cluster distance task: {}", e.getLocalizedMessage());
			}
		}
		return new DistanceResult(inst, bestCluster, bestScore);
	}
	
	/***
	 * Protected method to initiate clustering dataset.  The public methods
	 * doCluster() and doIncrementalCluster() invoke this method.
	 * 
	 * @param ds the data set to cluster
	 * @param clusters is a collection of clusters to modify
	 * @return a collection of modified clusters
	 */
	protected ClusterResult doCluster(DataSet ds, List<Cluster> clusters) {
		double start = System.currentTimeMillis();
		
		// if the clusterer hasn't been initially manually then init it now
		if (exec == null) init();
		
		LinkedHashSet<Cluster> modified = new LinkedHashSet<Cluster>();
		
		for (Instance inst : ds) {
			// Process in batches of blocks of 100 clusters
			List<List<? extends Instance>> blocks = createBlocks(clusters, 100);
			
//			double bestStart = System.currentTimeMillis();
			Cluster bestCluster = bestCluster(inst, blocks).c;
//			double bestTime = System.currentTimeMillis() - bestStart;
//			log.debug("Find Best Cluster Time: {} ", bestTime);
		
			if (bestCluster == null) {	// no candidate cluster was found - create new one
				bestCluster = createCluster();
				bestCluster.add(inst);
				if (!onlineUpdate) bestCluster.updateCentroid();
				clusters.add(bestCluster);
			}
			else {
				bestCluster.add(inst);
			}
			modified.add(bestCluster);
		}
		
		// centroids were not updated online so update them now
		if (!onlineUpdate) {
			for (Cluster c : modified) {
				c.updateCentroid();
			}
		}
		
		double clusterTime = System.currentTimeMillis() - start;
		log.debug("Clustering time (s): {}", clusterTime / 1000);
		
		return new InMemoryClusterResult(new LinkedList<Cluster>(modified));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public double distance(Instance inst1, Instance inst2) {
		double totalDist = 0; 

		try {
			for (FeatureTypeDefinition typedef : this.getTypeDefs()) {
				if (typedef.distFunc.getWeight() < 0.00001) continue;  // skip if weight is near zero
				
				Feature f1 = inst1.getFeature(typedef.featureName);
				Feature f2 = inst2.getFeature(typedef.featureName);
				
				double d = 0;
				
				if (f1 == null || f2 == null) {
					d = penalizeMissingFeatures ? typedef.distFunc.getWeight() : 0;  
				}
				else {
					d = typedef.distFunc.distance(f1, f2) * typedef.distFunc.getWeight();
				}
				
				totalDist += d;
			}
		}
		catch (Exception e) {
			log.error("Error calculating distance between:\n---\n" + inst1.toString() + "---\n" + inst2.toString() + "---\nException:", e);
		}
		
		return totalDist;
	}
}
