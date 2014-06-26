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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/***
 * This class represents a cluster result that stores all Clusters in memory.
 * 
 * @author slangevin
 *
 */
public class InMemoryClusterResult implements ClusterResult {
	private static final long serialVersionUID = -485649795889971226L;
	
	List<Cluster> clusters = new LinkedList<Cluster>();
	
	public InMemoryClusterResult(List<Cluster> clusters) {
		this.clusters.addAll(clusters);
	}
	
	@Override
	public Iterator<Cluster> iterator() {
		return clusters.iterator();
	}

	@Override
	public boolean isEmpty() {
		return clusters.isEmpty();
	}

	@Override
	public int size() {
		return clusters.size();
	}
}
