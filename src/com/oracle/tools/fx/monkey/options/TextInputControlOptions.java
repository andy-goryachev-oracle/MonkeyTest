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

import javafx.scene.control.TextInputControl;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.Templates;
import com.oracle.tools.fx.monkey.util.TextChoiceOption;
import com.oracle.tools.fx.monkey.util.TextSelector;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 *
 */
public class TextInputControlOptions {
    public static void appendTo(OptionPane op, boolean multiline, TextInputControl control) {
        TextChoiceOption promptTextOption = new TextChoiceOption("promptText", true, control.promptTextProperty());
        Utils.fromPairs(Templates.singleLineTextPairs(), (k,v) -> promptTextOption.addChoice(k, v));
        
        TextChoiceOption textOption = new TextChoiceOption("text", true, control.textProperty());
        Utils.fromPairs(
            multiline ? Templates.multiLineTextPairs() : Templates.singleLineTextPairs(),
            (k,v) -> textOption.addChoice(k, v)
        );

        op.section("TextInputControl");

        op.option(new BooleanOption("editable", "editable", control.editableProperty()));
        op.option("Font:", new FontOption("font", false, control.fontProperty()));
        op.option("Prompt Text:", promptTextOption);
        op.option("Text:", textOption);
        op.option("Text Formatter: TODO", null); // TODO

        ControlOptions.appendTo(op, control);
    }
}