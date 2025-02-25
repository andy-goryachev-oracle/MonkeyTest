/*
 * Copyright (c) 2025, Oracle and/or its affiliates. All rights reserved.
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

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;

/**
 * Context Menus
 */
public class Menus {
    private static final DecimalFormat FORMAT = new DecimalFormat("#0");

    /**
     * Creates a submenu with the specified values.
     *
     * @param <T> the type
     * @param cm the parent context nemu
     * @param text the menu text
     * @param setter the code to accept the chosen value
     * @param getter the code which supplies the current value
     * @param values the values
     * @return the created menu instance
     */
    public static <T> Menu subMenu(ContextMenu cm, String text, Consumer<T> setter, Supplier<T> getter, T... values) {
        // we could pass the property instead and to highlight the chosen value with a checkmark for example
        Menu m = FX.menu(cm, text);
        for (T value: values) {
            T v = getter == null ? null : getter.get();
            String name = value == null ? "<null>" : String.valueOf(value);
            if ((getter != null) && Objects.equals(v, value)) {
                name += " ✓";
            }
            item(m, name, () -> {
                setter.accept(value);
            });
        }
        return m;
    }

    private static void subMenu(ContextMenu cm, String text, DoubleProperty p, double[] values) {
        Menu m = FX.menu(cm, text);
        double val = p.get();
        for (double v: values) {
            String name = format(v);
            if (v == val) {
                name += " ✓";
            }
            item(m, name, () -> {
                p.set(v);
            });
        }
    }

    public static void sizeSubMenu(ContextMenu cm, Region r) {
        double[] min = {
            Region.USE_COMPUTED_SIZE,
            Region.USE_PREF_SIZE,
            0,
            10,
            25,
            50,
            100,
            250,
            500
        };
        subMenu(cm, "Min Height", r.minHeightProperty(), min);
        subMenu(cm, "Min Width", r.minWidthProperty(), min);

        double[] pref = {
            Region.USE_COMPUTED_SIZE,
            0,
            10,
            25,
            50,
            100,
            250,
            500
        };
        subMenu(cm, "Pref Height", r.prefHeightProperty(), pref);
        subMenu(cm, "Pref Width", r.prefWidthProperty(), pref);

        double[] max = {
            Region.USE_COMPUTED_SIZE,
            Region.USE_PREF_SIZE,
            0,
            10,
            25,
            50,
            100,
            250,
            500,
            1000,
            2500,
            5000,
            Double.POSITIVE_INFINITY
        };
        subMenu(cm, "Max Height", r.maxHeightProperty(), max);
        subMenu(cm, "Max Width", r.maxWidthProperty(), max);
    }

    private static String format(double v) {
        if (v == Region.USE_COMPUTED_SIZE) {
            return "USE_COMPUTED_SIZE";
        } else if (v == Region.USE_PREF_SIZE) {
            return "USE_PREF_SIZE";
        } else if (v == Double.POSITIVE_INFINITY) {
            return "INFINITY";
        }
        return FORMAT.format(v);
    }

    private static MenuItem item(Menu m, String text, Runnable action) {
        MenuItem mi = new MenuItem(text);
        mi.setMnemonicParsing(false);
        mi.setOnAction((ev) -> action.run());
        m.getItems().add(mi);
        return mi;
    }
}
