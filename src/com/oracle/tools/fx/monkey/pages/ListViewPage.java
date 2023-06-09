/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.HasSkinnable;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.SequenceNumber;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * ListView page
 */
public class ListViewPage extends TestPaneBase implements HasSkinnable {
    enum Demo {
        EMPTY("Empty"),
        LARGE("Large"),
        SMALL("Small"),
        VARIABLE("Variable Height"),
        LARGE_IMG("Large Images"),
        ;

        private final String text;
        Demo(String text) { this.text = text; }
        public String toString() { return text; }
    }

    public enum Selection {
        SINGLE("single selection"),
        MULTIPLE("multiple selection"),
        NULL("null selection model");

        private final String text;
        Selection(String text) { this.text = text; }
        public String toString() { return text; }
    }

    public enum Cmd {
        ROWS,
        VARIABLE_ROWS,
    }

    private enum Cells {
        DEFAULT,
        LARGE_ICON,
        VARIABLE,
    }

    private final ComboBox<Demo> demoSelector;
    private final ComboBox<Cells> cellFactorySelector;
    private final ComboBox<Selection> selectionSelector;
    private final CheckBox nullFocusModel;
    private ListView<Object> control;

    public ListViewPage() {
        FX.name(this, "ListViewPage");

        demoSelector = new ComboBox<>();
        FX.name(demoSelector, "demoSelector");
        demoSelector.getItems().addAll(Demo.values());
        demoSelector.setEditable(false);
        demoSelector.getSelectionModel().selectedItemProperty().addListener((s, p, c) -> {
            updatePane();
        });

        cellFactorySelector = new ComboBox<>();
        FX.name(cellFactorySelector, "cellSelector");
        cellFactorySelector.getItems().addAll(Cells.values());
        cellFactorySelector.setEditable(false);
        cellFactorySelector.getSelectionModel().selectedItemProperty().addListener((s, p, c) -> {
            updateCellFactory();
        });

        selectionSelector = new ComboBox<>();
        FX.name(selectionSelector, "selectionSelector");
        selectionSelector.getItems().addAll(Selection.values());
        selectionSelector.setEditable(false);
        selectionSelector.getSelectionModel().selectedItemProperty().addListener((s, p, c) -> {
            updatePane();
        });

        nullFocusModel = new CheckBox("null focus model");
        FX.name(nullFocusModel, "nullFocusModel");
        nullFocusModel.selectedProperty().addListener((s, p, c) -> {
            updatePane();
        });

        Button addButton = new Button("Add Item");
        addButton.setOnAction((ev) -> {
            control.getItems().add(newItem(""));
        });

        Button clearButton = new Button("Clear Items");
        clearButton.setOnAction((ev) -> {
            control.getItems().clear();
        });

        Button jumpButton = new Button("Jump w/VirtualFlow");
        jumpButton.setOnAction((ev) -> {
            jump();
        });

        Button refresh = new Button("Refresh");
        refresh.setOnAction((ev) -> {
            control.refresh();
        });

        // layout

        OptionPane op = new OptionPane();
        op.label("Data:");
        op.option(demoSelector);
        op.option(addButton);
        op.option(clearButton);
        op.label("Cell Factory:");
        op.option(cellFactorySelector);
        op.label("Selection Model:");
        op.option(selectionSelector);
        op.option(nullFocusModel);
        op.option(jumpButton);
        op.option(refresh);
        setOptions(op);

        demoSelector.getSelectionModel().selectFirst();
        selectionSelector.getSelectionModel().select(Selection.MULTIPLE);
    }

    protected Object[] createSpec(Demo d) {
        switch (d) {
        case EMPTY:
            return new Object[] {
            };
        case LARGE:
            return new Object[] {
                Cmd.ROWS, 10_000,
            };
        case SMALL:
            return new Object[] {
                Cmd.ROWS, 3,
            };
        case VARIABLE:
            return new Object[] {
                Cmd.VARIABLE_ROWS, 500,
            };
        default:
            throw new Error("?" + d);
        }
    }

    protected void updatePane() {
        Demo d = demoSelector.getSelectionModel().getSelectedItem();
        Object[] spec = createSpec(d);

        Pane n = createPane(d, spec);
        setContent(n);
    }

