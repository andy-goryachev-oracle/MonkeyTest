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
import com.oracle.tools.fx.monkey.options.GraphicOption;
import com.oracle.tools.fx.monkey.options.InsetsOption;
import com.oracle.tools.fx.monkey.util.EnterTextDialog;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.FontSelector;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.Templates;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.TextOption;
import com.oracle.tools.fx.monkey.util.TextSelector;

/**
 * Label Page
 */
public class LabelPage extends TestPaneBase {
    private final Label control;

    public LabelPage() {
        FX.name(this, "LabelPage");

        control = new Label();
        setContent(control);

        // JDK-8092102
//        control.textTruncatedProperty().addListener((s,p,c) -> {
//            System.err.println("truncated: " + c);
//        });
        
        TextSelector textSelector = TextSelector.fromPairs(
            "textSelector",
            (v) -> control.setText(v),
            Templates.multiLineTextPairs()
        );

        Button editButton = new Button("Enter Text");
        editButton.setOnAction((ev) -> {
            String text = control.getText();
            new EnterTextDialog(this, text, (v) -> {
                control.setText(v);
            }).show();
        });

        TextOption ellipsisString = new TextOption("ellipsisString", control.ellipsisStringProperty());

        // TODO different chooser
        FontSelector fontSelector = new FontSelector("font", (v) -> {
            control.setFont(v);
        });

        OptionPane op = new OptionPane();
        op.section("Label");
        op.label("Text:");
        op.option(textSelector.node());
        op.option(editButton);
        op.label("Alignment:");
        op.option(new EnumOption<>("alignment", Pos.class, control.alignmentProperty()));
        op.label("Content Display:");
        op.option(new EnumOption<>("contentDisplay", ContentDisplay.class, control.contentDisplayProperty()));
        op.label("Ellipsis String:");
        op.option(ellipsisString);
        op.label("Font:");
        op.option(fontSelector.fontNode());
        op.label("Font Size:");
        op.option(fontSelector.sizeNode());
        op.label("Graphic:");
        op.option(new GraphicOption("graphic", control.graphicProperty()));
        op.label("Padding:");
        op.option(new InsetsOption("padding", false, control.paddingProperty()));
        op.label("Line Spacing:");
        op.option(DoubleOption.of("lineSpacing", control.lineSpacingProperty(), 0, 1, 2, 3.14, 10, 33.33, 100));
        op.label("Text Alignment:");
        op.option(new EnumOption<>("textAlignment", TextAlignment.class, control.textAlignmentProperty()));
        op.label("Text Fill: TODO");// TODO text fill
        op.label("Text Overrun:");
        op.option(new EnumOption<>("textOverrun", OverrunStyle.class, control.textOverrunProperty()));
        op.option(new BooleanOption("mnemonicParsing", "mnemonic parsing", control.mnemonicParsingProperty()));
        op.option(new BooleanOption("underline", "underline", control.underlineProperty()));
        op.option(new BooleanOption("wrapText", "wrap text", control.wrapTextProperty()));

        // control
        ControlOptions.appendTo(control, op);
        setOptions(op);
    }
}
