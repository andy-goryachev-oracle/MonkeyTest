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

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.AnchorPoint;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.AnchorPolicy;
import javafx.stage.Screen;
import com.oracle.tools.fx.monkey.options.StageRelocationOption;

/**
 * Relocation option popup panel.
 */
public class RelocationPane extends GridPane {

    private final ObjectProperty<StageRelocationOption.Spec> property;
    private final ComboBox<Screen> screenField;
    private final ComboBox<NamedValue<AnchorPoint>> screenAnchorField;
    private final ComboBox<Insets> screenPaddingField;
    private final ComboBox<NamedValue<AnchorPoint>> stageAnchorField;
    private final ComboBox<AnchorPolicy> anchorPolicyField;
    
    public RelocationPane(ObjectProperty<StageRelocationOption.Spec> prop, Runnable onCompletion) {
        this.property = prop;
        FX.name(this, "RelocationPane");

        screenField = new ComboBox<>();
        screenField.getItems().setAll(collectScreens());
        screenField.setConverter(new ScreenConverter());
        FX.name(screenField, "screen");

        screenAnchorField = new ComboBox<>();
        screenAnchorField.getItems().setAll(collectAnchors());
        Utils.setUniversalConverter(screenAnchorField);
        FX.name(screenAnchorField, "screenAnchor");

        screenPaddingField = new ComboBox<>();
        screenPaddingField.getItems().setAll(collectInsets());
        Utils.setUniversalConverter(screenPaddingField);
        FX.name(screenPaddingField, "screenPadding");

        stageAnchorField = new ComboBox<>();
        stageAnchorField.getItems().setAll(collectAnchors());
        Utils.setUniversalConverter(stageAnchorField);
        FX.name(stageAnchorField, "stageAnchor");

        anchorPolicyField = new ComboBox<>();
        anchorPolicyField.getItems().setAll(collectAnchorPolicies());
        Utils.setUniversalConverter(anchorPolicyField);
        FX.name(anchorPolicyField, "anchorPolicy");

        Button ok = new Button("OK");
        ButtonBar.setButtonData(ok, ButtonData.OK_DONE);
        ok.setOnAction((_) -> {
            commit();
            onCompletion.run();
        });
        Button cancel = new Button("Cancel");
        cancel.setOnAction((_) -> {
            onCompletion.run();
        });
        ButtonBar.setButtonData(cancel, ButtonData.CANCEL_CLOSE);
        ButtonBar buttons = new ButtonBar();
        buttons.getButtons().setAll(ok, cancel);

        // layout
        setPrefWidth(400);
        setHgap(5);
        setVgap(5);
        setBackground(Background.fill(Color.LIGHTGRAY));
        setPadding(new Insets(5));
        setFocusTraversable(true);

        add(new Label("Screen:"), 0, 0);
        add(screenField, 1, 0);
        add(new Label("Screen Anchor:"), 0, 1);
        add(screenAnchorField, 1, 1);
        add(new Label("Screen Padding:"), 0, 2);
        add(screenPaddingField, 1, 2);
        add(new Label("Stage Anchor:"), 0, 3);
        add(stageAnchorField, 1, 3);
        add(new Label("Anchor Policy:"), 0, 4);
        add(anchorPolicyField, 1, 4);
        add(buttons, 0, 5, 2, 1);

        StageRelocationOption.Spec s = prop.get();
        if (s != null) {
            Utils.selectItem(screenField, s.screen());
            Utils.selectItem(screenAnchorField, s.screenAnchor());
            Utils.selectItem(screenPaddingField, s.screenPadding());
            Utils.selectItem(stageAnchorField, s.stageAnchor());
            Utils.selectItem(anchorPolicyField, s.anchorPolicy());
        }
    }

    private static List<Screen> collectScreens() {
        ArrayList<Screen> v = new ArrayList<>();
        v.add(null);
        v.addAll(Screen.getScreens());
        return v;
    }

    private static List<NamedValue<AnchorPoint>> collectAnchors() {
        ArrayList<NamedValue<AnchorPoint>> v = new ArrayList<>();
        v.add(null);
        v.add(new NamedValue<>("TOP_LEFT", AnchorPoint.TOP_LEFT));
        v.add(new NamedValue<>("TOP_CENTER", AnchorPoint.TOP_CENTER));
        v.add(new NamedValue<>("TOP_RIGHT", AnchorPoint.TOP_RIGHT));
        v.add(new NamedValue<>("CENTER_LEFT", AnchorPoint.CENTER_LEFT));
        v.add(new NamedValue<>("CENTER", AnchorPoint.CENTER));
        v.add(new NamedValue<>("CENTER_RIGHT", AnchorPoint.CENTER_RIGHT));
        v.add(new NamedValue<>("BOTTOM_LEFT", AnchorPoint.BOTTOM_LEFT));
        v.add(new NamedValue<>("BOTTOM_CENTER", AnchorPoint.BOTTOM_CENTER));
        v.add(new NamedValue<>("BOTTOM_RIGHT", AnchorPoint.BOTTOM_RIGHT));
        v.add(new NamedValue<>("333,333,absolute", AnchorPoint.absolute(333, 333)));
        v.add(new NamedValue<>("1,1,proportional", AnchorPoint.proportional(1, 1)));
        v.add(new NamedValue<>("-333,-333,absolute", AnchorPoint.absolute(-333, -333)));
        v.add(new NamedValue<>("-1,-1,proportional", AnchorPoint.proportional(-1, -1)));
        return v;
    }

    private static List<Insets> collectInsets() {
        ArrayList<Insets> v = new ArrayList<>();
        v.add(new Insets(0));
        v.add(new Insets(100));
        return v;
    }

    private static List<AnchorPolicy> collectAnchorPolicies() {
        ArrayList<AnchorPolicy> v = new ArrayList<>();
        v.add(null);
        for (AnchorPolicy p: AnchorPolicy.values()) {
            v.add(p);
        }
        return v;
    }

    private void commit() {
        StageRelocationOption.Spec spec = new StageRelocationOption.Spec(
            Utils.getSelectedItem(screenField),
            Utils.getSelectedNamedItem(screenAnchorField),
            Utils.getSelectedItem(screenPaddingField),
            Utils.getSelectedNamedItem(stageAnchorField),
            Utils.getSelectedItem(anchorPolicyField));
        property.set(spec);
    }
}
