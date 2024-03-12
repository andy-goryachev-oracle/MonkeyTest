/*
 * Copyright (c) 2022, 2024, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ConstrainedColumnResizeBase;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.ControlOptions;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.options.ObjectOption;
import com.oracle.tools.fx.monkey.options.ObjectSelector;
import com.oracle.tools.fx.monkey.util.ColumnBuilder;
import com.oracle.tools.fx.monkey.util.DataRow;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.HasSkinnable;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.SequenceNumber;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * TableView page
 */
public class TableViewPage extends TestPaneBase implements HasSkinnable {
    private enum Demo {
        ALL("all set: min, pref, max"),
        EDITABLE("editable"),
        PREF("pref only"),
        VARIABLE("variable cell height"),
        EMPTY("empty with pref"),
        MIN_WIDTH("min width"),
        MAX_WIDTH("max width"),
        MIN_WIDTH2("min width (middle)"),
        MAX_WIDTH2("max width (middle)"),
        MIN_WIDTH3("min width (beginning)"),
        MAX_WIDTH3("max width (beginning)"),
        FIXED_MIDDLE("fixed in the middle"),
        ALL_FIXED("all fixed"),
        ALL_MAX("all with maximum width"),
        MIN_IN_CENTER("min widths set in middle columns"),
        MAX_IN_CENTER("max widths set in middle columns"),
        NESTED("nested columns"),
        THOUSAND("1,000 rows"),
        MILLION("10,000,000 rows"),
        MANY_COLUMNS("many columns"),
        MANY_COLUMNS_SAME("many columns, same pref");

        private final String text;
        Demo(String text) { this.text = text; }
        @Override public String toString() { return text; }
    }

    private enum CellValue {
        VALUE,
        NULL,
        MIN_MAX,
        QUOTED,
    }

    private enum Cells {
        DEFAULT,
        GRAPHICS,
        VARIABLE,
        NULL,
    }

    private enum Selection {
        SINGLE_ROW("single row selection"),
        MULTIPLE_ROW("multiple row selection"),
        SINGLE_CELL("single cell selection"),
        MULTIPLE_CELL("multiple cell selection"),
        NULL("null selection model");

        private final String text;
        Selection(String text) { this.text = text; }
        @Override public String toString() { return text; }
    }

    private enum Filter {
        NONE("<NONE>"),
        SKIP1S("skip 11s"),
        SKIP2S("skip 22s");

        private final String text;
        Filter(String text) { this.text = text; }
        @Override public String toString() { return text; }
    }

    private enum Cmd {
        ROWS,
        COL,
        MIN,
        PREF,
        MAX,
        COMBINE,
        COL_WITH_GRAPHIC
    }

    private final ComboBox<Demo> demoSelector;
    private final ComboBox<CellValue> cellValueSelector;
    private final ComboBox<Cells> cellFactorySelector;
    private final ComboBox<Selection> selectionSelector;
    private final ComboBox<Filter> filterSelector;
    private final CheckBox hideColumn;

    private final TableView<Object> tableView;
    private final SimpleObjectProperty<TableColumn> currentColumn = new SimpleObjectProperty();

