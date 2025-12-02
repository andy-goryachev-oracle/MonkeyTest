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
package com.oracle.tools.fx.monkey.options;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.converter.DoubleStringConverter;
import com.oracle.tools.fx.monkey.util.NamedValue;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 * Font Picker Pane.
 */
public class FontPickerPane extends GridPane {
    private final boolean allowNull;
    private final Consumer<Font> client;
    // TODO editable combo box w/list of previously selected fonts (font + style + size)
    private final TextField editor;
    private final ListView<String> familyField = new ListView<>();
    private final ListView<Object> styleField = new ListView<>();
    private final ComboBox<Double> sizeField = new ComboBox<>();
    private final Label sample;
    private final List<String> fonts;

    public FontPickerPane(Font f, boolean allowNull, Consumer<Font> client) {
        this.allowNull = allowNull;
        this.client = client;

        fonts = collectFonts(allowNull);

        editor = new TextField();
        editor.addEventFilter(KeyEvent.ANY, (ev) -> {
            handleKeyPress();
        });

        familyField.getItems().setAll(fonts);
        familyField.getSelectionModel().selectedItemProperty().addListener((_, _, v) -> {
            setFamily(v);
        });

        styleField.getSelectionModel().selectedItemProperty().addListener((_, _, v) -> {
            setStyle(v);
        });

        sizeField.setEditable(true);
        sizeField.setConverter(new DoubleStringConverter());
        sizeField.getItems().setAll(
            1.0,
            2.5,
            6.0,
            8.0,
            10.0,
            11.0,
            12.0,
            13.0,
            14.0,
            16.0,
            18.0,
            24.0,
            32.0,
            48.0,
            72.0,
            144.0,
            480.0
        );

        sample = new Label("Brown fox jumped over a lazy dog.\n01234567890");
        sample.setMinHeight(70);
        sample.setMaxWidth(Double.MAX_VALUE);
        sample.setBackground(Background.fill(Color.WHITE));
        sample.setAlignment(Pos.TOP_LEFT);
        sample.setPadding(new Insets(5));
        // maybe in a scroll pane?
        
        Button ok = new Button("OK");
        ButtonBar.setButtonData(ok, ButtonData.OK_DONE);
        ok.setOnAction((_) -> {
            pickFont();
        });
        Button cancel = new Button("Cancel");
        ButtonBar.setButtonData(cancel, ButtonData.CANCEL_CLOSE);
        ButtonBar bb = new ButtonBar();
        bb.getButtons().setAll(ok, cancel);

        // layout
        setPrefHeight(350);
        setPrefWidth(500);
        setHgap(5);
        setVgap(5);
        setBackground(Background.fill(Color.LIGHTGRAY));
        setPadding(new Insets(5));
        setFocusTraversable(true);

        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(80.0);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(20.0);
        getColumnConstraints().addAll(c0, c1);
        
        RowConstraints r0 = new RowConstraints();
        RowConstraints r1 = new RowConstraints();
        r1.setFillHeight(true);
        RowConstraints r2 = new RowConstraints();
        getRowConstraints().addAll(r0, r1, r2);

        add(editor, 0, 0);
        add(sizeField, 1, 0);
        add(familyField, 0, 1);
        add(styleField, 1, 1);
        add(sample, 0, 2, 2, 1);
        add(bb, 0, 3, 2, 1);

        setFont(f);
    }

    // TODO move to caller?
    public Popup createPopup() {
        Popup p = new Popup();
        p.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
        //p.setAutoHide(true);
        p.getContent().add(this);

        p.setOnShown((ev) -> {
            editor.requestFocus();
        });

        return p;
    }

    private void handleKeyPress() {
        // TODO delayed action: filter, set all
        String pattern = editor.getText().toLowerCase(Locale.ROOT);
        ArrayList<String> fs = new ArrayList<>(fonts.size());
        for (String s : fonts) {
            if (s.toLowerCase(Locale.ROOT).contains(pattern)) {
                fs.add(s);
            }
        }
        familyField.getItems().setAll(fs);
        // TODO if one, select?
    }

    private void setFamily(String name) {
        System.out.println("setFamily " + name); // FIX
        
        String st = getCurrentStyle();
        double sz = getCurrentSize();
        
        List<Object> ss = collectStyles(name);
        styleField.getItems().setAll(ss);

        int ix = indexOf(ss, st);
        if (ix >= 0) {
            styleField.getSelectionModel().select(ix);
        }

        ix = indexOf(sizeField.getItems(), sizeField.getValue());
        if (ix < 0) {
            ix = indexOf(sizeField.getItems(), defaultFontSize());
        }
        styleField.getSelectionModel().select(ix);
    }

    private void setStyle(Object x) {
        System.out.println("setStyle " + x); // FIX
        // TODO
        // if one, select?
    }
    
