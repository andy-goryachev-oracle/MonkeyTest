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
    // TODO pass the control instance
    public static void appendTo(Region region, OptionPane op) {
        BorderSelector border = new BorderSelector("border", region.borderProperty());

        op.section("Region");
        op.label("Background: TODO"); // TODO
        op.label("Border:");
        op.option(border);
        op.label("Set Cache Shape: TODO"); // TODO
        op.label("Set Center Shape: TODO"); // TODO
        op.label("Set Max Height: TODO"); // TODO
        op.label("Set Max Width: TODO"); // TODO
        op.label("Set Min Height: TODO"); // TODO
        op.label("Set Min Width: TODO"); // TODO
        op.label("Set Opaque Insets: TODO"); // TODO
        op.label("Set Padding: TODO"); // TODO
        op.label("Set Pref Height: TODO"); // TODO
        op.label("Set Pref Width: TODO"); // TODO
        op.label("Set Scale Shape: TODO"); // TODO
        op.label("Set Shape: TODO"); // TODO
        op.label("Set Snap to Pixel: TODO"); // TODO

        // TODO node?  too many properties?
    }
}
