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
package com.oculusinfo.math.linearalgebra;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Vector implements Serializable {
    private static final long serialVersionUID = 7020657491745842344L;



    public static Vector zeroVector (int size) {
        double[] coords = new double[size];
        for (int i=0; i<size; ++i)
            coords[i] = 0.0;
        return new Vector(coords);
    }



    private double   _epsilon;
    private double[] _data;

    public Vector (double... data) {
        _data = data;
        _epsilon = 1E-12;
    }

    public Vector (Vector toCopy) {
        _data = Arrays.copyOf(toCopy._data, toCopy._data.length);
        _epsilon = toCopy._epsilon;
    }

    /**
     * Set the precision for equality and zero tests for this vector. If any
     * calculation yields two numbers closer than the given precision, they are
     * deemed equal. If any calculation yields a number less than the given
     * precision, it is deemed zero.
     * 
     * @param precision
     *            The precision for calculations with this vector.
     */
    public void setPrecision (double precision) {
        _epsilon = precision;
    }

    /**
     * Gets the precision used by this vector for equality tests. See
     * {@link #setPrecision(double)}
     */
    public double getPrecision () {
        return _epsilon;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Section: Clusterable implementation
    //
    public double getDistance (Vector other) {
        return Math.sqrt(getDistanceSquared(other));
    }

    /**
     * {@inheritDoc}
     */
    public double getDistanceSquared (Vector v) {
        return subtract(v).vectorLengthSquared();
    }

    /**
     * {@inheritDoc}
     */
    public Vector mean (List<? extends Vector> data) {
        if (data.isEmpty())
            throw new IllegalArgumentException("Attempt to take the mean of 0 vectors");

        Vector mean = zeroVector(data.get(0).size());
        for (Vector datum: data) {
            mean = mean.add(datum);
        }
        mean = mean.scale(1.0/data.size());
        return mean;
    }



    public int size () {
        return _data.length;
    }

    public double coord (int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("Illegal index 0");
        if (index >= _data.length)
            throw new IndexOutOfBoundsException("Illegal index "+index+" on vector of length "+_data.length);

        return _data[index];
    }

    public Vector add (Vector v) {
        int len = size();
        if (v.size() != len)
            throw new IllegalArgumentException("Attempt to add vectors of different lengths");

        double[] result = new double[len];
        for (int i=0; i<len; ++i) {
            result[i] = _data[i] + v._data[i];
        }

        return new Vector(result);
    }

    public Vector subtract (Vector v) {
        int len = size();
        if (v.size() != len)
            throw new IllegalArgumentException("Attempt to subtract vectors of different lengths");

        double[] result = new double[len];
        for (int i=0; i<len; ++i) {
            result[i] = _data[i] - v._data[i];
        }

        return new Vector(result);
    }

    public Vector scale (double scale) {
        int len = size();
        double[] coords = new double[len];
        for (int i=0; i<len; ++i)
            coords[i] = _data[i]*scale;

        return new Vector(coords);
    }

    public double dot (Vector v) {
        int len = size();
        if (v.size() != len)
            throw new IllegalArgumentException("Attempt to take the dot product of vectors of different lengths");

        double res = 0.0;

        for (int i=0; i<size(); ++i) {
            res += _data[i]*v._data[i];
        }

        return res;
    }

    public Vector cross (Vector v) {
        if (3 != size() || 3 != v.size()) 
            throw new IllegalArgumentException("Attempt to take the cross product of non-3-vectors");

        double xa = coord(0);
        double ya = coord(1);
        double za = coord(2);
        double xb = v.coord(0);
        double yb = v.coord(1);
        double zb = v.coord(2);
        return new Vector(ya*zb-za*yb, za*xb-xa*zb, xa*yb-ya*xb);
    }

    public double vectorLengthSquared () {
        return this.dot(this);
    }

    public double vectorLength () {
        return Math.sqrt(vectorLengthSquared());
    }


    @Override
    public int hashCode () {
        int len = size();
        int hash = len;
        for (int i=0; i<len; ++i) {
            hash = hash*71+(int) Math.round(_data[i]*73);
        }
        return hash;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (null == this) return false;
        if (!(obj instanceof Vector)) return false;
        Vector v = (Vector) obj;

        int len = size();
        if (v.size() != len) return false;
        for (int i=0; i<len; ++i) {
            double ours = _data[i];
            double theirs = v._data[i];
            if (Double.isNaN(ours) || Double.isNaN(theirs)) {
                if (!(Double.isNaN(ours) && Double.isNaN(theirs)))
                    return false;
            } else if (Math.abs(ours-theirs)>=_epsilon)
                return false;
        }
        return true;
    }

    @Override
    public String toString () {
        StringBuffer res = new StringBuffer();
        res.append("[");
        for (int i=0; i<_data.length; ++i) {
            if (i>0)
                res.append(", ");
            res.append(String.format("%.4f", _data[i]));
        }
        res.append("]");
        return res.toString();
    }
}
