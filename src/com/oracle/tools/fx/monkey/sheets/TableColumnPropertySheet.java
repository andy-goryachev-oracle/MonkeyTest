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
package com.oracle.tools.fx.monkey.sheets;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.options.GraphicOption;
import com.oracle.tools.fx.monkey.options.TextOption;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;

/**
 * TreeTableView/TableView (Selected) Column Property Sheet
 */
public class TableColumnPropertySheet extends BorderPane {
    protected TableColumnPropertySheet(TableColumnBase<?,?> c) {
        OptionPane op = new OptionPane();
        if(c instanceof TableColumn tc) {
            tableColumnOptions(op, tc);
        } else if(c instanceof TreeTableColumn tc) {
            treeTableColumnOptions(op, tc);
        }
        tableColumnBaseOptions(op, c);
        setCenter(op);
    }

    public static void open(Object parent, TableColumnBase<?, ?> c) {
        String name = c.getText();
        if (name == null) {
            name = "<null>";
        } else {
            name = " [" + name + "]";
        }
        TableColumnPropertySheet p = new TableColumnPropertySheet(c);
        ScrollPane sp = new ScrollPane(p);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        Stage w = new Stage();
        w.initOwner(FX.getParentWindow(parent));
        w.setScene(new Scene(sp));
        w.setTitle("Table Column Properties" + name);
        w.addEventHandler(KeyEvent.KEY_RELEASED, p::handleKey);
        w.setWidth(500);
        w.setHeight(800);
        w.show();
    }

    private void handleKey(KeyEvent ev) {
        if (ev.getCode() == KeyCode.ESCAPE) {
            Stage w = (Stage)ev.getSource();
            w.hide();
        }
    }

    private void tableColumnOptions(OptionPane op, TableColumn<?, ?> c) {
        op.section("TableColumn");
        op.option("Cell Factory: TODO", null); // TODO
        op.option("Cell Value Factory: TODO", null); // TODO
        op.option("Sort Type:", new EnumOption(null, TableColumn.SortType.class, c.sortTypeProperty()));
    }

    private void treeTableColumnOptions(OptionPane op, TreeTableColumn<?, ?> c) {
        op.section("TreeTableColumn");
        op.option("Cell Factory: TODO", null); // TODO
        op.option("Cell Value Factory: TODO", null); // TODO
        op.option("Sort Type:", new EnumOption(null, TreeTableColumn.SortType.class, c.sortTypeProperty()));
    }

    private void tableColumnBaseOptions(OptionPane op, TableColumnBase<?, ?> c) {
        op.section("TableColumnBase");
        op.option("Comparator: TODO", null); // TODO
        op.option("Context Menu: TODO", null); // TODO
        op.option(new BooleanOption(null, "editable", c.editableProperty()));
        op.option("Graphic:", new GraphicOption("graphic", c.graphicProperty()));
        op.option("Id:", new TextOption("id", c.idProperty()));
        op.option("Max Width:", Options.forColumnWidth("maxWidth", 5000.0, c.maxWidthProperty()));
        op.option("Min Width:", Options.forColumnWidth("minWidth", 10.0, c.minWidthProperty()));
        op.option("Pref Width:", Options.forColumnWidth("prefWidth", 80.0, c.prefWidthProperty()));
        op.option(new BooleanOption(null, "reorderable", c.reorderableProperty()));
        op.option(new BooleanOption(null, "resizeable", c.resizableProperty()));
        op.option(new BooleanOption(null, "sortable", c.sortableProperty()));
        op.option("Sort Node: TODO", null); // TODO
        op.option("Style:", new TextOption("style", c.styleProperty()));
        op.option("Text:", new TextOption("text", c.textProperty()));
        op.option("User Data: TODO", null); // TODO
        op.option(new BooleanOption(null, "visible", c.visibleProperty()));
    }
//  case COL_WITH_GRAPHIC:
//  {
//      TableColumn<Object, String> c = new TableColumn<>();
//      tableView.getColumns().add(c);
//      c.setText("C" + tableView.getColumns().size());
//      c.setCellValueFactory((f) -> new SimpleStringProperty(describe(c)));
//      c.setCellFactory((r) -> {
//          return new TableCell<>() {
//              @Override
//              protected void updateItem(String item, boolean empty) {
//                  super.updateItem(item, empty);
//                  Text t = new Text(
//                      "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111\n2\n3\n");
//                  t.wrappingWidthProperty().bind(widthProperty());
//                  setPrefHeight(USE_COMPUTED_SIZE);
//                  setGraphic(t);
//              }
//          };
//      });
//      lastColumn = c;
//  }

// FIX move to column menu
//void filter() {
//Filter f = filterSelector.getSelectionModel().getSelectedItem();
//if (f == Filter.NONE) {
//f = null;
//}
//if (f != null) {
//ObservableList<Object> items = FXCollections.observableArrayList();
//items.addAll(tableView.getItems());
//FilteredList<Object> filteredList = new FilteredList<>(items);
//switch(f) {
//case SKIP1S:
//filteredList.setPredicate((s) -> {
//  if (s == null) {
//      return true;
//  }
//  return !((String)s).contains("11");
//});
//break;
//case SKIP2S:
//filteredList.setPredicate((s) -> {
//  if (s == null) {
//      return true;
//  }
//  return !((String)s).contains("22");
//});
//break;
//default:
//throw new Error("?" + f);
//}
////tableView.setItems(filteredList);
//}
//}