    public TableViewPage() {
        FX.name(this, "TableViewPage");

        tableView = new TableView<>();
        tableView.setPadding(new Insets(2));
        // TODO move to "background" property
        tableView.focusedProperty().subscribe(nv -> tableView.setBackground(Background.fill(nv ? Color.LIGHTGREEN : Color.LIGHTPINK)));

        demoSelector = new ComboBox<>();
        FX.name(demoSelector, "demoSelector");
        demoSelector.getItems().addAll(Demo.values());
        demoSelector.setEditable(false);
        demoSelector.getSelectionModel().selectedItemProperty().addListener((s, p, c) -> {
            updatePane();
        });

        cellValueSelector = new ComboBox<>();
        FX.name(cellValueSelector, "cellValueSelector");
        cellValueSelector.getItems().addAll(CellValue.values());
        cellValueSelector.setEditable(false);
        cellValueSelector.getSelectionModel().selectedItemProperty().addListener((s, p, c) -> {
            updateCellValueFactory();
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

        filterSelector = new ComboBox<>();
        FX.name(filterSelector, "filterSelector");
        filterSelector.getItems().addAll(Filter.values());
        filterSelector.setEditable(false);
        filterSelector.getSelectionModel().selectedItemProperty().addListener((s, p, c) -> {
            updatePane();
        });

        Button addButton = new Button("Add Data Item");
        addButton.setOnAction((ev) -> {
            tableView.getItems().add(newItem());
        });

        Button clearButton = new Button("Clear Data Items");
        clearButton.setOnAction((ev) -> {
            tableView.getItems().clear();
        });

        SplitMenuButton addColumnButton = new SplitMenuButton(
            menuItem("at the beginning", () -> addColumn(0)),
            menuItem("in the middle", () -> addColumn(1)),
            menuItem("at the end", () -> addColumn(2))
        );
        addColumnButton.setText("Add Column");

        SplitMenuButton removeColumnButton = new SplitMenuButton(
            menuItem("at the beginning", () -> removeColumn(0)),
            menuItem("in the middle", () -> removeColumn(1)),
            menuItem("at the end", () -> removeColumn(2)),
            menuItem("all", () -> removeAllColumns())
        );
        removeColumnButton.setText("Remove Column");

        hideColumn = new CheckBox("hide middle column");
        FX.name(hideColumn, "hideColumn");
        hideColumn.selectedProperty().addListener((s, p, c) -> {
            hideMiddleColumn(c);
        });

        Button refresh = new Button("Refresh");
        refresh.setOnAction((ev) -> {
            tableView.refresh();
        });

        // layout

        OptionPane op = new OptionPane();
        op.section("TableView");
        
        op.option("Columns:", createColumnsSelector("columns", tableView.getColumns()));
        op.option(addColumnButton);
        op.option(removeColumnButton);
        op.option(hideColumn);

        op.option("Column Resize Policy:", createColumnResizePolicy("columnResizePolicy", tableView.columnResizePolicyProperty()));
        
        op.option(new BooleanOption("editable", "editable", tableView.editableProperty()));
        
        op.option("Fixed Cell Size:", DoubleOption.of("fixedCellSize", tableView.fixedCellSizeProperty(), 0, 20, 33.4, 50, 100));

        op.option(new BooleanOption("tableMenuButtonVisible", "table menu button visible", tableView.tableMenuButtonVisibleProperty()));
        
        op.option("Focus Model:", createFocusModelOptions("focusModel", tableView.focusModelProperty()));
            
        // TODO separate columns and data items
        op.label("Data:");
        op.option(demoSelector);
        op.option(addButton);
        op.option(clearButton);
        // TODO
        //op.option(editable);
        op.label("Filter:");
        op.option(filterSelector);
        op.label("Cell Value:");
        op.option(cellValueSelector);
        op.label("Cell Factory:");
        op.option(cellFactorySelector);
        op.label("Selection Model:");
        op.option(selectionSelector);
        op.option(refresh);

        // currently selected column option sheet
        //TableColumnOptions.appendTo(op, currentColumn);

        // control option sheet
        ControlOptions.appendTo(tableView, op);

        setContent(tableView);
        setOptions(op);

        FX.selectFirst(demoSelector);
        FX.selectFirst(cellValueSelector);
        FX.selectFirst(cellFactorySelector);
        FX.select(selectionSelector, Selection.MULTIPLE_CELL);
        FX.selectFirst(filterSelector);
    }

    protected MenuItem menuItem(String text, Runnable r) {
        MenuItem m = new MenuItem(text);
        m.setOnAction((ev) -> r.run());
        return m;
    }

    protected void addColumn(int where) {
        TableColumn<Object, String> c = new TableColumn<>();
        c.setText("C" + System.currentTimeMillis());
        c.setCellValueFactory((f) -> new SimpleStringProperty(describe(c)));

        int ct = tableView.getColumns().size();
        int ix;
        switch (where) {
        case 0:
            ix = 0;
            break;
        case 1:
            ix = ct / 2;
            break;
        case 2:
        default:
            ix = ct;
            break;
        }
        if ((ct == 0) || (ix >= ct)) {
            tableView.getColumns().add(c);
        } else {
            tableView.getColumns().add(ix, c);
        }
    }

    protected void removeColumn(int where) {
        int ct = tableView.getColumns().size();
        int ix;
        switch (where) {
        case 0:
            ix = 0;
            break;
        case 1:
            ix = ct / 2;
            break;
        case 2:
        default:
            ix = ct - 1;
            break;
        }

        if ((ct >= 0) && (ix < ct)) {
            tableView.getColumns().remove(ix);
        }
    }

    protected void removeAllColumns() {
        tableView.getColumns().clear();
    }

    protected Callback<ResizeFeatures, Boolean> wrap(Callback<ResizeFeatures, Boolean> policy) {
        return new Callback<ResizeFeatures, Boolean>() {
            @Override
            public Boolean call(ResizeFeatures f) {
                Boolean rv = policy.call(f);
                int ix = f.getTable().getColumns().indexOf(f.getColumn());
                System.out.println(
                    "col=" + (ix < 0 ? f.getColumn() : ix) +
                    " delta=" + f.getDelta() +
                    " w=" + f.getTable().getWidth() +
                    " rv=" + rv
                );
                return rv;
            }
        };
    }

    protected String describe(TableColumn c) {
        StringBuilder sb = new StringBuilder();
        if (c.getMinWidth() != 10.0) {
            sb.append("m");
        }
        if (c.getPrefWidth() != 80.0) {
            sb.append("p");
        }
        if (c.getMaxWidth() != 5000.0) {
            sb.append("X");
        }
        return sb.toString();
    }

    // FIX remove
    protected void updatePane() {
        Demo d = demoSelector.getSelectionModel().getSelectedItem();
        Object[] spec = createSpec(d);

        //Pane n = createPane(d, spec);
        //setContent(n);
    }

    protected void combineColumns(TableView<Object> t, int ix, int count, int name) {
        TableColumn<Object, ?> tc = new TableColumn<>();
        tc.setText("N" + name);

        for (int i = 0; i < count; i++) {
            TableColumn<Object, ?> c = t.getColumns().remove(ix);
            tc.getColumns().add(c);
        }
        t.getColumns().add(ix, tc);
    }

    protected Pane createPane(Demo demo, Object[] spec) {
        if ((demo == null) || (spec == null)) {
            return new BorderPane();
        }

        boolean cellSelection = false;
        boolean nullSelectionModel = false;
        SelectionMode selectionMode = SelectionMode.SINGLE;
        Selection sel = selectionSelector.getSelectionModel().getSelectedItem();
        if (sel != null) {
            switch (sel) {
            case MULTIPLE_CELL:
                selectionMode = SelectionMode.MULTIPLE;
                cellSelection = true;
                break;
            case MULTIPLE_ROW:
                selectionMode = SelectionMode.MULTIPLE;
                break;
            case NULL:
                nullSelectionModel = true;
                break;
            case SINGLE_CELL:
                cellSelection = true;
                break;
            case SINGLE_ROW:
                break;
            default:
                throw new Error("?" + sel);
            }
        }

        tableView.getSelectionModel().setCellSelectionEnabled(cellSelection);
        tableView.getSelectionModel().setSelectionMode(selectionMode);
        if (nullSelectionModel) {
            tableView.setSelectionModel(null);
        }

        TableColumn<Object, String> lastColumn = null;
        int id = 1;

        for (int i = 0; i < spec.length;) {
            Object x = spec[i++];
            if (x instanceof Cmd cmd) {
                switch (cmd) {
                case COL:
                    {
                        TableColumn<Object, String> c = new TableColumn<>();
                        tableView.getColumns().add(c);
                        c.setText("C" + tableView.getColumns().size());
                        c.setCellValueFactory((f) -> new SimpleStringProperty(describe(c)));
                        lastColumn = c;
                    }
                    break;
                case COL_WITH_GRAPHIC:
                    {
                        TableColumn<Object, String> c = new TableColumn<>();
                        tableView.getColumns().add(c);
                        c.setText("C" + tableView.getColumns().size());
                        c.setCellValueFactory((f) -> new SimpleStringProperty(describe(c)));
                        c.setCellFactory((r) -> {
                            return new TableCell<>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    Text t = new Text(
                                        "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111\n2\n3\n");
                                    t.wrappingWidthProperty().bind(widthProperty());
                                    setPrefHeight(USE_COMPUTED_SIZE);
                                    setGraphic(t);
                                }
                            };
                        });
                        lastColumn = c;
                    }
                    break;
                case MAX:
                    {
                        int w = (int)(spec[i++]);
                        if (lastColumn == null) {
                            throw new NullPointerException();
                        }
                        lastColumn.setMaxWidth(w);
                    }
                    break;
                case MIN:
                    {
                        int w = (int)(spec[i++]);
                        if (lastColumn == null) {
                            throw new NullPointerException();
                        }
                        lastColumn.setMinWidth(w);
                    }
                    break;
                case PREF:
                    {
                        int w = (int)(spec[i++]);
                        if (lastColumn == null) {
                            throw new NullPointerException();
                        }
                        lastColumn.setPrefWidth(w);
                    }
                    break;
                case ROWS:
                    {
                        int n = (int)(spec[i++]);
                        for (int j = 0; j < n; j++) {
                            tableView.getItems().add(newItem());
                        }
                    }
                    break;
                case COMBINE:
                    int ix = (int)(spec[i++]);
                    int ct = (int)(spec[i++]);
                    combineColumns(tableView, ix, ct, id++);
                    break;
                default:
                    throw new Error("?" + cmd);
                }
            } else {
                throw new Error("?" + x);
            }
        }

        updateCellValueFactory();
        updateCellFactory();

        switch (demo) {
        case EDITABLE:
            tableView.setEditable(true);
            {
                TableColumn<Object, String> c = new TableColumn<>("First Name");
                c.setCellValueFactory((x) -> ((Item)x.getValue()).firstName);
                tableView.getColumns().add(c);
            }
            {
                TableColumn<Object, String> c = new TableColumn<>("Last Name");
                c.setCellValueFactory((x) -> ((Item)x.getValue()).lastName);
                c.setCellFactory(TextFieldTableCell.forTableColumn());
                tableView.getColumns().add(c);
            }
            {
                TableColumn<Object, Integer> c = new TableColumn<>("Age");
                c.setCellValueFactory((x) -> ((Item)x.getValue()).age);
                tableView.getColumns().add(c);
            }

            tableView.getItems().addAll(
                new Item("John", "Doe", 55),
                new Item("Jane", "Deer", 25),
                new Item("A", "B", 99)
            );
        }

        hideMiddleColumn(hideColumn.isSelected());

        Filter f = filterSelector.getSelectionModel().getSelectedItem();
        if (f == Filter.NONE) {
            f = null;
        }
        if (f != null) {
            ObservableList<Object> items = FXCollections.observableArrayList();
            items.addAll(tableView.getItems());
            FilteredList<Object> filteredList = new FilteredList<>(items);
            switch(f) {
            case SKIP1S:
                filteredList.setPredicate((s) -> {
                    if (s == null) {
                        return true;
                    }
                    return !((String)s).contains("11");
                });
                break;
            case SKIP2S:
                filteredList.setPredicate((s) -> {
                    if (s == null) {
                        return true;
                    }
                    return !((String)s).contains("22");
                });
                break;
            default:
                throw new Error("?" + f);
            }
            tableView.setItems(filteredList);
        }

        BorderPane bp = new BorderPane();
        bp.setCenter(tableView);
        return bp;
    }

