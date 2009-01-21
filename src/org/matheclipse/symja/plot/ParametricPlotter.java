/*
CAS Computer Algebra System
Copyright (C) 2005  William Tracy

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/

package org.matheclipse.symja.plot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.Font;

import java.awt.event.*;
import javax.swing.*;

import java.util.*;

/*import com.hartmath.lib.*;
import com.hartmath.util.*;
import com.hartmath.expression.*;
import com.hartmath.mapping.*;*/

/** Plots parametric shapes.
  */
public class ParametricPlotter extends AbstractPlotter2D {
    /** The minimum bound on the independent variable.
      */
    protected double tMin;

    /** The maximum bounds on the independent variable.
      */
    protected double tMax;

    /** The range of the independent variable.
      */
    protected double tRange;

    /** The independent variable.
      */
    //protected HObject t;

    /** The first dependent variable.
      */
    //protected HUnaryNumerical x;

    /** The second dependent variable.
      */
    //protected HUnaryNumerical y;

    /** The x coordinates of the points plotted. They are indexed
      * first by plot number, then by the number of the point along
      * the plot (i.e. xPoints[functionNumber][pointNumber]).
      */
    protected double xPoints[][];

    /** The y coordinates of the points plotted.
      */
    protected double yPoints[][];

    /** Contains the hidden plots for re-use.
      */
    protected static List cache = new LinkedList();

    /** The number of shapes plotted. Poorly chosen variable name.
      * :-P
      */
    protected int functions;

    /** Populates the point arrays and readies plot for display.
      */
    public void plot(/*HFunction args*/) {
        //HFunction funcs;
        //HFunction tArgs;

        /*if (args.size() != 2)
            throw new IllegalArgumentException(
                                    "Incorrect number of arguments");
        if (!(args.get(0).isList() && args.get(1).isList()))
            throw new IllegalArgumentException(
                                    "Both arguments must be lists");*/

        thisResolution = newResolution;

        //funcs = (HFunction)args.get(0);
        /*if (funcs.get(0).isList()) {
            functions = funcs.size();
        } else {
            funcs = args;
            functions = 1;
        }*/

        /*tArgs = (HFunction)args.get(1);
        if (tArgs.size() != 3)
            throw new IllegalArgumentException(
                               "Variable name and bounds malformed");
        t = tArgs.get(0);
        tMin = ((HDouble)C.EV(C.N.f(tArgs.get(1)))).doubleValue();
        tMax = ((HDouble)C.EV(C.N.f(tArgs.get(2)))).doubleValue();
        tRange = tMax - tMin;

        xPoints = new double[functions][thisResolution + 1];
        yPoints = new double[functions][thisResolution + 1];
        color = new Color[functions];

        xMax = xMin = new HUnaryNumerical(
                      ((HFunction)funcs.get(0)).get(0), t).map(tMin);
        yMax = yMin = new HUnaryNumerical(
                      ((HFunction)funcs.get(0)).get(1), t).map(tMin);

        for (int f = 0; f < functions; ++f) {
            doPlot(funcs, f);
        }*/

        if (xMax <= xMin) {
            if (xMax < 0) {
                xMax = 0;
            } else if (xMin > 0) {
                xMin = 0;
            } else {
                ++xMax;
                --xMin;
            }
        }

        if (yMax <= yMin) {
            if (yMax < 0) {
                yMax = 0;
            } else if (yMin > 0) {
                yMin = 0;
            } else {
                ++yMax;
                --yMin;
            }
        }

        xRange = xMax - xMin;
        yRange = yMax - yMin;

        setupText();

        EventQueue.invokeLater(this);
    }

    /** Plots shape number f, and sets its color.
      */
    protected void doPlot(/*HFunction funcs,*/ int f) {
        /*if (((HFunction)funcs.get(f)).size() < 2)
            throw new IllegalArgumentException(
                            "Two functions required for plot #" + f);

        x = new HUnaryNumerical(((HFunction)funcs.get(f)).get(0), t);
        y = new HUnaryNumerical(((HFunction)funcs.get(f)).get(1), t);*/

        /*for(int counter = 0; counter <= thisResolution; ++counter) {
            plotPoint(f, counter);
        }*/
        //colorPlot(funcs, f);
    }

