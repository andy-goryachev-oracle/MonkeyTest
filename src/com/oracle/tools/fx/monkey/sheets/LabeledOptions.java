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
package com.oracle.tools.fx.monkey.sheets;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.text.TextAlignment;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.options.FontOption;
import com.oracle.tools.fx.monkey.options.GraphicOption;
import com.oracle.tools.fx.monkey.options.InsetsOption;
import com.oracle.tools.fx.monkey.options.TextChoiceOption;
import com.oracle.tools.fx.monkey.options.TextOption;
import com.oracle.tools.fx.monkey.util.OptionPane;

/**
 * Labeled Control Property Sheet.
 */
public class LabeledOptions {
    public static void appendTo(OptionPane op, boolean multiLine, Labeled label) {
        op.option("Alignment:", new EnumOption<>("alignment", Pos.class, label.alignmentProperty()));
        op.option("Content Display:", new EnumOption<>("contentDisplay", ContentDisplay.class, label.contentDisplayProperty()));
        op.option("Ellipsis String:", new TextOption("ellipsisString", label.ellipsisStringProperty()));
        op.option("Font:", new FontOption("font", false, label.fontProperty()));
        op.option("Graphic:", new GraphicOption("graphic", label.graphicProperty()));
        op.option("Padding:", new InsetsOption("padding", false, label.paddingProperty()));
        op.option("Line Spacing:", DoubleOption.lineSpacing("lineSpacing", label.lineSpacingProperty()));
        op.option("Text:", Options.textOption("text", multiLine, true, label.textProperty()));
        op.option("Text Alignment:", new EnumOption<>("textAlignment", TextAlignment.class, label.textAlignmentProperty()));
        op.option("Text Fill: TODO", null); // TODO text fill
        op.option("Text Overrun:", new EnumOption<>("textOverrun", OverrunStyle.class, label.textOverrunProperty()));
        op.option(new BooleanOption("mnemonicParsing", "mnemonic parsing", label.mnemonicParsingProperty()));
        op.option(new BooleanOption("underline", "underline", label.underlineProperty()));
        op.option(new BooleanOption("wrapText", "wrap text", label.wrapTextProperty()));

        // control
        ControlOptions.appendTo(op, label);
    }
}
