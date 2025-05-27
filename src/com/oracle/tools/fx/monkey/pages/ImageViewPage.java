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
import javafx.scene.image.ImageView;
import com.oracle.tools.fx.monkey.Loggers;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.sheets.NodePropertySheet;
import com.oracle.tools.fx.monkey.sheets.Options;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * ImageView Page.
 */
public class ImageViewPage extends TestPaneBase {
    private final ImageView imageView;

    public ImageViewPage() {
        super("ImageViewPage");

        imageView = new ImageView() {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute a, Object... ps) {
                Object v = super.queryAccessibleAttribute(a, ps);
                Loggers.accessibility.log(a, v);
                return v;
            }
        };

        OptionPane op = new OptionPane();
        op.section("ImageView");
        op.option("Fit Height:", DoubleOption.of("fitHeight", imageView.fitHeightProperty(), -1.0, 10.0, 100.0, 500.0));
        op.option("Fit Width:", DoubleOption.of("fitWidth", imageView.fitWidthProperty(), -1.0, 10.0, 100.0, 500.0));
        op.option("Image:", Options.createImageOption(imageView.imageProperty()));
        op.option(new BooleanOption("preserveRatio", "preserve ratio", imageView.preserveRatioProperty()));
        op.option(new BooleanOption("smooth", "smooth", imageView.smoothProperty()));
        // setViewport(Rectangle2D)
        op.option("X:", DoubleOption.of("x", imageView.xProperty(), -10.0, 0.0, 10));
        op.option("Y:", DoubleOption.of("y", imageView.yProperty(), -10.0, 0.0, 10));
        NodePropertySheet.appendTo(op, imageView);

        setContent(imageView);
        setOptions(op);
    }
}
