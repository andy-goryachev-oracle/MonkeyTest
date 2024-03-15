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
package com.oracle.tools.fx.monkey.sheets;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.options.TextChoiceOption;
import com.oracle.tools.fx.monkey.util.TextTemplates;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 * Convenience Methods.
 */
public class Options {
    /**
     * Returns the text choice option initialized with common single- or multi-line strings.
     * @param name
     * @param multiLine
     * @param allowEditButton
     * @param prop
     * @return
     */
    public static TextChoiceOption textOption(String name, boolean multiLine, boolean allowEditButton, StringProperty prop) {
        TextChoiceOption op = new TextChoiceOption(name, true, prop);
        Object[] pairs = multiLine ? TextTemplates.multiLineTextPairs() : TextTemplates.singleLineTextPairs();
        Utils.fromPairs(pairs, (k,v) -> op.addChoice(k, v));
        op.selectFirst();
        return op;
    }

    public static DoubleOption fixedSizeOption(String name, DoubleProperty p) {
        return DoubleOption.of(name, p, 0, 20, 33.4, 50, 100);
    }

    public static DoubleOption spacing(String name, DoubleProperty p) {
        return DoubleOption.of(name, p, 0, 0.333, 0.5, 1, 2, 10, 20, 33.4, 50, 100);
    }

    public static DoubleOption tabPaneConstraints(String name, DoubleProperty p) {
        DoubleOption d = new DoubleOption(name, p);
        d.addChoice("0", Double.valueOf(0));
        d.addChoice("10", 10.0);
        d.addChoice("33.3", 33.3);
        d.addChoice("100", 100.0);
        d.addChoice("Double.MAX_VALUE", Double.MAX_VALUE);
        d.addChoice("Double.POSITIVE_INFINITY", Double.POSITIVE_INFINITY);
        d.addChoice("Double.NaN", Double.NaN);
        d.selectInitialValue();
        return d;
    }

    public static DoubleOption forRegion(String name, Property<Number> p) {
        DoubleOption d = new DoubleOption(name, p);
        d.addChoice("USE_COMPUTED_SIZE (-1)", Region.USE_COMPUTED_SIZE);
        d.addChoice("USE_PREF_SIZE (-∞)", Region.USE_PREF_SIZE);
        d.addChoice("0", Double.valueOf(0));
        d.addChoice("10", 10.0);
        d.addChoice("33.3", 33.3);
        d.addChoice("100", 100.0);
        d.addChoice("Double.MAX_VALUE", Double.MAX_VALUE);
        d.addChoice("Double.MIN_VALUE", Double.MIN_VALUE);
        d.addChoice("Double.POSITIVE_INFINITY", Double.POSITIVE_INFINITY);
        d.addChoice("Double.NaN", Double.NaN);
        d.selectInitialValue();
        return d;
    }

    public static DoubleOption lineSpacing(String name, Property<Number> p) {
        return DoubleOption.of(name, p, 0, 0.5, 1, 2, 3.14, 10, 33.33, 100);
    }
}
