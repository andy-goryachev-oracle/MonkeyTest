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
package com.oracle.tools.fx.monkey.pages;

import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import com.oracle.tools.fx.monkey.Loggers;
import com.oracle.tools.fx.monkey.options.PaneContentOptions;
import com.oracle.tools.fx.monkey.sheets.RegionPropertySheet;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 * BorderPane Page.
 */
public class BorderPanePage extends TestPaneBase {
    private final BorderPane pane;

    public BorderPanePage() {
        super("BorderPanePage");

        pane = new BorderPane() {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute a, Object... ps) {
                Object v = super.queryAccessibleAttribute(a, ps);
                Loggers.accessibility.log(a, v);
                return v;
            }
        };

        Button clear = new Button("Remove All");
        clear.setOnAction((ev) -> {
            pane.getChildren().clear();
        });

        OptionPane op = new OptionPane();
        op.section("BorderPane");
        op.option("Bottom:", PaneContentOptions.childOption("center", pane.bottomProperty()));
        op.option("Center:", PaneContentOptions.childOption("center", pane.centerProperty()));
        op.option("Left:", PaneContentOptions.childOption("left", pane.leftProperty()));
        op.option("Right:", PaneContentOptions.childOption("right", pane.rightProperty()));
        op.option("Top:", PaneContentOptions.childOption("top", pane.topProperty()));
        op.option(Utils.buttons(clear));
        RegionPropertySheet.appendTo(op, pane);

        setContent(pane);
        setOptions(op);
    }
}
