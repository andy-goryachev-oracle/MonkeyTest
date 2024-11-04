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
package com.oracle.tools.fx.monkey.tools;

import java.util.Arrays;
import java.util.Comparator;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HeaderBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.oracle.tools.fx.monkey.util.CustomPane;
import com.oracle.tools.fx.monkey.util.FX;

/**
 * Custom Stage Tester.
 */
public class CustomStage extends Stage {
    public CustomStage(StageStyle style, NodeOrientation ori) {
        super(style);

        setTitle("Stage [" + style + "]");
        setWidth(700);
        setHeight(500);

        setUiPanel();
        getScene().setNodeOrientation(ori);
    }

    void setContent(Parent n) {
        if (getStyle() == StageStyle.EXTENDED) {
            if (!Platform.isSupported(ConditionalFeature.EXTENDED_WINDOW)) {
                System.err.println("CustomStage: ConditionalFeature.EXTENDED_WINDOW is not supported!");
            }

            HeaderBar h = new HeaderBar();
            h.setOnContextMenuRequested((ev) -> createHeaderPopupMenu(ev, h));
            h.setStyle("-fx-background-color:salmon;");
            h.setLeading(new Label("Leading"));
            h.setCenter(new Label("Center"));
            h.setTrailing(new Label("Trailing"));
            BorderPane p = new BorderPane(n);
            p.setTop(h);
            n = p;
        }
        Scene sc = new Scene(n);
        sc.setOnContextMenuRequested(this::createPopupMenu);
        setScene(sc);
    }

    void createHeaderPopupMenu(ContextMenuEvent ev, HeaderBar h) {
        ContextMenu m = new ContextMenu();
        FX.item(m, "Preferred Height: USE_COMPUTED_SIZE", () -> h.setPrefHeight(Region.USE_COMPUTED_SIZE));
        FX.item(m, "10", () -> h.setPrefHeight(10));
        FX.item(m, "100", () -> h.setPrefHeight(100));
        FX.item(m, "1,000", () -> h.setPrefHeight(1_000));
        FX.separator(m);
        FX.item(m, "leftSystemInset=" + h.getLeftSystemInset());
        FX.item(m, "rightSystemInset=" + h.getRightSystemInset());
        m.show(this, ev.getScreenX(), ev.getScreenY());
        ev.consume();
    }

    void createPopupMenu(ContextMenuEvent ev) {
        ContextMenu m = new ContextMenu();
        FX.item(m, "Irregular Shape", this::setIrregularShape);
        FX.item(m, "UI Panel", this::setUiPanel);
        FX.separator(m);
        FX.item(m, "Close", this::hide);
        m.show(this, ev.getScreenX(), ev.getScreenY());
        ev.consume();
    }

    void setIrregularShape() {
        Circle c = new Circle(100, Color.SALMON);
        StackPane g = new StackPane(c);
        g.setBackground(Background.fill(Color.TRANSPARENT));
        setContent(g);
    }

    void setUiPanel() {
        CustomPane p = CustomPane.create();
        setContent(p);
    }

    public static void addMenu(MenuBar m) {
        StageStyle[] styles = StageStyle.values();
        Arrays.sort(styles, new Comparator<StageStyle>() {
            @Override
            public int compare(StageStyle a, StageStyle b) {
                return a.toString().compareTo(b.toString());
            }
        });

        for (StageStyle st: styles) {
            FX.item(m, st.toString(), () -> {
                NodeOrientation ori = m.getEffectiveNodeOrientation();
                new CustomStage(st, ori).show();
            });
        }
    }
}
