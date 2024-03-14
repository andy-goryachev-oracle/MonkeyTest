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

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.ObjectOption;
import com.oracle.tools.fx.monkey.sheets.LabeledOptions;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * TitledPane Page
 */
public class TitledPanePage extends TestPaneBase {
    private final TitledPane titledPane;

    public TitledPanePage() {
        FX.name(this, "TitledPane");

        titledPane = new TitledPane();

//        TextChoiceOption textOption = Options.singleLineTextOption("text", true, titledPane.textProperty());
//        textOption.removeChoice("Writing Systems");

        ObjectOption<Node> contentOption = new ObjectOption<>("content", titledPane.contentProperty());
        contentOption.addChoiceSupplier("Label", () -> new Label("Label"));
        contentOption.addChoiceSupplier("AnchorPane", () -> makeAnchorPane());
        contentOption.addChoiceSupplier("<null>", () -> null);

        OptionPane op = new OptionPane();
        op.section("TitledPane");
        op.option(new BooleanOption("animated", "animated", titledPane.animatedProperty()));
        op.option(new BooleanOption("collapsible", "collapsible", titledPane.collapsibleProperty()));
//        op.option("Text:", textOption);
        op.option("Content:", contentOption);
        op.option(new BooleanOption("expanded", "expanded", titledPane.expandedProperty()));

        op.section("Labeled");
        LabeledOptions.appendTo(op, false, titledPane);

        setContent(titledPane);
        setOptions(op);

        contentOption.selectFirst();
    }

    protected Node makeAnchorPane() {
        VBox b = new VBox(new TextField("First"), new TextField("Second"));
        AnchorPane p = new AnchorPane(b);
        AnchorPane.setTopAnchor(b, 10.0);
        AnchorPane.setBottomAnchor(b, 10.0);
        AnchorPane.setLeftAnchor(b, 100.0);
        AnchorPane.setRightAnchor(b, 50.0);
        return p;
    }
}
