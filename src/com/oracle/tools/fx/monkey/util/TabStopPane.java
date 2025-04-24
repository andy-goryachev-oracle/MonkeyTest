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
import java.util.Comparator;
import java.util.List;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.TabStop;
import javafx.scene.text.TabStopPolicy;

/**
 * Visual editor for a TabStopPolicy.
 *
 * TODO decouple tab stops here from the policy
 * TODO drag without updating tab policy!  update policy only on mouse release
 */
public class TabStopPane extends Pane {

    private final TabStopPolicy policy;
    private int seq;
    private List<Tick> ticks;
    private TabStop clickedStop;
    private boolean dragged;
    private static final double HALFWIDTH = 4;

    public TabStopPane(TabStopPolicy p) {
        this.policy = p;
        setPrefHeight(15);
        setBackground(Background.fill(Color.WHITE));

        p.tabStops().subscribe(this::update);
        p.defaultStopsProperty().subscribe(this::update);
        widthProperty().subscribe(this::update);

        addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
    }

    private void update() {
        ticks = null;
        requestLayout();
    }

    // ticks: Paths (with TabStop in properties)
    // init: create from policy
    // layout: create/update paths, setAll
    // edit: add/remove ticks, setAll
    // release: update policy
    private List<Tick> createTicks() {
        double width = getWidth();
        double height = getHeight();
        double x = 0.0;
        ArrayList<Tick> ts = new ArrayList<>(16);

        // tab stops
        for (TabStop t : policy.tabStops()) {
            x = (float)t.getPosition();
            if ((x - HALFWIDTH) > width) {
                break;
            }
            ts.add(Tick.createTabStop(t, height));
        }

        // default stops
        double defaultStops = policy.getDefaultStops();
        if (defaultStops > 0.0) {
            for (;;) {
                x = nextPosition(x, defaultStops);
                if ((x - HALFWIDTH) > width) {
                    break;
                }
                ts.add(Tick.createTick(x, height));
            }
        }
        return ts;
    }

    // similar to FixedTabAdvancePolicy.nextPosition()
    private static double nextPosition(double position, double tabAdvance) {
        double n = (position / tabAdvance);
        return ((int)(n + Math.ulp(n)) + 1) * tabAdvance;
    }

    @Override
    protected void layoutChildren() {
        if (ticks == null) {
            ticks = createTicks();
            getChildren().setAll(ticks);
        }
    }

    private TabStop findTabStop(double x) {
        for (int i = 0; i < policy.tabStops().size(); i++) {
            TabStop t = policy.tabStops().get(i);
            if (Math.abs(t.getPosition() - x) < HALFWIDTH) {
                return t;
            }
        }
        return null;
    }

    // TODO sort + normalize (remove closely positioned tabs)
    // but: removing close tabs while dragging should be verboten!
    private static void sort(ArrayList<TabStop> updated) {
        updated.sort(new Comparator<TabStop>() {
            @Override
            public int compare(TabStop a, TabStop b) {
                return (int)Math.signum(a.getPosition() - b.getPosition());
            }
        });
    }

    private void handleMousePressed(MouseEvent ev) {
        double x = ev.getX();
        dragged = false;
        clickedStop = findTabStop(x);
    }

    private void handleMouseReleased(MouseEvent ev) {
        // was dragged? update tab stops
        // was tabstop? remove
        if (clickedStop == null) {
            double x = ev.getX();
            List<TabStop> original = policy.tabStops();
            ArrayList<TabStop> updated = new ArrayList<>(original);
            updated.add(new TabStop(x));
            sort(updated);
            policy.tabStops().setAll(updated);
        } else {
            if (!dragged) {
                policy.tabStops().remove(clickedStop);
            }
        }
        clickedStop = null;
        dragged = false;
        // TODO update
    }

    private void handleMouseDragged(MouseEvent ev) {
        // update the tabstop being dragged
        if (clickedStop != null) {
            double x = ev.getX();
            List<TabStop> original = policy.tabStops();
            int sz = original.size();
            ArrayList<TabStop> updated = new ArrayList<>(sz);
            for (int i = 0; i < sz; i++) {
                TabStop t = policy.tabStops().get(i);
                if (t == clickedStop) {
                    clickedStop = new TabStop(x);
                    updated.add(clickedStop);
                } else {
                    updated.add(t);
                }
            }
            sort(updated);
            policy.tabStops().setAll(updated);
            requestLayout();
            dragged = true;
        }
    }

    private static class Tick extends Path {
        public double position;

        public Tick(double position) {
            this.position = position;
            setManaged(false);
        }

        private static Tick createTabStop(TabStop tab, double height) {
            double x = tab.getPosition();
            Tick t = new Tick(x);
            t.setManaged(false);
            t.setStroke(Color.BLACK);
            t.setStrokeWidth(0.5);
            t.setStrokeLineJoin(StrokeLineJoin.BEVEL);
            double h2 = height / 2.0;
            ArrayList<PathElement> es = new ArrayList<>(5);
            es.add(new MoveTo(x, 0));
            es.add(new LineTo(x + HALFWIDTH, h2));
            es.add(new LineTo(x, height));
            es.add(new LineTo(x - HALFWIDTH, h2));
            es.add(new ClosePath());
            t.getElements().setAll(es);
            return t;
        }

        private static Tick createTick(double x, double height) {
            Tick t = new Tick(x);
            t.setManaged(false);
            t.setStroke(Color.BLACK);
            t.setStrokeWidth(1.0);
            ArrayList<PathElement> es = new ArrayList<>(2);
            es.add(new MoveTo(x, 0));
            es.add(new LineTo(x, height));
            t.getElements().setAll(es);
            return t;
        }
    }
}
