/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.tools.fx.monkey.util;

import java.util.function.Consumer;

/**
 * Creates common selectors.
 */
public class Selectors {
    /**
     * Creates the line spacing selector.
     * @param client the client
     * @return the selector
     */
    public static ItemSelector<Double> lineSpacing(Consumer<Double> client) {
        return new ItemSelector<Double>(
            "lineSpacing",
            client,
            0.0,
            1.0,
            2.5,
            3.3333333,
            5.0,
            10.0,
            31.45,
            100.0
        );
    }

    /**
     * Creates the tab size selector.
     * @param client the client
     * @return the selector
     */
    public static ItemSelector<Integer> tabsize(Consumer<Integer> client) {
        return new ItemSelector<Integer>(
            "tabSize",
            client,
            0,
            1,
            2,
            3,
            4,
            8,
            16,
            32,
            64
        );
    }
}
