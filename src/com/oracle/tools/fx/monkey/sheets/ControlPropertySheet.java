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

import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.PickResult;
import com.oracle.tools.fx.monkey.options.ObjectOption;
import com.oracle.tools.fx.monkey.util.ImageTools;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.OptionWindow;

/**
 * Control Property Sheet.
 */
public class ControlPropertySheet {
    public static void appendTo(OptionPane op, Control control) {
        op.section("Control");
        op.option("Context Menu:", contextMenuOptions("contextMenu", control));
        op.option("Tooltip:", tooltipOption("tooltip", control.tooltipProperty()));
        // region
        RegionPropertySheet.appendTo(op, control);
    }

    public static Node tooltipOption(String name, ObjectProperty<Tooltip> p) {
        ObjectOption<Tooltip> op = new ObjectOption<>(name, p);
        op.addChoice("<null>", null);
        op.addChoiceSupplier("simple text", () -> createSimpleTextTooltip());
        op.addChoiceSupplier("with image", () -> createTooltipWithImage());
        op.selectInitialValue();
        return op;
    }

    private static Tooltip createSimpleTextTooltip() {
        Tooltip t = new Tooltip("simple text tooltip");
        return t;
    }

    private static Tooltip createTooltipWithImage() {
        Tooltip t = new Tooltip("tooltip with image");
        t.setGraphic(ImageTools.createImageView(128, 96));
        return t;
    }

    public static ObjectOption<ContextMenu> contextMenuOptions(String name, Control c) {
        Picker picker = new Picker();
        ObjectProperty<ContextMenu> p = c.contextMenuProperty();
        c.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, picker);
        ObjectOption<ContextMenu> op = new ObjectOption<>(name, p);
        op.addChoiceSupplier("Show Properties Monitor", () -> createShowNodePropertiesContextMenu(picker));
        op.addChoice("<null>", null);
        op.selectInitialValue();
        return op;
    }

    private static ContextMenu createShowNodePropertiesContextMenu(Picker picker) {
        AtomicReference<PickResult> ref = new AtomicReference();
        ContextMenu m = new ContextMenu() {
            @Override
            public void show(Node anchor, double screenX, double screenY) {
                ref.set(picker.getPickResult());
                super.show(anchor, screenX, screenY);
            }
            @Override public void hide() {
                super.hide();
                ref.set(null);
            }
        };
        MenuItem mi = new MenuItem("Show Properties Monitor...");
        mi.setOnAction((ev) -> openPropertiesMonitor(ref.get()));
        m.getItems().add(mi);
        return m;
    }

    private static void openPropertiesMonitor(PickResult pick) {
        if (pick == null) {
            return;
        }
        Node source = pick.getIntersectedNode();
        if (source == null) {
            return;
        }
        String name = source.getClass().getSimpleName();
        PropertiesMonitor p = new PropertiesMonitor(source);
        OptionWindow.open(source, "Properties: " + name, 800, 900, p);
    }

    static class Picker implements EventHandler<ContextMenuEvent> {
        private PickResult pick;

        @Override
        public void handle(ContextMenuEvent ev) {
            pick = ev.getPickResult();
        }

        public PickResult getPickResult() {
            return pick;
        }
    }
}
