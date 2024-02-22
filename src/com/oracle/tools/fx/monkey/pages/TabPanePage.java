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
package com.oracle.tools.fx.monkey.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * TabPane Page.
 */
public class TabPanePage extends TestPaneBase {
    private final TabPane control;

    public TabPanePage() {
        FX.name(this, "TabPanePage");

        control = new TabPane();
        control.getTabs().addAll(
            new Tab("One", mkContent("Tab One Content")),
            new Tab("Two", mkContent("Tab Two Content")),
            new Tab("Three", mkContent("Tab Three Content")),
            new Tab("Four", mkContent("Tab Four Content"))
        );

        // TODO options
        control.setTabDragPolicy(TabDragPolicy.REORDER);
        control.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        OptionPane p = new OptionPane();
        p.label("TODO");
        //p.option(promptChoice.node());

        setContent(control);
        setOptions(p);
    }

    private Node mkContent(String text) {
        Label label = new Label(text);

        TextField textField = new TextField();
        textField.setPromptText("focus here");

        Button button = new Button("OK");

        VBox b = new VBox(5);
        b.setPadding(new Insets(0, 20, 0, 20));
        b.setAlignment(Pos.CENTER);
        b.getChildren().add(label);
        b.getChildren().add(textField);
        b.getChildren().add(button);
        return b;
    }
}
