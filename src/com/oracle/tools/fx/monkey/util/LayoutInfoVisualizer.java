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
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.CaretInfo;
import javafx.scene.text.HitInfo;
import javafx.scene.text.LayoutInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.TextLineInfo;
import javafx.util.Duration;

/**
 * Visualizes text geometry available via LayoutInfo API.
 */
public class LayoutInfoVisualizer {
    public final SimpleBooleanProperty showCaretAndRange = new SimpleBooleanProperty();
    public final SimpleBooleanProperty showLines = new SimpleBooleanProperty();
    public final SimpleBooleanProperty showLayoutBounds = new SimpleBooleanProperty();
    public final SimpleBooleanProperty includeLineSpace = new SimpleBooleanProperty();

    private Pane parent;
    private final BooleanBinding isAnimated;
    private final SimpleObjectProperty<Node> owner = new SimpleObjectProperty<>();
    private Timeline animation;
    private Path boundsPath;
    private Path caretPath;
    private Path rangePath;
    private Group lines;
    private EventHandler<MouseEvent> mouseListener;
    private int startIndex;

    private static final double CARET_VIEW_ORDER = 1000;
    private static final double RANGE_VIEW_ORDER = 1010;
    private static final double TEXT_LINES_VIEW_ORDER = 1020;
    private static final double BOUNDS_VIEW_ORDER = 1030;

    public LayoutInfoVisualizer() {
        isAnimated = Bindings.createBooleanBinding(() -> {
                return
                    (owner.get() != null) &&
                    (
                        showLines.get() ||
                        showLayoutBounds.get()
                    );
            },
            owner,
            showLines,
            showLayoutBounds
        );
        isAnimated.addListener((p) -> update());
        showCaretAndRange.addListener((p) -> updateCaretAndRange());
    }

    public void attach(Pane parent, Text t) {
        this.parent = parent;
        owner.set(t);
    }

    public void attach(Pane parent, TextFlow t) {
        this.parent = parent;
        owner.set(t);
    }

    void update() {
        if (isAnimated.get()) {
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
        updateLayoutBounds();
        updateTextLines();
    }

    private void updateCaretAndRange() {
        if (showCaretAndRange.get()) {
            // caret
            if (caretPath == null) {
                caretPath = new Path();
                caretPath.setStrokeWidth(1);
                caretPath.setStroke(Color.RED);
                caretPath.setManaged(false);
                caretPath.setViewOrder(CARET_VIEW_ORDER);
                parent.getChildren().add(caretPath);
            }

            // range
            if (rangePath == null) {
                rangePath = new Path();
                rangePath.setStrokeWidth(0);
                rangePath.setFill(Color.rgb(0, 128, 255, 0.3));
                rangePath.setManaged(false);
                rangePath.setViewOrder(RANGE_VIEW_ORDER);
                parent.getChildren().add(rangePath);
            }

            // mouse
            if (mouseListener == null) {
                mouseListener = this::handleMouseEvent;
                owner.get().addEventHandler(MouseEvent.ANY, mouseListener);
            }
        } else {
            // mouse
            if (mouseListener != null) {
                owner.get().removeEventHandler(MouseEvent.ANY, mouseListener);
                mouseListener = null;
            }

            // caret
            if (caretPath != null) {
                parent.getChildren().remove(caretPath);
                caretPath = null;
            }

            // range
            if (rangePath != null) {
                parent.getChildren().remove(rangePath);
                rangePath = null;
            }
        }
    }

    private void updateLayoutBounds() {
        if (showLayoutBounds.get()) {
            if (boundsPath == null) {
                boundsPath = new Path();
                boundsPath.setViewOrder(BOUNDS_VIEW_ORDER);
                boundsPath.setStrokeWidth(0);
                boundsPath.setFill(Color.rgb(255, 128, 0, 0.1));
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
                lines.setAutoSizeChildren(false);
                lines.setViewOrder(TEXT_LINES_VIEW_ORDER);
                lines.setManaged(false);
                parent.getChildren().add(lines);
            }
            lines.getChildren().setAll(createTextLinesShapes());
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

    private HitInfo hitInfo(MouseEvent ev) {
        Node n = owner.get();
        Point2D p = n.screenToLocal(ev.getScreenX(), ev.getScreenY());
        if (n instanceof Text t) {
            return t.hitTest(p);
        } else if (n instanceof TextFlow t) {
            return t.hitTest(p);
        }
        return null;
    }

    private void append(ArrayList<PathElement> a, Rectangle2D r) {
        a.add(new MoveTo(r.getMinX(), r.getMinY()));
        a.add(new LineTo(r.getMaxX(), r.getMinY()));
        a.add(new LineTo(r.getMaxX(), r.getMaxY()));
        a.add(new LineTo(r.getMinX(), r.getMaxY()));
        a.add(new LineTo(r.getMinX(), r.getMinY()));
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
            return Color.rgb(255, 0, 0, 0.3);
        case 1:
            return Color.rgb(0, 255, 0, 0.3);
        default:
            return Color.rgb(0, 0, 255, 0.3);
        }
    }

    private List<Node> createTextLinesShapes() {
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
            r.setManaged(false);
            a.add(r);
        }
        return a;
    }

    private PathElement[] createRange(int start, int end) {
        Node n = owner.get();
        if (n instanceof Text t) {
            return fix_8341438(t.rangeShape(start, end), 0.0, 0.0);
        } else if (n instanceof TextFlow t) {
            Insets m = t.getInsets();
            double dx = m.getLeft(); // FIX rtl?
            double dy = m.getTop();
            return fix_8341438(t.rangeShape(start, end), dx, dy);
        }
        return new PathElement[0];
    }

    // FIX JDK-8341438
    private static PathElement[] fix_8341438(PathElement[] es, double dx, double dy) {
        PathElement[] rv = new PathElement[es.length];
        for(int i=0; i<es.length; i++) {
            PathElement em = es[i];
            PathElement shifted;
            if(em instanceof MoveTo v) {
                shifted = new MoveTo(v.getX() + dx, v.getY() + dy);
            } else if(em instanceof LineTo v) {
                shifted = new LineTo(v.getX() + dx, v.getY() + dy);
            } else {
                shifted = em;
            }
            rv[i] = shifted;
        }
        return rv;
    }

    private PathElement[] createCaretShape(CaretInfo ci) {
        ArrayList<PathElement> a = new ArrayList<>();
        for (int i = 0; i < ci.getPartCount(); i++) {
            Rectangle2D r = ci.getPartAt(i);
            a.add(new MoveTo(r.getMinX(), r.getMinY()));
            a.add(new LineTo(r.getMaxX(), r.getMaxY()));
        }
        return a.toArray(PathElement[]::new);
    }

    void handleMouseEvent(MouseEvent ev) {
        HitInfo h = hitInfo(ev);
        LayoutInfo la = layoutInfo();
        CaretInfo ci = la.caretInfo(h.getCharIndex(), h.isLeading());
        caretPath.getElements().setAll(createCaretShape(ci));

        var t = ev.getEventType();
        if (t == MouseEvent.MOUSE_PRESSED) {
            startIndex = h.getInsertionIndex();
        } else if (t == MouseEvent.MOUSE_DRAGGED) {
            int end = h.getInsertionIndex();
            PathElement[] es = createRange(startIndex, end);
            rangePath.getElements().setAll(es);
        } else if (t == MouseEvent.MOUSE_RELEASED) {
            rangePath.getElements().clear();
        }
    }
}
