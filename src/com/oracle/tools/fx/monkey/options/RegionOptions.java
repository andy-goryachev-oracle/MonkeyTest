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

import javafx.scene.layout.Region;
import com.oracle.tools.fx.monkey.util.OptionPane;

/**
 * Region Property Sheet.
 */
public class RegionOptions {
    public static void appendTo(OptionPane op, Region region) {
        op.section("Region");
        op.label("Background: TODO"); // TODO

        op.label("Border:");
        op.option(new BorderOption("border", region.borderProperty()));

        op.option(new BooleanOption("cacheShape", "cache shape", region.cacheShapeProperty()));

        op.option(new BooleanOption("centerShape", "center shape", region.centerShapeProperty()));

        op.label("Max Height:");
        op.option(DoubleOption.forRegion("maxHeight", region.maxHeightProperty()));
        op.label("Max Width:");
        op.option(DoubleOption.forRegion("maxWidth", region.maxWidthProperty()));
        op.label("Min Height:");
        op.option(DoubleOption.forRegion("minHeight", region.minHeightProperty()));
        op.label("Min Width:");
        op.option(DoubleOption.forRegion("minWidth", region.minWidthProperty()));

        op.label("Opaque Insets:");
        op.option(new InsetsOption("opaqueInsets", true, region.opaqueInsetsProperty()));

        op.label("Padding:");
        op.option(new InsetsOption("padding", false, region.paddingProperty()));

        op.label("Pref Height:");
        op.option(DoubleOption.forRegion("prefHeight", region.prefHeightProperty()));
        op.label("Pref Width:");
        op.option(DoubleOption.forRegion("prefWidth", region.prefWidthProperty()));

        op.option(new BooleanOption("scaleShape", "scale shape", region.scaleShapeProperty()));

        op.label("Shape: TODO"); // TODO

        op.option(new BooleanOption("snapToPixel", "snap to pixel", region.snapToPixelProperty()));

        NodeOptions.appendTo(op, region);
    }
}
