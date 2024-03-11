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

        op.label("Accessible Help: TODO"); // TODO

        op.label("Accessible Role:");
        op.option(new EnumOption<>("accessibleRole", AccessibleRole.class, node.accessibleRoleProperty()));

        op.label("Accessible Role Description: TODO"); // TODO

        op.label("Accessible Text: TODO"); // TODO

        op.label("Blend Mode:");
        op.option(new EnumOption<>("blendMode", BlendMode.class, node.blendModeProperty()));

        op.option(new BooleanOption("cache", "cache", node.cacheProperty()));

        op.label("Cache Hint:");
        op.option(new EnumOption<>("cacheHint", CacheHint.class, node.cacheHintProperty()));

        op.label("Clip: TODO"); // TODO

        op.label("Cursor: TODO"); // TODO

        op.label("Depth Test:");
        op.option(new EnumOption<>("depthText", CacheHint.class, node.cacheHintProperty()));

        op.option(new BooleanOption("disable", "disable", node.disableProperty()));

        op.label("Effect: TODO"); // TODO

        op.option(new BooleanOption("focusTraversable", "focus traversable", node.focusTraversableProperty()));

        op.label("Id:");
        op.option(new TextOption("id", node.idProperty()));

        op.label("Input Method Requests: TODO"); // TODO

        op.label("Layout X: TODO"); // TODO

        op.label("Layout Y: TODO"); // TODO

        op.option(new BooleanOption("managed", "managed", node.managedProperty()));

        op.option(new BooleanOption("mouseTransparent", "mouse transparent", node.mouseTransparentProperty()));

        op.label("Node Orientation:");
        op.option(new EnumOption<>("nodeOrientation", NodeOrientation.class, node.nodeOrientationProperty()));

        op.label("On Various Events: TODO"); // TODO own section?

        op.label("Opacity: TODO"); // TODO

        op.option(new BooleanOption("pickOnBounds", "pick on bounds", node.pickOnBoundsProperty()));

        op.label("Rotate: TODO"); // TODO

        op.label("Rotation Axis: TODO"); // TODO

        op.label("Scale X: TODO"); // TODO
        op.label("Scale Y: TODO"); // TODO
        op.label("Scale Z: TODO"); // TODO

        op.label("Style:");
        op.option(new TextOption("style", node.styleProperty()));

        op.label("Translate X: TODO"); // TODO
        op.label("Translate Y: TODO"); // TODO
        op.label("Translate Z: TODO"); // TODO

        op.label("User Data: TODO"); // TODO

        op.label("View Order: TODO"); // TODO

        op.option(new BooleanOption("visible", "visible", node.visibleProperty()));
    }
}
