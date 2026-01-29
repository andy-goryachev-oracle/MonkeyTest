/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * Virtual keyboard test page.
 */
public class VirtualKeyboardPage extends TestPaneBase {
    
    // FXVK:58
    private enum Type {
        TEXT("text", 0),
        NUMERIC("numeric", 1),
        URL("url", 2),
        EMAIL("email", 3);

        public final String text;
        public final int type;

        Type(String text, int type) {
            this.text = text;
            this.type = type;
        }
    };
    private final static String VK_TYPE_PROP_KEY = "vkType";

    public VirtualKeyboardPage() {
        super("VirtualKeyboardPage");
        
        TextField text = create(Type.TEXT);
        TextField numeric = create(Type.NUMERIC);
        TextField url = create(Type.URL);
        TextField email = create(Type.EMAIL);
        
        TextArea info = new TextArea("""
            The FX virtual keyboard must be enabled by adding the following command line argument:

            -Dcom.sun.javafx.virtualKeyboard=javafx
            """);
        info.setWrapText(true);
        info.setEditable(false);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHgrow(Priority.SOMETIMES);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);

        RowConstraints r0 = new RowConstraints();
        r0.setVgrow(Priority.SOMETIMES);

        RowConstraints rFill = new RowConstraints();
        rFill.setVgrow(Priority.ALWAYS);

        GridPane p = new GridPane();
        p.setPadding(new Insets(10));
        p.setHgap(10);
        p.setVgap(10);
        p.getColumnConstraints().setAll(c1, c2);
        p.getRowConstraints().setAll(r0, r0, r0, r0, rFill);
        p.add(text, 0, 0);
        p.add(numeric, 0, 1);
        p.add(url, 0, 2);
        p.add(email, 0, 3);
        p.add(info, 0, 4, 2, 1);
        
        setContent(p);
    }

    private static TextField create(Type type) {
        TextField t = new TextField();
        t.setPromptText(type.text);
        t.getProperties().put(VK_TYPE_PROP_KEY, type.type);
        return t;
    }
}
