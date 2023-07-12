/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.text.Text;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 *
 */
public class ClipboardPage extends TestPaneBase {
    private final TreeItem<Entry> root;
    private final TreeTableView<Entry> control;

    public ClipboardPage() {
        FX.name(this, "ClipboardPage");

        root = new TreeItem<>(null);
        control = new TreeTableView<>(root);
        control.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        control.setShowRoot(false);
        {
            TreeTableColumn<Entry, String> c = new TreeTableColumn<>();
            c.setText("Data Format");
            c.setMinWidth(100);
            c.setMaxWidth(200);
            c.setCellValueFactory((f) -> {
                var t = f.getValue();
                if (t != null) {
                    var tt = t.getValue();
                    if (tt != null) {
                        return tt.text;
                    }
                }
                return null;
            });
            control.getColumns().add(c);
        }
        {
            TreeTableColumn<Entry, String> c = new TreeTableColumn<>();
            c.setText("Value");
            c.setPrefWidth(1000);
            c.setCellFactory((r) -> {
                return new TreeTableCell<Entry,String>() {
                    @Override
                    protected void updateItem(String text, boolean empty) {
                        super.updateItem(text, empty);
                        Text t = new Text(text);
                        t.wrappingWidthProperty().bind(widthProperty());
                        setPrefHeight(USE_COMPUTED_SIZE);
                        setGraphic(t);
                    }
                };
            });
            // TODO text flow
            c.setCellValueFactory((f) -> {
                var t = f.getValue();
                if (t != null) {
                    var tt = t.getValue();
                    if (tt != null) {
                        return tt.text2;
                    }
                }
                return null;
            });
            control.getColumns().add(c);
        }

        Button addButton = new Button("Read");
        addButton.setOnAction((ev) -> read());

        OptionPane op = new OptionPane();
        op.add(addButton);

        setContent(control);
        setOptions(op);
        
        read();
    }

    private void read() {
        Clipboard c = Clipboard.getSystemClipboard();
        List<DataFormat> formats = new ArrayList<>(c.getContentTypes());
        Collections.sort(formats, new Comparator<DataFormat>() {
            @Override
            public int compare(DataFormat a, DataFormat b) {
                return a.toString().compareTo(b.toString());
            }
        });

        ArrayList<TreeItem<Entry>> items = new ArrayList<>();
        for (DataFormat f: formats) {
            TreeItem<Entry> fn = new TreeItem<>(new Entry(f.toString(), null));
            items.add(fn);
            
            Object x = c.getContent(f);
            String val = convert(x);
            fn.getChildren().add(new TreeItem<>(new Entry(null, val)));
        }
        
        root.getChildren().setAll(items);
    }
    
    private static String convert(Object x) {
        // String, ByteBuffer
        return x.toString();
    }

    private static class Entry {
        public final SimpleStringProperty text;
        public final SimpleStringProperty text2;

        public Entry(String s1, String s2) {
            text = new SimpleStringProperty(s1);
            text2 = new SimpleStringProperty(s2);
        }
    }
}
