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

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class ClusterJsonMapper {

    private static ObjectMapper mapper = new ObjectMapper();
    private static JsonFactory factory = new JsonFactory();

    public static Cluster fromJson(String jsonAsString)
    	throws JsonMappingException, JsonParseException, IOException {
    
    	mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    	Cluster cluster = mapper.readValue(jsonAsString, Cluster.class);
    	
    	return cluster;
    }

    public static String toJson(Cluster cluster, boolean prettyPrint) 
    	throws JsonMappingException, JsonGenerationException, IOException {
        
    	StringWriter writer = new StringWriter();
        JsonGenerator generator = factory.createJsonGenerator(writer);
        
        if (prettyPrint) {
            generator.useDefaultPrettyPrinter();
        }
        
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.writeValue(generator, cluster);
        return writer.toString();
    }
}