/*
 * Copyright (c) 2023, 2024, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import com.oracle.tools.fx.monkey.util.FX;

/**
 * Font Option Bound to a Property.
 */
public class FontOption extends HBox {
    private final SimpleObjectProperty<Font> property = new SimpleObjectProperty<>();
    private final ComboBox<String> fontField = new ComboBox<>();
    private final ComboBox<String> styleField = new ComboBox<>();
    private final ComboBox<Double> sizeField = new ComboBox<>();

    public FontOption(String name, boolean allowNull, ObjectProperty<Font> p) {
        FX.name(this, name);
        if (p != null) {
            property.bindBidirectional(p);
        }

        FX.name(fontField, name + "_FONT");
        fontField.getItems().setAll(collectFonts(allowNull));
        fontField.getSelectionModel().selectedItemProperty().addListener((x) -> {
            String fam = fontField.getSelectionModel().getSelectedItem();
            updateStyles(fam);
            update();
        });

        FX.name(styleField, name + "_STYLE");
        styleField.getSelectionModel().selectedItemProperty().addListener((x) -> {
            update();
        });

        FX.name(sizeField, name + "_SIZE");
        sizeField.getItems().setAll(
            1.0,
            2.5,
            6.0,
            8.0,
            10.0,
            11.0,
            12.0,
            16.0,
            24.0,
            32.0,
            48.0,
            72.0,
            144.0,
            480.0
        );
        sizeField.getSelectionModel().selectedItemProperty().addListener((x) -> {
            update();
        });

        getChildren().setAll(fontField, styleField, sizeField);
        setHgrow(fontField, Priority.ALWAYS);
        setMargin(sizeField, new Insets(0, 0, 0, 2));

        setFont(property.get());
    }

    public SimpleObjectProperty<Font> getProperty() {
        return property;
    }

    public void select(String name) {
        fontField.getSelectionModel().select(name);
    }

    public Font getFont() {
        String name = fontField.getSelectionModel().getSelectedItem();
        if (name == null) {
            return null;
        }
        String style = styleField.getSelectionModel().getSelectedItem();
        if (!isBlank(style)) {
            name = name + " " + style;
        }
        Double size = sizeField.getSelectionModel().getSelectedItem();
        if (size == null) {
            size = 12.0;
        }
        return new Font(name, size);
    }

    private static boolean isBlank(String s) {
        return s == null ? true : s.trim().length() == 0;
    }

    protected void updateStyles(String family) {
        String st = styleField.getSelectionModel().getSelectedItem();
        if (st == null) {
            st = "";
        }

        List<String> ss = Font.getFontNames(family);
        for (int i = 0; i < ss.size(); i++) {
            String s = ss.get(i);
            if (s.startsWith(family)) {
                s = s.substring(family.length()).trim();
                ss.set(i, s);
            }
        }
        Collections.sort(ss);

        styleField.getItems().setAll(ss);
        int ix = ss.indexOf(st);
        if (ix >= 0) {
            styleField.getSelectionModel().select(ix);
        }
    }

    protected void update() {
        Font f = getFont();
        property.set(f);
    }

    private void setFont(Font f) {
        String name;
        String style;
        double size;
        if (f == null) {
            name = null;
            style = null;
            size = 12.0;
        } else {
            name = f.getFamily();
            style = f.getStyle();
            size = f.getSize();
        }
        fontField.getSelectionModel().select(name);
        styleField.getSelectionModel().select(style);
        sizeField.getSelectionModel().select(size);
    }

    protected List<String> collectFonts(boolean allowNull) {
        ArrayList<String> rv = new ArrayList<>();
        if (allowNull) {
            rv.add(null);
        }
        rv.addAll(Font.getFamilies());
        return rv;
    }

    public void selectSystemFont() {
        FX.select(fontField, "System");
        FX.select(styleField, "");
        FX.select(sizeField, 12.0);
    }
}
