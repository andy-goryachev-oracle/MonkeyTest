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
package com.oracle.tools.fx.monkey.util;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.CaretInfo;
import javafx.scene.text.LayoutInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.TextLineInfo;
import javafx.util.Duration;

/**
 * Visualizes text geometry available via LayoutInfo API.
 */
public class LayoutInfoVisualizer {
    // TODO range is separate from text lines
    public final SimpleBooleanProperty showCaret = new SimpleBooleanProperty();
    public final SimpleBooleanProperty showLines = new SimpleBooleanProperty();
    public final SimpleBooleanProperty showLayoutBounds = new SimpleBooleanProperty();
    public final SimpleBooleanProperty includeLineSpace = new SimpleBooleanProperty();

    private Pane parent;
    private final BooleanBinding isActive;
    private final SimpleObjectProperty<Node> owner = new SimpleObjectProperty<>();
    private Timeline animation;
    private Path boundsPath;
    private Path caretPath;
    private Group lines;

    public LayoutInfoVisualizer() {
        isActive = Bindings.createBooleanBinding(() -> {
                return
                    (owner.get() != null) &&
                    (
                        showCaret.get() ||
                        showLines.get() ||
                        showLayoutBounds.get()
                    );
            },
            owner,
            showCaret,
            showLines,
            showLayoutBounds
        );
        isActive.addListener((p) -> update());
    }

    public void attach(Text t) {
    }

    public void attach(Pane parent, TextFlow t) {
        if (parent == null) {
            parent = (Pane)t.getParent();
        }
        this.parent = parent;
        owner.set(t);
    }

    void update() {
        if (isActive.get()) {
            if (animation == null) {
                animation = new Timeline(
                    new KeyFrame(Duration.millis(100), (ev) -> refresh())
                );
                animation.setCycleCount(Timeline.INDEFINITE);
                animation.setDelay(Duration.millis(20));
                animation.play();
            }
        } else {
            if (animation != null) {
                animation.stop();
                animation = null;
                refresh();
            }
        }
    }

    void refresh() {
        updateCaret();
        updateLayoutBounds();
        updateTextLines();
    }

    private void updateCaret() {
        if (showCaret.get()) {
            if (caretPath == null) {
                // TODO maybe show the caret under the mouse instead
                caretPath = new Path();
                caretPath.setStrokeWidth(1);
                caretPath.setStroke(Color.RED);
                caretPath.setManaged(false);
                parent.getChildren().add(caretPath);
            }
            caretPath.getElements().setAll(createCaretShapes());
        } else {
            if (caretPath != null) {
                parent.getChildren().remove(caretPath);
                caretPath = null;
            }
        }
    }

    private void updateLayoutBounds() {
        if (showLayoutBounds.get()) {
            if (boundsPath == null) {
                boundsPath = new Path();
                boundsPath.setStrokeWidth(0);
                boundsPath.setFill(Color.rgb(255, 0, 0, 0.2));
                boundsPath.setManaged(false);
                parent.getChildren().add(boundsPath);
            }
            boundsPath.getElements().setAll(createBoundsShapes());
        } else {
            if (boundsPath != null) {
                parent.getChildren().remove(boundsPath);
                boundsPath = null;
            }
        }
    }

    private void updateTextLines() {
        if (showLines.get()) {
            if (lines == null) {
                lines = new Group();
                lines.setManaged(false);
                parent.getChildren().add(lines);
            }
            lines.getChildren().setAll(createTextLineShapes());
        } else {
            if (lines != null) {
                parent.getChildren().remove(lines);
                lines = null;
            }
        }
    }

    private LayoutInfo layoutInfo() {
        Node n = owner.get();
        if (n instanceof Text t) {
            return t.getLayoutInfo();
        } else if (n instanceof TextFlow t) {
            return t.getLayoutInfo();
        }
        return null;
    }

    private int getTextLength() {
        Node n = owner.get();
        if (n instanceof Text t) {
            return t.getText().length();
        } else if (n instanceof TextFlow t) {
            return FX.getTextLength(t);
        }
        return 0;
    }

    private void append(ArrayList<PathElement> a, CaretInfo ci) {
        for (int i = 0; i < ci.getPartCount(); i++) {
            Rectangle2D r = ci.getPartAt(i);
            a.add(new MoveTo(r.getMinX(), r.getMinY()));
            a.add(new LineTo(r.getMaxX(), r.getMaxY()));
        }
    }

    private void append(ArrayList<PathElement> a, Rectangle2D r) {
        a.add(new MoveTo(r.getMinX(), r.getMinY()));
        a.add(new LineTo(r.getMaxX(), r.getMinY()));
        a.add(new LineTo(r.getMaxX(), r.getMaxY()));
        a.add(new LineTo(r.getMinX(), r.getMaxY()));
        a.add(new LineTo(r.getMinX(), r.getMinY()));
    }

    private PathElement[] createCaretShapes() {
        LayoutInfo la = layoutInfo();
        ArrayList<PathElement> a = new ArrayList<>();
        int len = getTextLength();
        for (int i = 0; i < len; i++) {
            CaretInfo ci = la.caretInfo(i, true);
            append(a, ci);
        }
        CaretInfo ci = la.caretInfo(len, false);
        append(a, ci);
        return a.toArray(PathElement[]::new);
    }

    private PathElement[] createBoundsShapes() {
        LayoutInfo la = layoutInfo();
        ArrayList<PathElement> a = new ArrayList<>();
        Rectangle2D r = la.getBounds(includeLineSpace.get());
        append(a, r);
        return a.toArray(PathElement[]::new);
    }

    private static Color color(int index) {
        switch (index % 3) {
        case 0:
            return Color.rgb(255, 0, 0, 0.5);
        case 1:
            return Color.rgb(0, 255, 0, 0.5);
        default:
            return Color.rgb(0, 0, 255, 0.5);
        }
    }

    private List<Node> createTextLineShapes() {
        LayoutInfo la = layoutInfo();
        List<TextLineInfo> lines = la.getTextLines(includeLineSpace.get());
        ArrayList<Node> a = new ArrayList<>();
        int i = 0;
        for (TextLineInfo line : lines) {
            Rectangle2D b = line.bounds();
            Color c = color(i++);
            Rectangle r = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
            r.setFill(c);
            r.setStrokeWidth(0);
            a.add(r);
        }
        return a;
    }
}
