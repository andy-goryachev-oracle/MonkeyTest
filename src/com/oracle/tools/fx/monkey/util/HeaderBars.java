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
package com.oracle.tools.fx.monkey.util;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HeaderBar;
import javafx.scene.paint.Color;

/**
 * HeaderBar (Preview Feature) choices and related methods.
 */
public class HeaderBars {

    public static enum Choice {
        NONE,
        SIMPLE,
        SPLIT;

        @Override
        public String toString() {
            return switch(this) {
                case NONE -> "<none>";
                case SIMPLE ->"Simple";
                case SPLIT -> "Split";
            };
        }
    }

    public static Parent createSimple(Parent n) {
        HeaderBar headerBar = new HeaderBar();
        headerBar.setBackground(Background.fill(Color.LIGHTSKYBLUE));
        headerBar.setCenter(searchField());

        BorderPane bp = new BorderPane();
        bp.setTop(headerBar);
        bp.setCenter(n);
        return bp;
    }

    public static Parent createSplit(Parent n) {
        HeaderBar leftHeaderBar = new HeaderBar();
        leftHeaderBar.setBackground(Background.fill(Color.VIOLET));
        leftHeaderBar.setLeft(new Button("Left"));
        leftHeaderBar.setCenter(searchField());
        leftHeaderBar.setRightSystemPadding(false);

        HeaderBar rightHeaderBar = new HeaderBar();
        rightHeaderBar.setBackground(Background.fill(Color.LIGHTSKYBLUE));
        rightHeaderBar.setLeftSystemPadding(false);
        rightHeaderBar.setRight(new Button("Right"));

        BorderPane left = new BorderPane();
        left.setTop(leftHeaderBar);
        left.setCenter(n);

        BorderPane right = new BorderPane();
        right.setTop(rightHeaderBar);

        return new SplitPane(left, right);
    }

    private static TextField searchField() {
        TextField f = new TextField();
        f.setPromptText("Search...");
        f.setMaxWidth(300);
        return f;
    }
}