    protected void hideMiddleColumn(boolean on) {
        if (on) {
            int ct = tableView.getColumns().size();
            if (ct > 0) {
                tableView.getColumns().get(ct / 2).setVisible(false);
            }
        } else {
            for (TableColumn c: tableView.getColumns()) {
                c.setVisible(true);
            }
        }
    }

    protected String newItem() {
        return SequenceNumber.next();
    }

    @Override
    public void nullSkin() {
        tableView.setSkin(null);
    }

    @Override
    public void newSkin() {
        tableView.setSkin(new TableViewSkin<>(tableView));
    }

    private Callback<CellDataFeatures<Object, String>, ObservableValue<String>> getValueFactory(CellValue t) {
        if (t != null) {
            switch (t) {
            case MIN_MAX:
                return (f) -> {
                    String s = describe(f.getTableColumn());
                    return new SimpleStringProperty(s);
                };
            case QUOTED:
                return (f) -> {
                    String s = "\"" + f.getValue() + '"';
                    return new SimpleStringProperty(s);
                };
            case VALUE:
                return (f) -> {
                    String s = String.valueOf(f.getValue());
                    return new SimpleStringProperty(s);
                };
            }
        }
        return null;
    }

    private Node getIcon(String text) {
        if (text.contains("0")) {
            return icon(Color.RED);
        } else if (text.contains("1")) {
            return icon(Color.GREEN);
        }
        return null;
    }

