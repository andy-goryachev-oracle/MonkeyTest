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

import javafx.geometry.NodeOrientation;
import javafx.scene.AccessibleRole;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import com.oracle.tools.fx.monkey.util.OptionPane;

/**
 * Node Property Sheet.
 */
public class NodeOptions {
    public static void appendTo(OptionPane op, Node node) {
        op.section("Node");

        op.option("Accessible Help: TODO", null); // TODO

        op.option("Accessible Role:", new EnumOption<>("accessibleRole", AccessibleRole.class, node.accessibleRoleProperty()));

        op.option("Accessible Role Description: TODO", null); // TODO

        op.option("Accessible Text: TODO", null); // TODO

        op.option("Blend Mode:", new EnumOption<>("blendMode", BlendMode.class, node.blendModeProperty()));

        op.option(new BooleanOption("cache", "cache", node.cacheProperty()));

        op.option("Cache Hint:", new EnumOption<>("cacheHint", CacheHint.class, node.cacheHintProperty()));

        op.option("Clip: TODO", null); // TODO

        op.option("Cursor: TODO", null); // TODO

        op.option("Depth Test:", new EnumOption<>("depthText", CacheHint.class, node.cacheHintProperty()));

        op.option(new BooleanOption("disable", "disable", node.disableProperty()));

        op.option("Effect: TODO", null); // TODO

        op.option(new BooleanOption("focusTraversable", "focus traversable", node.focusTraversableProperty()));

        op.option("Id:", new TextOption("id", node.idProperty()));

        op.option("Input Method Requests: TODO", null); // TODO

        op.option("Layout X: TODO", null); // TODO

        op.option("Layout Y: TODO", null); // TODO

        op.option(new BooleanOption("managed", "managed", node.managedProperty()));

        op.option(new BooleanOption("mouseTransparent", "mouse transparent", node.mouseTransparentProperty()));

        op.option("Node Orientation:", new EnumOption<>("nodeOrientation", NodeOrientation.class, node.nodeOrientationProperty()));

        op.option("On Various Events: TODO", null); // TODO own section?

        op.option("Opacity: TODO", null); // TODO

        op.option(new BooleanOption("pickOnBounds", "pick on bounds", node.pickOnBoundsProperty()));

        op.option("Rotate: TODO", null); // TODO

        op.option("Rotation Axis: TODO", null); // TODO

        op.option("Scale X: TODO", null); // TODO
        op.option("Scale Y: TODO", null); // TODO
        op.option("Scale Z: TODO", null); // TODO

        op.option("Style:", new TextOption("style", node.styleProperty()));

        op.option("Translate X: TODO", null); // TODO
        op.option("Translate Y: TODO", null); // TODO
        op.option("Translate Z: TODO", null); // TODO

        op.option("User Data: TODO", null); // TODO

        op.option("View Order: TODO", null); // TODO

        op.option(new BooleanOption("visible", "visible", node.visibleProperty()));
    }
}