    /** Chooses the color for a plot.
      */
    protected void colorPlot(/*HFunction funcs,*/ int f) {
        /*if (((HFunction)funcs.get(f)).size() > 2) {
            String s = ((HFunction)funcs.get(f)).get(2).toString()
                                                      .toLowerCase();
            if (s.startsWith("b"))
                color[f] = Color.BLUE;
            else if (s.startsWith("c"))
                color[f] = Color.CYAN;
            else if (s.startsWith("g"))
                color[f] = Color.GREEN;
            else if (s.startsWith("m"))
                color[f] = Color.MAGENTA;
            else if (s.startsWith("o"))
                color[f] = Color.ORANGE;
            else if (s.startsWith("p"))
                color[f] = Color.PINK;
            else if (s.startsWith("r"))
                color[f] = Color.RED;
            else
                color[f] = Color.YELLOW;
        } else {
            color[f] = COLOR[f % COLOR.length];
        }*/
    }

    /** Plots point number n on shape f.
      */
    /*protected void plotPoint(int f, int n) {
        xPoints[f][n] = x.map(tMin
                    + tRange * (double)n / (double)(thisResolution));
        if (xPoints[f][n] < xMin)
            xMin = xPoints[f][n];
        else if (xPoints[f][n] > xMax)
            xMax = xPoints[f][n];

        yPoints[f][n] = y.map(tMin
                    + tRange * (double)n / (double)(thisResolution));
        if (yPoints[f][n] < yMin)
            yMin = yPoints[f][n];
        else if (yPoints[f][n] > yMax)
            yMax = yPoints[f][n];
    }*/

    /** Paints the plotted shapes on the display.
      */
    protected void paintPlots(Graphics2D g2d,
                              int top,
                              int height,
                              int bottom,
                              int left,
                              int width,
                              int right) {
        int x[] = new int[thisResolution + 1];
        int y[] = new int[thisResolution + 1];

        for (int f = 0; f < functions; ++f) {
            paintPlot(g2d,
                      top,
                      height,
                      bottom,
                      left,
                      width,
                      right,
                      x,
                      y,
                      f);
        }
    }

    /** Paints a shape on the display.
      */
    protected void paintPlot(Graphics2D g2d,
                             int top,
                             int height,
                             int bottom,
                             int left,
                             int width,
                             int right,
                             int x[],
                             int y[],
                             int f) {
        g2d.setColor(color[f]);
        for (int counter = 0; counter <= thisResolution; ++counter) {
            convertPoint(top,
                         height,
                         bottom,
                         left,
                         width,
                         right,
                         x,
                         y,
                         f,
                         counter);
        }
        g2d.drawPolyline(x, y, thisResolution + 1);
    }

    /** Positions a point on shape f at point n in the display
      * coordinate system.
      */
    protected void convertPoint(int top,
                                int height,
                                int bottom,
                                int left,
                                int width,
                                int right,
                                int x[],
                                int y[],
                                int f,
                                int n) {
        x[n] = left
             + (int)((xPoints[f][n] - xMin)
                  * width / xRange);
        y[n] = top + height
               - (int)((yPoints[f][n] - yMin)
                                     * height / yRange);
    }

    /** Returns either a new plot or a plot from a cache.
      */
    public static ParametricPlotter getParametricPlotter() {
        if (cache.isEmpty()) {
            return new ParametricPlotter();
        } else {
            ParametricPlotter pp = (ParametricPlotter)cache.get(0);
            cache.remove(pp);
            return pp;
        }
    }

    /** Caches the unused plot.
      */
    public void reclaim() {
        /*xPoints = null;
        yPoints = null;
        x = null;
        y = null;*/
        cache.add(this);
    }

    /** Empties the cache. Called when the applet is stopped, which
      * hoses the Swing components.
      */
    public static void clearCache() {
        cache.clear();
    }
}
