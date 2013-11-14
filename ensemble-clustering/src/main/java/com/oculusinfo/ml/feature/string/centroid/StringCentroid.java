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
package com.oculusinfo.ml.feature.string.centroid;

import java.util.Collection;

import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.feature.string.StringFeature;

public class StringCentroid implements Centroid<StringFeature> {
	private static final long serialVersionUID = 4724468020495197877L;
	private String name;

	@Override
	public void add(StringFeature feature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(StringFeature feature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<StringFeature> getAggregatableCentroid () {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public StringFeature getCentroid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class<StringFeature> getType() {
		return StringFeature.class;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