    private void setFont(Font f) {
        System.out.println(f); // FIX
        if(f == null) {
            if(allowNull) {
                familyField.getSelectionModel().select(null);
                editor.setText(null);
            }
        } else {
            String fam = f.getFamily();
            String sty = f.getStyle();
            double sz = f.getSize();
            familyField.getSelectionModel().select(fam);
            select(styleField, sty);
            select(sizeField, sz);
            editor.setText(getFontString(f));
        }
    }
    
    public String getCurrentFamily() {
        return familyField.getSelectionModel().getSelectedItem();
    }

    public String getCurrentStyle() {
        Object v = styleField.getSelectionModel().getSelectedItem();
        return getDisplayValue(v);
    }

    public double getCurrentSize() {
        Double v = sizeField.getValue();
        return v == null ? defaultFontSize() : v;
    }

    private static int indexOf(List<?> items, String value) {
        int sz = items.size();
        for (int i = 0; i < sz; i++) {
            Object x = items.get(i);
            String s = getDisplayValue(x);
            if (Utils.eq(s, value)) {
                return i;
            }
        }
        return -1;
    }

    private static int indexOf(List<Double> items, Double value) {
        if (value != null) {
            int sz = items.size();
            for (int i = 0; i < sz; i++) {
                double x = items.get(i);
                if (Math.abs(x - value) < 0.005) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static void select(ListView<Object> list, String val) {
        int ix = indexOf(list.getItems(), val);
        if (ix >= 0) {
            list.getSelectionModel().select(ix);
        }
    }

    private static void select(ComboBox<Double> cb, double value) {
        int ix = indexOf(cb.getItems(), value);
        if (ix >= 0) {
            cb.getSelectionModel().select(ix);
        }
    }

    private static String getDisplayValue(Object x) {
        if(x == null) {
            return null;
        } else if(x instanceof NamedValue v) {
            return v.getDisplay();
        } else {
            return x.toString();
        }
    }

    private static List<String> collectFonts(boolean allowNull) {
        ArrayList<String> rv = new ArrayList<>();
        if (allowNull) {
            rv.add(0, null);
        }
        rv.add("Cursive");
        rv.add("Fantasy");
        rv.add("Monospace");
        rv.add("Sans-serif");
        rv.add("Serif");
        rv.add("System");
        rv.addAll(Font.getFamilies());
        sort(rv);
        return rv;
    }

    private static List<Object> collectStyles(String family) {
        if (Utils.isBlank(family)) {
            return List.of();
        }

        List<String> ss = Font.getFontNames(family);
        int sz = ss.size();
        ArrayList<Object> rv = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++) {
            String s = ss.get(i);
            Object v = parseStyle(family, s);
            rv.add(v);
        }
        sort(rv);
        return rv;
    }
    
    private static void sort(List<?> items) {
        Collator coll = Collator.getInstance(Locale.ROOT);
        Collections.sort(items, new Comparator<Object>() {
            @Override
            public int compare(Object a, Object b) {
                String sa = toString(a);
                String sb = toString(b);
                return coll.compare(sa, sb);
            }

            private static String toString(Object x) {
                if (x == null) {
                    return "";
                } else if (x instanceof NamedValue v) {
                    return v.getDisplay();
                } else {
                    return x.toString();
                }
            }
        });
    }

    private static Object parseStyle(String family, String s) {
        if (s.startsWith(family)) {
            s = s.substring(family.length()).trim();
        }
        if (Utils.isBlank(s)) {
            return new NamedValue("Regular", "");
        }
        return s;
    }

    private static double defaultFontSize() {
        return Font.getDefault().getSize();
    }

    public static String getFontString(Font f) {
        if (f == null) {
            return null;
        }

        String fam = f.getFamily();
        String sty = f.getStyle();
        double sz = f.getSize();
        return fam + " " + sty + " " + sz;
    }

    private void pickFont() {
        Font f = getCurrentFont();
        System.out.println("pickFont: " + f); // FIX
        client.accept(f);
    }

    public Font getCurrentFont() {
        String fm = getCurrentFamily();
        if (Utils.isBlank(fm)) {
            return null;
        } else {
            // FIX or maybe construct the actual font name, and get it?  do not parse weight!
            Object v = styleField.getSelectionModel().getSelectedItem();
            String st;
            if(v == null) {
                st = "";
            } else if(v instanceof NamedValue nv) {
                st = nv.getValue().toString();
            } else {
                st = v.toString();
            }
            String name;
            if (Utils.isBlank(st)) {
                name = fm;
            } else {
                name = fm + " " + st;
            }
            double sz = getCurrentSize();
            return Font.font(name, sz);
        }
    }

    // TODO remove?
    private static FontWeight parseWeight(String s) {
        FontWeight w = FontWeight.findByName(s);
        if (w == null) {
            System.out.println("Unable to parse: " + s); // FIX
            return FontWeight.NORMAL;
        }
        return w;
    }
}
