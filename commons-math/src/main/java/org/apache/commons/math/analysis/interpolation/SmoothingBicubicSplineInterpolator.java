/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.util.MathUtils.OrderDirection;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Generates a bicubic interpolation function.
 * Before interpolating, smoothing of the input data is performed using
 * splines.
 * See <b>Handbook on splines for the user</b>, ISBN 084939404X,
 * chapter 2.
 *
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (Di, 10 Aug 2010) $
 * @since 2.1
 * @deprecated This class does not perform smoothing; the name is thus misleading.
 * Please use {@link org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolator}
 * instead. If smoothing is desired, a tentative implementation is provided in class
 * {@link org.apache.commons.math.analysis.interpolation.SmoothingPolynomialBicubicSplineInterpolator}.
 * This class will be removed in math 3.0.
 */
public class SmoothingBicubicSplineInterpolator
    implements BivariateRealGridInterpolator {
    /**
     * {@inheritDoc}
     */
    public BicubicSplineInterpolatingFunction interpolate(final double[] xval,
                                                          final double[] yval,
                                                          final double[][] zval)
        throws MathException, IllegalArgumentException {
        if (xval.length == 0 || yval.length == 0 || zval.length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NO_DATA);
        }
        if (xval.length != zval.length) {
            throw new DimensionMismatchException(xval.length, zval.length);
        }

        MathUtils.checkOrder(xval, OrderDirection.INCREASING, true);
        MathUtils.checkOrder(yval, OrderDirection.INCREASING, true);

        final int xLen = xval.length;
        final int yLen = yval.length;

        // Samples (first index is y-coordinate, i.e. subarray variable is x)
        // 0 <= i < xval.length
        // 0 <= j < yval.length
        // zX[j][i] = f(xval[i], yval[j])
        final double[][] zX = new double[yLen][xLen];
        for (int i = 0; i < xLen; i++) {
            if (zval[i].length != yLen) {
                throw new DimensionMismatchException(zval[i].length, yLen);
            }

            for (int j = 0; j < yLen; j++) {
                zX[j][i] = zval[i][j];
            }
        }

        final SplineInterpolator spInterpolator = new SplineInterpolator();

        // For each line y[j] (0 <= j < yLen), construct a 1D spline with
        // respect to variable x
        final PolynomialSplineFunction[] ySplineX = new PolynomialSplineFunction[yLen];
        for (int j = 0; j < yLen; j++) {
            ySplineX[j] = spInterpolator.interpolate(xval, zX[j]);
        }

        // For every knot (xval[i], yval[j]) of the grid, calculate corrected
        // values zY_1
        final double[][] zY_1 = new double[xLen][yLen];
        for (int j = 0; j < yLen; j++) {
            final PolynomialSplineFunction f = ySplineX[j];
            for (int i = 0; i < xLen; i++) {
                zY_1[i][j] = f.value(xval[i]);
            }
        }

        // For each line x[i] (0 <= i < xLen), construct a 1D spline with
        // respect to variable y generated by array zY_1[i]
        final PolynomialSplineFunction[] xSplineY = new PolynomialSplineFunction[xLen];
        for (int i = 0; i < xLen; i++) {
            xSplineY[i] = spInterpolator.interpolate(yval, zY_1[i]);
        }

        // For every knot (xval[i], yval[j]) of the grid, calculate corrected
        // values zY_2
        final double[][] zY_2 = new double[xLen][yLen];
        for (int i = 0; i < xLen; i++) {
            final PolynomialSplineFunction f = xSplineY[i];
            for (int j = 0; j < yLen; j++) {
                zY_2[i][j] = f.value(yval[j]);
            }
        }

        // Partial derivatives with respect to x at the grid knots
        final double[][] dZdX = new double[xLen][yLen];
        for (int j = 0; j < yLen; j++) {
            final UnivariateRealFunction f = ySplineX[j].derivative();
            for (int i = 0; i < xLen; i++) {
                dZdX[i][j] = f.value(xval[i]);
            }
        }

        // Partial derivatives with respect to y at the grid knots
        final double[][] dZdY = new double[xLen][yLen];
        for (int i = 0; i < xLen; i++) {
            final UnivariateRealFunction f = xSplineY[i].derivative();
            for (int j = 0; j < yLen; j++) {
                dZdY[i][j] = f.value(yval[j]);
            }
        }

        // Cross partial derivatives
        final double[][] dZdXdY = new double[xLen][yLen];
        for (int i = 0; i < xLen ; i++) {
            final int nI = nextIndex(i, xLen);
            final int pI = previousIndex(i);
            for (int j = 0; j < yLen; j++) {
                final int nJ = nextIndex(j, yLen);
                final int pJ = previousIndex(j);
                dZdXdY[i][j] = (zY_2[nI][nJ] - zY_2[nI][pJ] -
                                zY_2[pI][nJ] + zY_2[pI][pJ]) /
                    ((xval[nI] - xval[pI]) * (yval[nJ] - yval[pJ]));
            }
        }

        // Create the interpolating splines
        return new BicubicSplineInterpolatingFunction(xval, yval, zY_2,
                                                      dZdX, dZdY, dZdXdY);
    }

    /**
     * Compute the next index of an array, clipping if necessary.
     * It is assumed (but not checked) that {@code i} is larger than or equal to 0}.
     *
     * @param i Index
     * @param max Upper limit of the array
     * @return the next index
     */
    private int nextIndex(int i, int max) {
        final int index = i + 1;
        return index < max ? index : index - 1;
    }
    /**
     * Compute the previous index of an array, clipping if necessary.
     * It is assumed (but not checked) that {@code i} is smaller than the size of the array.
     *
     * @param i Index
     * @return the previous index
     */
    private int previousIndex(int i) {
        final int index = i - 1;
        return index >= 0 ? index : 0;
    }
}