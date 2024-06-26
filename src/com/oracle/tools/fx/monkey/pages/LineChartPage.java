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
package com.oracle.tools.fx.monkey.pages;

import javafx.scene.AccessibleAttribute;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import com.oracle.tools.fx.monkey.Loggers;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.sheets.XYChartPropertySheet;
import com.oracle.tools.fx.monkey.util.OptionPane;

/**
 * Line Chart Page.
 */
public class LineChartPage extends XYChartPageBase {
    private final LineChart<Number, Number> chart;

    public LineChartPage() {
        super("LineChartPage");

        chart = new LineChart<>(createNumberAxis("X Axis"), createNumberAxis("Y Axis")) {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute a, Object... ps) {
                Object v = super.queryAccessibleAttribute(a, ps);
                Loggers.accessibility.log(a, v);
                return v;
            }
        };
        chart.setTitle("Line Chart");
        addSeries();

        OptionPane op = new OptionPane();
        op.section("LineChart");
        op.option(new BooleanOption("createSymbols", "create symbols", chart.createSymbolsProperty()));
        XYChartPropertySheet.appendTo(this, op, chart);

        setContent(chart);
        setOptions(op);
    }

    @Override
    public XYChart<?, Number> chart() {
        return chart;
    }
}
