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
package com.oculusinfo.ml.feature.semantic.distance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/***
 * This class represents a tree of concepts.  
 * We assume a tree hierarchy for efficiency of calculating lowest common parent (LCP) operations.
 * LCP can be performed in O(h) time where h is the height of the tree.  
 * 
 * Note: This LCP can be made in constant time if a linear caching step is performed
 * 
 * If the Taxonomy needs to support multiple inheritance, this class will need to be modified.
 *   
 * @author slangevin
 *
 */
public class Concept implements Serializable {
	private static final long serialVersionUID = 1273216830907344599L;
	private int depth = -1;
	private int height = -1;
	private String name;
	private Concept parent = null;
	private final Set<Concept> children = new HashSet<Concept>();
	private final HashMap<String, Concept> lookupcache = new HashMap<String, Concept>();
	private final Hashtable<String, Concept> lcacache = new Hashtable<String, Concept>();
	
	public Concept(String name) {
		this.name = name;
	}
	
	public Concept(String name, Concept parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public synchronized int getDepth() {
		if (depth < 0) {
			depth = 1;
			Concept p = parent;
			while (p != null) {
				depth++;
				p = p.getParent();
			}
		}
        return depth;
	}
	
	public synchronized int getHeight() {
		if (height < 0) {
			height = 1;
			int max = 0;
			for (Concept child : children) {
				int h = child.getHeight();
				if (h > max) max = h;
			}		
			height += max;
		}
		return height;
	}

	public String getName() {
		return name;
	}
	
	/***
	 * Finds the closest common parent of this Concept and y. 
	 * 
	 * Note: if the parameter y is the same as this Concept it will return itself
	 * 
	 * @param c the concept to find a common parent with
	 * @return the closest common parent Concept or null if none exists
	 */
	public Concept findCommonAncestor(Concept c) {
		// if the lca cache already contains this lookup then return the cached copy
		if (lcacache.containsKey(c.getName())) return lcacache.get(c.getName());
		
		Concept x = this;
		Concept y = c;
        int xdepth = x.getDepth();
        int ydepth = y.getDepth();
        if (xdepth < ydepth) {
            for (int i = 0; i < ydepth - xdepth; i++) y = y.getParent();
        }
        else {
            for (int i = 0; i < xdepth - ydepth; i++) x = x.getParent();
        }
        while (x != y) {
            x = x.parent;
            y = y.parent;
        }
        // cache result
        lcacache.put(c.getName(), x);
        
        return x;
	}
	
	public Concept findConcept(String name) {
		Concept found = null;
		
		// base case
		if (this.name.equalsIgnoreCase(name)) return this;
		
		// if concept is in lookup cache then return it
		if (lookupcache.containsKey(name)) return lookupcache.get(name);
		
		// Depth first search children
		for (Concept c : this.children) {
			found = c.findConcept(name);
			if (found != null) break;
		}
		
		// cache result and return - don't cache not found results!
		if (found != null) lookupcache.put(name, found);
	 
		return found;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(this.name + "\n");
		StringBuilder prefix = new StringBuilder();
		for (int i=0; i < this.getDepth(); i++) prefix.append("-");
		for (Concept child: children) {
			str.append(prefix + child.toString());
		}
		return str.toString();
	}

	public void setParent(Concept c) {
		this.parent = c;
	}
	
	public Concept getParent() {
		return parent;
	} 
	
	public void addChild(Concept c) {
		this.children.add(c);
	}

	public Set<Concept> getChildren() {
		return children;
	} 
	
	
}
