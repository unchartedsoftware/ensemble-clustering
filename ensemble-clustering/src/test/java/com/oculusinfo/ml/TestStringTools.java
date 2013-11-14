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
package com.oculusinfo.ml;

import org.junit.Assert;
import org.junit.Test;

import com.oculusinfo.ml.utils.StringTools;

public class TestStringTools {

	@Test
	public void test() {
		String str2 = "godel";
		String str3 = " godel ";
		String str4 = "~godel~";
		String str5 = "\tgodel";
		String str6 = "%##@godel@#!@";
		String str7 = "apple banana";
		String str8 = "#$%apple banana";
		String str9 = "apple $%#@banana";
		String str10 = "apple\tbanana";
		String str11 = "banana apple";
		
		Assert.assertEquals(StringTools.fingerPrint(str2), "godel");
		Assert.assertEquals(StringTools.fingerPrint(str3), "godel");
		Assert.assertEquals(StringTools.fingerPrint(str4), "godel");
		Assert.assertEquals(StringTools.fingerPrint(str5), "godel");
		Assert.assertEquals(StringTools.fingerPrint(str6), "godel");
		Assert.assertEquals(StringTools.fingerPrint(str7), "apple banana");
		Assert.assertEquals(StringTools.fingerPrint(str8), "apple banana");
		Assert.assertEquals(StringTools.fingerPrint(str9), "apple banana");
		Assert.assertEquals(StringTools.fingerPrint(str10), "apple banana");
		Assert.assertEquals(StringTools.fingerPrint(str11), "apple banana");
	}

}
