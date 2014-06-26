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
package com.oculusinfo.ml.spark;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.oculusinfo.ml.feature.bagofwords.BagOfWordsFeature;
import com.oculusinfo.ml.feature.numeric.NumericVectorFeature;
import com.oculusinfo.ml.feature.spatial.GeoSpatialFeature;

/***
 * This helper class assists with processing CSV files in SparkInstanceParsers
 * 
 * @author slangevin
 *
 */
public class SparkInstanceParserHelper {
	private Map<String, Field> fields = new HashMap<String, Field>();
	
	public SparkInstanceParserHelper(String line) throws IOException {
		List<String> fieldStrings = CsvParser.fsmParse(line); //(String[])parser.parse(line).get(0);
		
		for (String fieldStr : fieldStrings) {
			Field field = fieldStringToField(fieldStr);
			if (field != null) {
				fields.put(field.name, field);
			}
		}
	}
	
	private Field fieldStringToField(String fieldStr) {
		if (fieldStr.isEmpty()) return null;
		
		Field field = null;
		try {
			String[] tokens = fieldStr.split(":");
			if (tokens.length < 2) {
				field = new Field(tokens[0], "");
			}
			else {
				field = new Field(tokens[0], tokens[1]);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return field;
	}
	
	public Integer fieldToInteger(String name) {
		Field field = fields.get(name);
		
		if (field == null) return null;
		
		Integer val = null;
		
		try {
			val = Integer.parseInt(field.value);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	
	public Double fieldToDouble(String name) {
		Field field = fields.get(name);
		
		if (field == null) return null;
		
		Double val = null;
		
		try {
			val = Double.parseDouble(field.value);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return val;
	}
	
	public String fieldToString(String name) {
		Field field = fields.get(name);
		
		if (field == null) return null;
		
		return field.value;
	}
	
	public BagOfWordsFeature fieldToBagOfWordsFeature(String name) {
		Field field = fields.get(name);
		
		if (field == null) return null;
		
		BagOfWordsFeature feature = null;
		
		try {
			feature = new BagOfWordsFeature(field.name);
		
			String val = field.value.substring(1, field.value.length()-1);  // strip off enclosing [ ]
		
			if (val.isEmpty()) return null;
			
			String[] entries = val.split(";");
		
			for (String entry : entries) {
				if (entry.isEmpty()) continue;
				String[] tokens = entry.split("=");
				feature.setCount(tokens[0], Integer.parseInt(tokens[1]));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return feature;
	}
	
	public GeoSpatialFeature fieldToGeoSpatialFeature(String name) {
		Field field = fields.get(name);
		
		if (field == null) return null;
		
		GeoSpatialFeature feature = null;
		
		try {
			feature = new GeoSpatialFeature(field.name);
		
			String val = field.value.substring(1, field.value.length()-1);  // strip off enclosing [ ]
		
			if (val.isEmpty()) return null;
			
			String[] tokens = val.split(";");
		
			feature.setValue(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return feature;
	}
	
	public NumericVectorFeature fieldToNumericVectorFeature(String name) {
		Field field = fields.get(name);
		
		if (field == null) return null;
		
		NumericVectorFeature feature = null;
		
		try {
			feature = new NumericVectorFeature(field.name);
		
			String val = field.value.substring(1, field.value.length()-1);  // strip off enclosing [ ]
		
			if (val.isEmpty()) return null;
			
			String[] entries = val.split(";");
		
			double[] vector = new double[entries.length];
		
			for (int i=0; i < entries.length; i++) {
				if (entries[i].isEmpty()) continue;
				vector[i] = Double.parseDouble(entries[i]);
			}
			feature.setValue(vector);
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
		return feature;
	}
}
