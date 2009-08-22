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

package org.apache.commons.math.complex;

import org.apache.commons.math.MathRuntimeException;

/**
 * Static implementations of common 
 * {@link org.apache.commons.math.complex.Complex} utilities functions.
 *
 * @version $Revision: 772119 $ $Date: 2009-05-06 05:43:28 -0400 (Wed, 06 May 2009) $
 */
public class ComplexUtils {
    
    /**
     * Default constructor.
     */
    private ComplexUtils() {
        super();
    }
    
    /**
     * Creates a complex number from the given polar representation.
     * <p>
     * The value returned is <code>r&middot;e<sup>i&middot;theta</sup></code>,
     * computed as <code>r&middot;cos(theta) + r&middot;sin(theta)i</code></p>
     * <p>
     * If either <code>r</code> or <code>theta</code> is NaN, or 
     * <code>theta</code> is infinite, {@link Complex#NaN} is returned.</p>
     * <p>
     * If <code>r</code> is infinite and <code>theta</code> is finite, 
     * infinite or NaN values may be returned in parts of the result, following
     * the rules for double arithmetic.<pre>
     * Examples: 
     * <code>
     * polar2Complex(INFINITY, &pi;/4) = INFINITY + INFINITY i
     * polar2Complex(INFINITY, 0) = INFINITY + NaN i
     * polar2Complex(INFINITY, -&pi;/4) = INFINITY - INFINITY i
     * polar2Complex(INFINITY, 5&pi;/4) = -INFINITY - INFINITY i </code></pre></p>
     * 
     * @param r the modulus of the complex number to create
     * @param theta  the argument of the complex number to create
     * @return <code>r&middot;e<sup>i&middot;theta</sup></code>
     * @throws IllegalArgumentException  if r is negative
     * @since 1.1
     */
    public static Complex polar2Complex(double r, double theta) {
        if (r < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "negative complex module {0}", r);
        }
        return new Complex(r * Math.cos(theta), r * Math.sin(theta));
    }
    
}
