/*
 * Copyright (c) 2024, 2025, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import java.util.function.Consumer;
import javafx.application.ColorScheme;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.sheets.Options;
import com.oracle.tools.fx.monkey.util.CustomPane;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;

/**
 * Custom Stage Window.
 */
public class CustomStage extends Stage {

    public enum StageContent {
        EMPTY("Empty"),
        IRREGULAR_SHAPE("Irregular Shape"),
        NESTED_STAGES("Nested Stages"),
        TEXT_AREA("TextArea"),
        UI_PANEL("UI Panel");

        private final String text;

        StageContent(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum TargetLocation {
        SAME_SCREEN("Same Screen"),
        OTHER_SCREEN("Other Screen"),
        OUTSIDE("Outside");

        private final String text;

        TargetLocation(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private final Consumer<Scene> sceneConfig;
    private static int seq;

    public CustomStage(StageStyle style, StageContent content, Consumer<Scene> sceneConfig) {
        super(style);
        this.sceneConfig = sceneConfig;

        setTitle("Stage [" + style + "]");
        setWidth(700);
        setHeight(500);

        setContent(content);
    }

    private void setContent(StageContent content) {
        Parent n = switch(content) {
        case EMPTY ->
            createEmpty();
        case IRREGULAR_SHAPE ->
            createIrregularShape();
        case NESTED_STAGES ->
            createNestedStages();
        case TEXT_AREA ->
            createTextArea();
        case UI_PANEL ->
            createUiPanel();
        };
        Scene sc = new Scene(n);
        sc.setFill(Color.TRANSPARENT);
        n.setOnContextMenuRequested(this::createPopupMenu);
        sceneConfig.accept(sc);
        setScene(sc);
    }

    private void createPopupMenu(ContextMenuEvent ev) {
        ContextMenu m = new ContextMenu();
        for(StageContent c: StageContent.values()) {
            FX.item(m, c.toString(), () -> setContent(c));
        }
        FX.separator(m);
        FX.item(m, "Size to Scene", this::sizeToScene);
        FX.item(m, "To Back", this::toBack);
        FX.item(m, "To Front", this::toFront);
        FX.separator(m);
        FX.checkItem(m, "Full Screen", isFullScreen(), this::setFullScreen);
        FX.checkItem(m, "Iconified", isIconified(), this::setIconified);
        FX.checkItem(m, "Maximize", isMaximized(), this::setMaximized);
        FX.separator(m);
        FX.item(m, "Close", this::hide);
        m.show(this, ev.getScreenX(), ev.getScreenY());
    }

    private Parent createEmpty() {
        return new Group();
    }

    private Parent createIrregularShape() {
        Circle c = new Circle(100, Color.RED);
        StackPane g = new StackPane(c);
        g.setBorder(new Border(new BorderStroke(Color.rgb(0, 0, 0, 0.3), BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        g.setBackground(Background.fill(Color.TRANSPARENT));
        return g;
    }

    private Parent createUiPanel() {
        return CustomPane.create();
    }

    private Parent createTextArea() {
        return new TextArea();
    }

    record Position(double x, double y) { }

    public Position getPosition(TargetLocation t) {
        return switch(t) {
        case OTHER_SCREEN -> {
            List<Screen> ss = Screen.getScreensForRectangle(getX(), getY(), 1, 1);
            Screen current = (ss.isEmpty() ? null : ss.getFirst());
            for (Screen s: Screen.getScreens()) {
                if(s != current) {
                    Rectangle2D r = s.getVisualBounds();
                    yield new Position(r.getMinX() + r.getWidth() * 0.25, r.getMinY() + r.getHeight() * 0.25);
                }
            }
            yield null;
        }
        case OUTSIDE -> {
            double x = Double.MAX_VALUE;
            double y = Double.MAX_VALUE;
            for (Screen s: Screen.getScreens()) {
                var b = s.getVisualBounds();
                x = Math.min(x, b.getMinX());
                y = Math.min(y, b.getMinY());
            }
            yield new Position(x - 1000, y - 1000);
        }
        case SAME_SCREEN ->
            new Position(getX() + 20, getY() + 20);
        };
    }

    private Parent createNestedStages() {
        SimpleBooleanProperty alwaysOnTop = new SimpleBooleanProperty();
        SimpleObjectProperty<TargetLocation> location = new SimpleObjectProperty<>(TargetLocation.SAME_SCREEN);
        SimpleObjectProperty<Modality> modality = new SimpleObjectProperty<>(Modality.NONE);
        SimpleObjectProperty<NodeOrientation> nodeOrientation = new SimpleObjectProperty<>(NodeOrientation.INHERIT);
        SimpleBooleanProperty owner = new SimpleBooleanProperty();
        SimpleObjectProperty<StageStyle> stageStyle = new SimpleObjectProperty<>(StageStyle.DECORATED);
        //
        SimpleBooleanProperty fullScreen = new SimpleBooleanProperty();
        SimpleStringProperty fullScreenExitHint = new SimpleStringProperty();
        SimpleBooleanProperty iconified = new SimpleBooleanProperty(false);
        SimpleBooleanProperty maximized = new SimpleBooleanProperty(false);
        //
        ObjectProperty<ColorScheme> colorScheme = new SimpleObjectProperty<>();
        ObjectProperty<Boolean> persistentScrollBars = new SimpleObjectProperty<>();
        ObjectProperty<Boolean> reducedData = new SimpleObjectProperty<>();
        ObjectProperty<Boolean> reducedMotion = new SimpleObjectProperty<>();
        ObjectProperty<Boolean> reducedTransparency = new SimpleObjectProperty<>();
        
        Platform.Preferences pp = Platform.getPreferences();
        colorScheme.set(pp.getColorScheme());
        persistentScrollBars.set(pp.persistentScrollBarsProperty().get());
        reducedData.set(pp.reducedDataProperty().get());
        reducedMotion.set(pp.reducedMotionProperty().get());
        reducedTransparency.set(pp.reducedTransparencyProperty().get());
        
        OptionPane op = new OptionPane();
        // init
        op.section("Initialization");
        op.option(new BooleanOption("alwaysOnTop", "always on top", alwaysOnTop));
        // TODO HeaderBar
        op.option("Location:", new EnumOption("location", TargetLocation.class, location));
        op.option("Modality:", new EnumOption("modality", Modality.class, modality));
        op.option("Node Orientation:", new EnumOption("nodeOrientation", NodeOrientation.class, nodeOrientation));
        op.option(new BooleanOption("owner", "set owner", owner));
        op.option("Stage Style:", new EnumOption("stageStyle", StageStyle.class, stageStyle));
        // stage
        op.section("Stage");
        op.option(new BooleanOption("fullScreen", "full screen", fullScreen));
        op.option("Full Screen Hint:", Options.textOption("fullScreenHint", true, true, fullScreenExitHint));
        op.option(new BooleanOption("iconified", "iconified", iconified));
        op.option(new BooleanOption("maximized", "maximized", maximized));

        Consumer<Scene> sceneConfig = (sc) -> {
            Scene.Preferences p = sc.getPreferences();
            p.setColorScheme(colorScheme.get());
            p.setPersistentScrollBars(persistentScrollBars.get());
            p.setReducedData(reducedData.get());
            p.setReducedMotion(reducedMotion.get());
            p.setReducedTransparency(reducedTransparency.get());
        };

        Button onTopButton = new Button();
        onTopButton.setTooltip(new Tooltip("Toggles the alwaysOnTop property"));
        onTopButton.textProperty().bind(Bindings.createStringBinding(() -> {
            return "AlwaysOnTop" + (isAlwaysOnTop() ? " ✓" : "");
        }, alwaysOnTopProperty()));
        onTopButton.setOnAction((_) -> {
            setAlwaysOnTop(!onTopButton.getText().contains("✓"));
        });
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction((_) -> {
            hide();
        });

        Button createButton = new Button("Create Stage");
        createButton.setOnAction((_) -> {
            // create stage
            Modality mod = modality.get();
            Stage own = owner.get() ? this : null;
            StringBuilder sb = new StringBuilder();
            if ((mod != null) && (mod != Modality.NONE)) {
                sb.append(mod).append(" ");
            }
            sb.append("S_");
            sb.append(seq++);
            if (own != null) {
                sb.append(" owner=");
                sb.append(own.getTitle());
            }
            Position pos = getPosition(location.get());

            Stage s = new CustomStage(stageStyle.get(), StageContent.NESTED_STAGES, sceneConfig);
            s.setTitle(sb.toString());
            // init
            s.setAlwaysOnTop(alwaysOnTop.get());
            s.initModality(mod);
            s.initOwner(own);
            s.setFullScreen(fullScreen.get());
            s.setFullScreenExitHint(fullScreenExitHint.get());
            s.setIconified(iconified.get());
            s.setMaximized(maximized.get());

            if (pos != null) {
                s.setX(pos.x());
                s.setY(pos.y());
            }

            s.show();
        });

        BorderPane bp = new BorderPane(op);
        bp.setPadding(new Insets(10));
        bp.setBottom(FX.buttonBar(onTopButton, closeButton, null, createButton));
        return bp;
    }
}