    private Node icon(Color color) {
        Canvas c = new Canvas(16, 16);
        GraphicsContext g = c.getGraphicsContext2D();
        g.setFill(color);
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        return c;
    }

    private Callback getCellFactory(Cells t) {
        if (t != null) {
            switch (t) {
            case NULL:
                return null;
            case GRAPHICS:
                return (r) -> {
                    return new TableCell<String,String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null) {
                                super.setText(null);
                                super.setGraphic(null);
                            } else {
                                String s = item.toString();
                                super.setText(s);
                                Node n = getIcon(s);
                                super.setGraphic(n);
                            }
                        }
                    };
                };
            case VARIABLE:
                return (r) -> {
                    return new TableCell<String,String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
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
        return TableColumn.DEFAULT_CELL_FACTORY;
    }

    private void updateColumns(Consumer<TableColumn<Object, String>> handler) {
        if (tableView != null) {
            for (TableColumn<Object, ?> c: tableView.getColumns()) {
                handler.accept((TableColumn<Object, String>)c);
            }
        }
    }

    private void updateCellValueFactory() {
        CellValue t = cellValueSelector.getSelectionModel().getSelectedItem();
        Callback<CellDataFeatures<Object, String>, ObservableValue<String>> f = getValueFactory(t);
        updateColumns((c) -> {
            c.setCellValueFactory(f);
        });
    }