    // FIX move to column menu
//    private Callback<CellDataFeatures<Object, String>, ObservableValue<String>> getValueFactory(CellValue t) {
//        if (t != null) {
//            switch (t) {
//            case MIN_MAX:
//                return (f) -> {
//                    String s = describe(f.getTableColumn());
//                    return new SimpleStringProperty(s);
//                };
//            case QUOTED:
//                return (f) -> {
//                    String s = "\"" + f.getValue() + '"';
//                    return new SimpleStringProperty(s);
//                };
//            case VALUE:
//                return (f) -> {
//                    String s = String.valueOf(f.getValue());
//                    return new SimpleStringProperty(s);
//                };
//            }
//        }
//        return null;
//    }

//    private Node getIcon(String text) {
//        if (text.contains("0")) {
//            return icon(Color.RED);
//        } else if (text.contains("1")) {
//            return icon(Color.GREEN);
//        }
//        return null;
//    }
//
//    private Node icon(Color color) {
//        Canvas c = new Canvas(16, 16);
//        GraphicsContext g = c.getGraphicsContext2D();
//        g.setFill(color);
//        g.fillRect(0, 0, c.getWidth(), c.getHeight());
//        return c;
//    }

    // FIX move to column menu
//    private Callback getCellFactory(Cells t) {
//        if (t != null) {
//            switch (t) {
//            case NULL:
//                return null;
//            case GRAPHICS:
//                return (r) -> {
//                    return new TableCell<String,String>() {
//                        @Override
//                        protected void updateItem(String item, boolean empty) {
//                            super.updateItem(item, empty);
//                            if (item == null) {
//                                super.setText(null);
//                                super.setGraphic(null);
//                            } else {
//                                String s = item.toString();
//                                super.setText(s);
//                                Node n = getIcon(s);
//                                super.setGraphic(n);
//                            }
//                        }
//                    };
//                };
//            case VARIABLE:
//                return (r) -> {
//                    return new TableCell<String,String>() {
//                        @Override
//                        protected void updateItem(String item, boolean empty) {
//                            super.updateItem(item, empty);
//                            String s =
//                                "111111111111111111111111111111111111111111111" +
//                                "11111111111111111111111111111111111111111\n2\n3\n";
//                            Text t = new Text(s);
//                            t.wrappingWidthProperty().bind(widthProperty());
//                            setPrefHeight(USE_COMPUTED_SIZE);
//                            setGraphic(t);
//                        }
//                    };
//                };
//            }
//        }
//        return TableColumn.DEFAULT_CELL_FACTORY;
//    }
}
