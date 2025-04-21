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
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
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
 */
public class TabStopPane extends Pane {
    private final TabStopPolicy policy;
    private final IntegerBinding binding;
    private int seq;
    private ArrayList<Path> ticks = new ArrayList<>();

    public TabStopPane(TabStopPolicy p) {
        this.policy = p;
        setPrefHeight(15);
        setBackground(Background.fill(Color.WHITE));

        // move this to the policy maybe?
        binding = Bindings.createIntegerBinding(() -> {
            requestLayout();
            return seq++;
        }, p.tabStops(), p.defaultStopsProperty());
    }

    @Override
    protected void layoutChildren() {
        int i;
        double pos = 0.0;
        // tabs
        ArrayList<Path> ps = new ArrayList<>();
        for (i = 0; i < policy.tabStops().size(); i++) {
            TabStop t = policy.tabStops().get(i);
            pos = t.getPosition();
            Path p = updateTab(i, pos);
            ps.add(p);
        }
        // default stops
        double defaultStops = policy.getDefaultStops();
        if (defaultStops > 0.0) {
            for (;; i++) {
                pos = (1 + (int)(pos / defaultStops)) * defaultStops;
                if (pos >= getWidth()) {
                    break;
                }
                Path p = updateTick(i, pos);
                ps.add(p);
            }
            ticks = ps;
            getChildren().setAll(ps);
        }
    }

    private Path updateTab(int ix, double position) {
        Path p = ix < ticks.size() ? ticks.get(ix) : new Path();
        p.setManaged(false);
        p.setStroke(Color.BLACK);
        p.setStrokeWidth(0.5);
        p.setStrokeLineJoin(StrokeLineJoin.BEVEL);
        double x = position;
        double w2 = 4;
        double h2 = getHeight() / 2.0;
        ArrayList<PathElement> es = new ArrayList<>(5);
        es.add(new MoveTo(x, 0));
        es.add(new LineTo(x + w2, h2));
        es.add(new LineTo(x, getHeight()));
        es.add(new LineTo(x - w2, h2));
        es.add(new ClosePath());
        p.getElements().setAll(es);
        return p;
    }

    private Path updateTick(int ix, double position) {
        Path p = ix < ticks.size() ? ticks.get(ix) : new Path();
        p.setManaged(false);
        p.setStroke(Color.BLACK);
        p.setStrokeWidth(1.0);
        double x = position;
        ArrayList<PathElement> es = new ArrayList<>(2);
        es.add(new MoveTo(x, 0));
        es.add(new LineTo(x, getHeight()));
        p.getElements().setAll(es);
        return p;
    }
}