    private void updateCellFactory() {
        Cells t = cellFactorySelector.getSelectionModel().getSelectedItem();
        Callback<TableColumn<Object, String>, TableCell<Object, String>> f = getCellFactory(t);
        if (f != null) {
            updateColumns((c) -> {
                c.setCellFactory(f);
            });
        }
    }

    /**
     * a user-defined policy demonstrates that we can indeed create a custom policy using the new API.
     * this policy simply sizes all columns equally.
     */
    static class UserDefinedResizePolicy
        extends ConstrainedColumnResizeBase
        implements Callback<TableView.ResizeFeatures, Boolean> {

        @SuppressWarnings("unchecked")
        @Override
        public Boolean call(ResizeFeatures rf) {
            List<? extends TableColumnBase<?, ?>> visibleLeafColumns = rf.getTable().getVisibleLeafColumns();
            int sz = visibleLeafColumns.size();
            // using added public method getContentWidth()
            double w = rf.getContentWidth() / sz;
            for (TableColumnBase<?, ?> c: visibleLeafColumns) {
                // using added public method setColumnWidth()
                rf.setColumnWidth(c, w);
            }
            return false;
        }
    }

    static class Item {
        public final SimpleStringProperty firstName = new SimpleStringProperty();
        public final SimpleStringProperty lastName = new SimpleStringProperty();
        public final SimpleObjectProperty<Integer> age = new SimpleObjectProperty<>();