    // TODO consider updating the existing control instead of re-creating it
    protected Pane createPane(Demo demo, Object[] spec) {
        if ((demo == null) || (spec == null)) {
            return new BorderPane();
        }

        boolean nullSelectionModel = false;
        SelectionMode selectionMode = SelectionMode.SINGLE;
        Selection sel = selectionSelector.getSelectionModel().getSelectedItem();
        if (sel != null) {
            switch (sel) {
            case MULTIPLE:
                selectionMode = SelectionMode.MULTIPLE;
                break;
            case NULL:
                nullSelectionModel = true;
                break;
            case SINGLE:
                break;
            default:
                throw new Error("?" + sel);
            }
        }

        control = new ListView<>();
        control.getSelectionModel().setSelectionMode(selectionMode);
        if (nullSelectionModel) {
            control.setSelectionModel(null);
        }
        if (nullFocusModel.isSelected()) {
            control.setFocusModel(null);
        }

        for (int i = 0; i < spec.length;) {
            Object x = spec[i++];
            if (x instanceof Cmd cmd) {
                switch (cmd) {
                case ROWS: {
                    int n = (int)(spec[i++]);
                    for (int j = 0; j < n; j++) {
                        control.getItems().add(newItem(i));
                    }
                }
                    break;
                case VARIABLE_ROWS: {
                    int n = (int)(spec[i++]);
                    for (int j = 0; j < n; j++) {
                        control.getItems().add(newVariableItem(j));
                    }
                }
                    break;
                default:
                    throw new Error("?" + cmd);
                }
            } else {
                throw new Error("?" + x);
            }
        }

        BorderPane bp = new BorderPane();
        bp.setCenter(control);
        return bp;
    }

    protected String newItem(Object n) {
        return n + "." + SequenceNumber.next();
    }

    protected String newVariableItem(Object n) {
        int rows = 1 << new Random().nextInt(5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(i);
        }
        return n + "." + SequenceNumber.next() + "." + sb;
    }

    protected void jump() {
        int sz = control.getItems().size();
        int ix = sz / 2;

        control.getSelectionModel().select(ix);
        VirtualFlow f = findVirtualFlow(control);
        f.scrollTo(ix);
        f.scrollPixels(-1.0);
    }

    private VirtualFlow findVirtualFlow(Parent parent) {
        for (Node node: parent.getChildrenUnmodifiable()) {
            if (node instanceof VirtualFlow f) {
                return f;
            }

            if (node instanceof Parent p) {
                VirtualFlow f = findVirtualFlow(p);
                if (f != null) {
                    return f;
                }
            }
        }
        return null;
    }

    @Override
    public void nullSkin() {
        control.setSkin(null);
    }

    @Override
    public void newSkin() {
        control.setSkin(new ListViewSkin(control));
    }

    private void updateCellFactory() {
        Cells t = cellFactorySelector.getSelectionModel().getSelectedItem();
        Callback<ListView<Object>, ListCell<Object>> f = getCellFactory(t);
        control.setCellFactory(f);
    }
    
    private static Image createImage(String s) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("sha-256").digest(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hash = new byte[3];
        }
        Color color = Color.rgb(hash[0] & 0xff, hash[1] & 0xff, hash[2] & 0xff);
        Canvas c = new Canvas(512, 512);
        GraphicsContext g = c.getGraphicsContext2D();
        g.setFill(color);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        return c.snapshot(null, null);
    }
    
    private Callback getCellFactory(Cells t) {
        if (t != null) {
            switch (t) {
            case LARGE_ICON:
                return (r) -> {
                    return new ListCell<Object>() {
                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {
                                String s = item.toString();
                                super.setText(s);
                                Node n = new ImageView(createImage(s));
                                super.setGraphic(n);
                            }
                        }
                    };
                };
            case VARIABLE:
                return (r) -> {
                    return new ListCell<Object>() {
                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            String s =
                                "111111111111111111111111111111111111111111111" +
                                "11111111111111111111111111111111111111111\n2\n3\n";
                            Text t = new Text(s);
                            t.wrappingWidthProperty().bind(widthProperty());
                            setPrefHeight(USE_COMPUTED_SIZE);
                            setGraphic(t);
                        }
                    };
                };
            }
        }

        // ListViewSkin
        return (r) -> new ListCell<Object>() {
            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (item instanceof Node) {
                    setText(null);
                    Node currentNode = getGraphic();
                    Node newNode = (Node)item;
                    if (currentNode == null || !currentNode.equals(newNode)) {
                        setGraphic(newNode);
                    }
                } else {
                    setText(item == null ? "null" : item.toString());
                    setGraphic(null);
                }
            }
        };
    }
}
