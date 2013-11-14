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
package com.oculusinfo.ml.tracks;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;



/**
 * A visible frame, in which to show visual graphical tests, in a way that's
 * easy to run from junit tests.
 * 
 * @author nkronenfeld
 */
public class TestFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private Object _showLock;
    public TestFrame () {
        _showLock = new Object();
        initializeGeometry();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing (WindowEvent e) {
                synchronized (_showLock) {
                    _showLock.notify();
                }
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized (ComponentEvent e) {
                saveGeometry();
            }
            @Override
            public void componentMoved (ComponentEvent e) {
                saveGeometry();
            }
        });
    }

    private void initializeGeometry () {
        Preferences p = Preferences.userRoot();
        Preferences oculus = p.node("com.oculusinfo");
        Preferences test = oculus.node("testing");

        int x = test.getInt("test.frame.x", 100);
        int y = test.getInt("test.frame.y", 100);
        int width = test.getInt("test.frame.width", 500);
        int height = test.getInt("test.frame.height", 500);

        setLocation(x, y);
        setSize(width, height);
    }

    private void saveGeometry () {
        Preferences p = Preferences.userRoot();
        Preferences oculus = p.node("com.oculusinfo");
        Preferences test = oculus.node("testing");

        Dimension size = getSize();
        Point location = getLocation();
        test.putInt("test.frame.x", location.x);
        test.putInt("test.frame.y", location.y);
        test.putInt("test.frame.width", size.width);
        test.putInt("test.frame.height", size.height);
    }

    /**
     * Show the frame, and wait until it is closed.
     */
    public void showAndWait () {
        setVisible(true);
        synchronized (_showLock) {
            try {
                _showLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
