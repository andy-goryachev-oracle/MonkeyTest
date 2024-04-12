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

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.OptionWindow;

/**
 * Properties... menu.
 */
public class PropertiesMenu {
    public static void openPropertiesDialog(Object parent, Node n) {
        String name;
        OptionPane op = new OptionPane();
        if(n instanceof Button t) {
            name = "Button";
            ButtonPropertySheet.appendTo(op, t);
        } else if(n instanceof CheckBox t) {
            name = "CheckBox";
            CheckBoxPropertySheet.appendTo(op, t);
        } else if (n instanceof Label t) {
            name = "Label";
            LabeledPropertySheet.appendTo(op, "Label", false, t);
        } else if(n instanceof TextArea t) {
            name = "TextArea";
            TextAreaPropertySheet.appendTo(op, t);
        } else if(n instanceof TextField t) {
            name = "TextField";
            TextFieldPropertySheet.appendTo(op, t, null);
        } else {
            // TODO other types
            System.err.println("property sheet not yet supported:" + n);
            return;
        }

        OptionWindow.open(parent, "Properties:" + name, 500, 800, op);
    }
}
