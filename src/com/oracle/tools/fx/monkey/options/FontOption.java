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
package com.oracle.tools.fx.monkey.options;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import com.oracle.tools.fx.monkey.settings.HasSettings;
import com.oracle.tools.fx.monkey.settings.SStream;
import com.oracle.tools.fx.monkey.util.FX;

/**
 * Font Option Bound to a Property.
 */
public class FontOption extends Button implements HasSettings {
    private final SimpleObjectProperty<Font> property = new SimpleObjectProperty<>();
    private Popup popup;
    private final boolean allowNull;

    public FontOption(String name, boolean allowNull, ObjectProperty<Font> p) {
        this.allowNull = allowNull;

        FX.name(this, name);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.CENTER_LEFT);

        if (p != null) {
            property.bindBidirectional(p);
        }

        textProperty().bind(Bindings.createStringBinding(this::getButtonText, property));

        setFontValue(property.get());

        setOnAction((ev) -> togglePopup());
    }

    private void togglePopup() {
        if (popup == null) {
            Point2D p = localToScreen(0.0, getHeight());
            Font f = property.get();
            FontPickerPane fp = new FontPickerPane(f, allowNull, (v) -> {
                property.set(v);
                popup.hide();
            });
            popup = fp.createPopup();
            popup.setOnHidden((_) -> {
                if (popup != null) {
                    popup = null;
                }
            });
            popup.show(this, p.getX(), p.getY());
        } else {
            popup.hide();
            popup = null;
        }
    }

    private void setFontValue(Font f) {
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
    }

    @Override
    public SStream storeSettings() {
        SStream s = SStream.writer();
        Font f = property.get();
        if (f == null) {
            s.add("-");
        } else {
            s.add(f.getName());
            s.add(f.getSize());
        }
        return s;
    }

    @Override
    public void restoreSettings(SStream s) {
        Font f;
        String name = s.nextString("-");
        if ("-".equals(name)) {
            f = null;
        } else {
            double sz = s.nextDouble(12.0);
            f = new Font(name, sz);
        }
        property.set(f);
    }

    private String getButtonText() {
        Font f = property.get();
        return getFontString(f);
    }

    public SimpleObjectProperty<Font> getProperty() {
        return property;
    }

    public void selectSystemFont() {
        setFont(Font.getDefault());
    }

    public static String getFontString(Font f) {
        if (f == null) {
            return null;
        }

        String fam = f.getFamily();
        String sty = f.getStyle();
        double sz = f.getSize();
        return fam + " " + sty + " " + sz;
    }
}
