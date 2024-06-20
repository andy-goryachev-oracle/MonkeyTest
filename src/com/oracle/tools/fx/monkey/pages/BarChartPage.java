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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import com.oracle.tools.fx.monkey.Loggers;
import com.oracle.tools.fx.monkey.sheets.Options;
import com.oracle.tools.fx.monkey.sheets.XYChartPropertySheet;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;

/**
 * Bar Chart Page.
 */
public class BarChartPage extends XYChartPageBase {
    private final BarChart<String, Number> chart;

    public BarChartPage() {
        super("BarChartPage");

        chart = new BarChart<>(createCategoryAxis("X Axis"), createNumberAxis("Y Axis")) {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute a, Object... ps) {
                Object v = super.queryAccessibleAttribute(a, ps);
                Loggers.accessibility.log(a, v);
                return v;
            }
        };
        chart.setTitle("Bar Chart");
        FX.setPopupMenu(chart, this::createMenu);
        addSeries();

        OptionPane op = new OptionPane();
        op.section("BarChart");
        op.option("Bar Gap:", Options.gaps("barGap", chart.barGapProperty()));
        op.option("Category Gap:", Options.gaps("categoryGap", chart.categoryGapProperty()));
        XYChartPropertySheet.appendTo(this, op, chart);

        setContent(chart);
        setOptions(op);
    }

    ContextMenu createMenu() {
        ContextMenu m = new ContextMenu();
        FX.item(m, "Add Duplicate Category", this::addDuplicateCategory);
        return m;
    }

    void addDuplicateCategory() {
        var d = chart.getData();
        if (d.size() > 0) {
            var dd = d.get(0).getData();
            if (dd.size() > 0) {
                var v = dd.get(0);
                dd.add(new XYChart.Data(v.getXValue(), v.getYValue().doubleValue() + 1.0));
            }
        }
    }

    @Override
    public XYChart<?, Number> chart() {
        return chart;
    }
}
