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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.skin.ProgressIndicatorSkin;
import javafx.scene.layout.VBox;
import com.oracle.tools.fx.monkey.Loggers;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.sheets.ControlPropertySheet;
import com.oracle.tools.fx.monkey.util.HasSkinnable;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * ProgressIndicator Page.
 */
public class ProgressIndicatorPage extends TestPaneBase implements HasSkinnable {
    private final ProgressIndicator indeterminate;
    private final ProgressIndicator determinate;

    public ProgressIndicatorPage() {
        super("ProgressIndicatorPage");

        indeterminate = new ProgressIndicator() {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute a, Object... ps) {
                Object v = super.queryAccessibleAttribute(a, ps);
                Loggers.accessibility.log(a, v);
                return v;
            }
        };

        determinate = new ProgressIndicator() {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute a, Object... ps) {
                Object v = super.queryAccessibleAttribute(a, ps);
                Loggers.accessibility.log(a, v);
                return v;
            }
        };

        OptionPane op = new OptionPane();
        op.section("ProgressIndicator (Indeterminate)");
        op.option("Progress:", DoubleOption.of("iProgress", indeterminate.progressProperty(), 0.0));
        op.section("ProgressIndicator (Determinate)");
        op.option("Progress:", DoubleOption.of("dProgress", determinate.progressProperty(), 0.0));
        ControlPropertySheet.appendTo(op, indeterminate);
        ControlPropertySheet.appendTo(op, determinate);

        setContent(new VBox(indeterminate, determinate));
        setOptions(op);
    }

    @Override
    public void nullSkin() {
        indeterminate.setSkin(null);
        determinate.setSkin(null);
    }

    @Override
    public void newSkin() {
        indeterminate.setSkin(new ProgressIndicatorSkin(indeterminate));
        determinate.setSkin(new ProgressIndicatorSkin(determinate));
    }
}
