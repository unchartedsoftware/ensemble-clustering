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
package com.oculusinfo.ml.feature.numeric.centroid;

import java.util.Collection;
import java.util.Collections;

import com.oculusinfo.ml.centroid.Centroid;
import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;

/***
 * A Centroid for NumericVectorFeatures that represents the centroid as the average number of each vector component
 * 
 * @author slangevin
 *
 */
public class MeanNumericVectorCentroid implements Centroid<NumericVectorFeature>  {
	private static final long serialVersionUID = 8127455596937762659L;
	private String name;
	private double weight;
	private double[] meanVector;
	
	@Override
	public void add(NumericVectorFeature feature) {
		double addedWeight = feature.getWeight();
		double newWeight = weight + addedWeight;

		if (meanVector == null) {
			if (feature.getValue() != null) {
				meanVector = feature.getValue().clone();
				weight = addedWeight;
			}
		}
		else {
			if (feature.getValue() != null) {
				// incrementally revise the centroid vector
				for (int i=0; i < meanVector.length; i++) {
					meanVector[i] = (meanVector[i] * weight + feature.getValue()[i] * addedWeight) / newWeight;
				}
				weight = newWeight;
			}
		}
	}

	@Override
	public void remove(NumericVectorFeature feature) {
	    double removedWeight = feature.getWeight();
	    double newWeight = weight - removedWeight;

	    if (0.0 == weight) return;

		// decrement centroid vector
		for (int i=0; i < meanVector.length; i++) {
			meanVector[i] = (meanVector[i] * weight - feature.getValue()[i] * removedWeight) / newWeight;
		}
		weight = newWeight;
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
	public Class<NumericVectorFeature> getType() {
		return NumericVectorFeature.class;
	}

    @Override
    public Collection<NumericVectorFeature> getAggregatableCentroid() {
        return Collections.singleton(getCentroid());
    }

	@Override
	public NumericVectorFeature getCentroid() {
		// create the centroid geospatial feature set
		NumericVectorFeature mean = new NumericVectorFeature(name);
		
		mean.setValue(meanVector);
		mean.setWeight(weight);
				
		return mean;
	}

	@Override
	public void reset() {
		meanVector = null;
		weight = 0;
	}
}