        public Item(String firstName, String lastName, int age) {
            this.firstName.set(firstName);
            this.lastName.set(lastName);
            this.age.set(age);
        }
    }

    private Node createColumnsSelector(String name, ObservableList<TableColumn<Object, ?>> columns) {
        ObjectSelector<ObservableList<TableColumn<Object, ?>>> s = new ObjectSelector<>(name, (v) -> {
            columns.setAll(v);
        });
        s.addChoice("With All Constraints", columnBuilder().
            col("Fixed").min(70).max(70).
            col("MinPrefMax").min(50).pref(200).max(300).
            col("Min").min(50).
            col("Pref").pref(200).
            col("Max").max(150).
            col("Std").
            asList()
        );
        s.addChoiceSupplier("Many Equal Widths", () -> {
            var cs = columnBuilder();
            for(int i=1; i<20; i++) {
                cs.col("C" + i);
            }
            return cs.asList();
        });
        s.addChoice("<empty>", FXCollections.observableArrayList());
        return s;
    }

    private ColumnBuilder<TableColumn<Object,?>> columnBuilder() {
        return new ColumnBuilder<>(() -> {
            TableColumn<Object,?> tc = new TableColumn();
            tc.setCellValueFactory((cdf) -> {
                Object v = cdf.getValue();
                if(v instanceof DataRow r) {
                    return r.getValue(tc);
                }
                return new SimpleObjectProperty(v);
            });
            return tc;
        });
    }

