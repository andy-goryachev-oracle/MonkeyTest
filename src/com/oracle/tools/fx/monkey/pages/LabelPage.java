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

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.text.TextAlignment;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.ControlOptions;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.options.FontOption;
import com.oracle.tools.fx.monkey.options.GraphicOption;
import com.oracle.tools.fx.monkey.options.InsetsOption;
import com.oracle.tools.fx.monkey.options.TextOption;
import com.oracle.tools.fx.monkey.util.EnterTextDialog;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.Templates;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.TextSelector;

/**
 * Label Page
 */
public class LabelPage extends TestPaneBase {
    private final Label label;

    public LabelPage() {
        FX.name(this, "LabelPage");

        label = new Label();
        setContent(label);

        // JDK-8092102
//        control.textTruncatedProperty().addListener((s,p,c) -> {
//            System.err.println("truncated: " + c);
//        });

        // TODO
        TextSelector textSelector = TextSelector.fromPairs(
            "textSelector",
            (v) -> label.setText(v),
            Templates.multiLineTextPairs()
        );

        Button editButton = new Button("Enter Text");
        editButton.setOnAction((ev) -> {
            String text = label.getText();
            new EnterTextDialog(this, text, (v) -> {
                label.setText(v);
            }).show();
        });

        OptionPane op = new OptionPane();
        op.section("Label");

        op.label("Text:");
        op.option(textSelector.node());
        op.option(editButton);

        op.label("Alignment:");
        op.option(new EnumOption<>("alignment", Pos.class, label.alignmentProperty()));

        op.label("Content Display:");
        op.option(new EnumOption<>("contentDisplay", ContentDisplay.class, label.contentDisplayProperty()));

        op.label("Ellipsis String:");
        op.option(new TextOption("ellipsisString", label.ellipsisStringProperty()));

        op.label("Font:");
        op.option(new FontOption("font", false, label.fontProperty()));

        op.label("Graphic:");
        op.option(new GraphicOption("graphic", label.graphicProperty()));

        op.label("Padding:");
        op.option(new InsetsOption("padding", false, label.paddingProperty()));

        op.label("Line Spacing:");
        op.option(DoubleOption.lineSpacing("lineSpacing", label.lineSpacingProperty()));

        op.label("Text Alignment:");
        op.option(new EnumOption<>("textAlignment", TextAlignment.class, label.textAlignmentProperty()));

        op.label("Text Fill: TODO"); // TODO text fill

        op.label("Text Overrun:");
        op.option(new EnumOption<>("textOverrun", OverrunStyle.class, label.textOverrunProperty()));

        op.option(new BooleanOption("mnemonicParsing", "mnemonic parsing", label.mnemonicParsingProperty()));

        op.option(new BooleanOption("underline", "underline", label.underlineProperty()));

        op.option(new BooleanOption("wrapText", "wrap text", label.wrapTextProperty()));

        // control
        ControlOptions.appendTo(label, op);
        setOptions(op);
    }
}
