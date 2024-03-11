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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import com.oracle.tools.fx.monkey.util.FX;

/**
 * Simple Text Option Bound to a Property.
 * Presents a text field with an Edit button for mode complex text.
 */
// TODO combo box for history?
// TODO highlight special characters?
// TODO commit on ENTER instead of binding?
public class TextOption extends BorderPane {
    private final SimpleStringProperty property = new SimpleStringProperty();
    private final TextField textField;
    private final Button editButton;

    public TextOption(String name, StringProperty p) {
        FX.name(this, name);
        property.bindBidirectional(p);

        textField = new TextField();
        textField.textProperty().bindBidirectional(property);

        editButton = new Button("Edit");
        editButton.setDisable(true); // TODO

        setCenter(textField);
        setRight(editButton);
        setMargin(editButton, new Insets(0, 0, 0, 2));
    }
}
