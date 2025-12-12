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
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.AnchorPoint;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.AnchorPolicy;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.Formats;
import com.oracle.tools.fx.monkey.util.PopupButton;
import com.oracle.tools.fx.monkey.util.RelocationPane;

/**
 * Stage Relocation Policy Option.
 */
public class StageRelocationOption extends PopupButton {

    public record Spec(
        Screen screen,
        AnchorPoint screenAnchor,
        Insets screenPadding,
        AnchorPoint stageAnchor,
        AnchorPolicy anchorPolicy
    ) {
        @Override
        public String toString() {
            return Formats.screen(screen) + " ...";
        }
    }

    private final SimpleObjectProperty<Spec> property = new SimpleObjectProperty<>();

    public StageRelocationOption(String name) {
        FX.name(this, name);
        setMaxWidth(Double.MAX_VALUE);
        setAlignment(Pos.CENTER_LEFT);

        setContentSupplier(() -> {
            return new RelocationPane(property, this::hidePopup);
        });

        textProperty().bind(Bindings.createStringBinding(this::getButtonText, property));

        setOnAction((ev) -> togglePopup());
    }

    private String getButtonText() {
        Spec v = property.get();
        return v == null ? null : v.toString();
    }

    public SimpleObjectProperty<Spec> getProperty() {
        return property;
    }

    private static void describe(StringBuilder sb, Object x) {
        if (x instanceof Screen s) {
            sb.append(Formats.screen(s));
        } else if (x instanceof Insets m) {
            sb.append(Formats.insets(m));
        } else if (x != null) {
            // TODO describe
            sb.append(x);
        }
    }

    public void apply(Stage stage) {
        Spec s = property.get();
        if (s != null) {
            if(s.screen() == null) {
                if ((s.screenPadding() == null) && (s.anchorPolicy() == null)) {
                    stage.relocate(
                        s.screenAnchor(),
                        s.stageAnchor());
                } else {
                    stage.relocate(
                        s.screenAnchor(),
                        s.screenPadding(),
                        s.stageAnchor(),
                        s.anchorPolicy());
                }
            } else {
                stage.relocate(
                    s.screen(),
                    s.screenAnchor(),
                    s.screenPadding(),
                    s.stageAnchor(),
                    s.anchorPolicy());
            }
        }
    }
}