    protected Object[] createSpec(Demo d) {
        switch (d) {
        case ALL:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL, Cmd.MIN, 30, Cmd.MAX, 30,
                Cmd.COL, Cmd.PREF, 200,
                Cmd.COL, Cmd.PREF, 300, Cmd.MAX, 400,
                Cmd.COL, Cmd.MIN, 40,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            };
        case PREF:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.PREF, 100,
                Cmd.COL, Cmd.PREF, 200,
                Cmd.COL, Cmd.PREF, 300,
                Cmd.COL, Cmd.PREF, 400
            };
        case VARIABLE:
            return new Object[] {
                Cmd.ROWS, 10_000,
                Cmd.COL_WITH_GRAPHIC,
                Cmd.COL_WITH_GRAPHIC,
                Cmd.COL_WITH_GRAPHIC
            };
        case EMPTY:
            return new Object[] {
                Cmd.COL, Cmd.PREF, 100,
                Cmd.COL, Cmd.PREF, 200,
                Cmd.COL, Cmd.PREF, 300
            };
        case MIN_WIDTH:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL, Cmd.MIN, 300
            };
        case MAX_WIDTH:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL, Cmd.MAX, 100
            };
        case MIN_WIDTH2:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL, Cmd.MIN, 300,
                Cmd.COL
            };
        case MAX_WIDTH2:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL, Cmd.MAX, 100,
                Cmd.COL
            };
        case MIN_WIDTH3:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.MIN, 300,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            };
        case MAX_WIDTH3:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.MAX, 100,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            };
        case MIN_IN_CENTER:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL, Cmd.MIN, 20,
                Cmd.COL, Cmd.MIN, 30,
                Cmd.COL, Cmd.MIN, 40,
                Cmd.COL, Cmd.MIN, 50,
                Cmd.COL, Cmd.MIN, 60,
                Cmd.COL
            };
        case MAX_IN_CENTER:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL, Cmd.MAX, 20,
                Cmd.COL, Cmd.MAX, 30,
                Cmd.COL, Cmd.MAX, 40,
                Cmd.COL, Cmd.MAX, 50,
                Cmd.COL, Cmd.MAX, 60,
                Cmd.COL
            };
        case FIXED_MIDDLE:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL, Cmd.MIN, 100, Cmd.MAX, 100,
                Cmd.COL, Cmd.MIN, 100, Cmd.MAX, 100,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            };
        case ALL_FIXED:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.MIN, 50, Cmd.MAX, 50,
                Cmd.COL, Cmd.MIN, 50, Cmd.MAX, 50,
                Cmd.COL, Cmd.MIN, 50, Cmd.MAX, 50
            };
        case ALL_MAX:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.MAX, 50,
                Cmd.COL, Cmd.MAX, 50,
                Cmd.COL, Cmd.MAX, 50
            };
        case NESTED:
            return new Object[] {
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.PREF, 100,
                Cmd.COL, Cmd.PREF, 200,
                Cmd.COL, Cmd.PREF, 300,
                Cmd.COL, Cmd.MIN, 100, Cmd.MAX, 100,
                Cmd.COL, Cmd.PREF, 100,
                Cmd.COL, Cmd.MIN, 100,
                Cmd.COL, Cmd.MAX, 100,
                Cmd.COL, Cmd.PREF, 400,
                Cmd.COL,
                Cmd.COMBINE, 0, 3,
                Cmd.COMBINE, 1, 2
            };
        case MANY_COLUMNS:
            return new Object[] {
                Cmd.ROWS, 300,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            };
        case MANY_COLUMNS_SAME:
            return new Object[] {
                Cmd.ROWS, 300,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30,
                Cmd.COL, Cmd.PREF, 30
            };
        case MILLION:
            return new Object[] {
                Cmd.ROWS, 10_000_000,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            };
        case THOUSAND:
            return new Object[] {
                Cmd.ROWS, 1_000,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            };
        case EDITABLE:
            return new Object[0];
        default:
            throw new Error("?" + d);
        }
    }

    private ObservableList<TableColumn<Object, ?>> createColumns() {
        return null;
    }

    private Node createColumnResizePolicy(String name, ObjectProperty<Callback<ResizeFeatures, Boolean>> p) {
        ObjectOption<Callback<ResizeFeatures, Boolean>> s = new ObjectOption<>(name, p);
        s.addChoice("AUTO_RESIZE_FLEX_NEXT_COLUMN", TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
        s.addChoice("AUTO_RESIZE_FLEX_LAST_COLUMN", TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        s.addChoice("AUTO_RESIZE_ALL_COLUMNS", TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        s.addChoice("AUTO_RESIZE_LAST_COLUMN", TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        s.addChoice("AUTO_RESIZE_NEXT_COLUMN", TableView.CONSTRAINED_RESIZE_POLICY_NEXT_COLUMN);
        s.addChoice("AUTO_RESIZE_SUBSEQUENT_COLUMNS", TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        //s.addChoice("CONSTRAINED_RESIZE_POLICY", TableView.CONSTRAINED_RESIZE_POLICY);
        s.addChoice("UNCONSTRAINED_RESIZE_POLICY", TableView.UNCONSTRAINED_RESIZE_POLICY);
        s.addChoice("user defined, equal width", new UserDefinedResizePolicy());
        return s;
    }

    private Node createFocusModelOptions(String name, ObjectProperty<TableView.TableViewFocusModel<Object>> p) {
        ObjectOption<TableView.TableViewFocusModel<Object>> s = new ObjectOption<>(name, p);
        s.addChoiceSupplier("<default>", () -> new TableView.TableViewFocusModel(tableView));
        s.addChoice("<null>", null);
        return s;
    }
}
