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
package com.oculusinfo.ml.feature.semantic;

import com.oculusinfo.ml.feature.Feature;

public class SemanticFeature extends Feature {
	private static final long serialVersionUID = -6524985038597553461L;
	private String concept;
	private String uri;
	private String label;
	
	public SemanticFeature() {
		super();
	}
	
	public SemanticFeature(String name) {
		super(name);
	}
	
	public void setValue(String concept, String uri) {
		this.concept = concept;
		this.uri = uri;
	}
	
	public void setValue(String concept, String uri, String label) {
		this.concept = concept;
		this.uri = uri;
		this.label = label;
	}

	public String getConcept() {
		return concept;
	}
	
	public void setConcept(String concept) {
		this.concept = concept;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean suppressLabel) {
		StringBuilder output = new StringBuilder();
		output.append(this.getId());
		if (label != null && !suppressLabel) {
			output.append(":" + label);
		}
		return output.toString();
	}
	
	@Override
	public String getId() {
		return (name + ":" + concept); // + ":" + uri);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
