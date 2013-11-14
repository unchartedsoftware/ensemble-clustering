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
package com.oculusinfo.math.statistics;

public class StatTracker {
    private int _n;
    private double _sumX;
    private double _sumXSquared;
    private double _min;
    private double _max;

    public StatTracker () {
        reset();
    }

    public void reset () {
        _n = 0;
        _sumX = 0;
        _sumXSquared = 0;
        _min = Double.NaN;
        _max = Double.NaN;
    }

    public void addStat (double value) {
        ++_n;
        _sumX += value;
        _sumXSquared += value*value;
        if (Double.isNaN(_min) || value < _min)
            _min = value;
        if (Double.isNaN(_max) || value > _max)
            _max = value;
    }

    public int numItems () {
        return _n;
    }

    public double mean () {
        return _sumX/_n;
    }

    public double max () {
        return _max;
    }

    public double min () {
        return _min;
    }

    /**
     * Normalize a value to fit in the range we've tracked
     * 
     * @return 0 if <code>value</code> is at the minimum tracked, 1 if at the
     *         max, linear interpolations thereof for other values, and NaN if
     *         no values have been tracked
     */
    public double normalizeValue (double value) {
        if (1 > _n) return 0.0;
        if (1 == _n) {
            if (value == _min) return 1.0;
            else return 0.0;
        }
        return (value-_min)/(_max-_min);
    }

    public double variance () {
        double mean = mean();
        return _sumXSquared/_n - mean*mean;
    }

    public double standardDeviation () {
        return Math.sqrt(variance());
    }
}
