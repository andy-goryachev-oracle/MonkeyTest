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
package com.oracle.tools.fx.monkey.options;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.NamedValue;

/**
 * Border Selector
 */
// TODO common base class?
public class BorderSelector extends ComboBox<NamedValue<Border>> {
    private final SimpleObjectProperty<Border> property = new SimpleObjectProperty<>();

    public BorderSelector(String name, ObjectProperty<Border> p) {
        property.bindBidirectional(p);
        FX.name(this, name);

        addChoice("<null>", null);
        addChoice("EMPTY", Border.EMPTY);
        addChoice("Red (1)", createBorder(Color.RED, 1, null));
        addChoice("Green (20)", createBorder(Color.GREEN, 20, null));
        addChoice("Rounded", createBorder(Color.ORANGE, 1, 5.0));

        // TODO add the current value to choices and select it

        getSelectionModel().selectedItemProperty().addListener((s,pr,c) -> {
            Border b = c.getValue();
            property.set(b);
        });
    }

    public void addChoice(String name, Border border) {
        getItems().add(new NamedValue<>(name, border));
    }
    
    private static Border createBorder(Color color, double width, Double radius) {
        BorderStrokeStyle style = BorderStrokeStyle.SOLID;
        CornerRadii radii = radius == null ? null : new CornerRadii(radius);
        BorderWidths widths = new BorderWidths(width);

        BorderStroke[] strokes = {
            new BorderStroke(color, style, radii, widths)
        };
        return new Border(strokes);
    }
}
