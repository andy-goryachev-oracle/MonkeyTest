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

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.TestPaneBase;

/**
 * Test code from CheckBoxTreeEditor, see https://bugs.openjdk.org/browse/JDK-8209017
 *
 * TODO add different cell types
 */
public class TreeViewPage extends TestPaneBase {
    private TreeView<String> control;
    private int childNum;

    public TreeViewPage() {
        FX.name(this, "TreeViewPage");

        CheckBox indeterminate = new CheckBox("Indeterminate");
        FX.name(indeterminate, "indeterminate");

        CheckBox selected = new CheckBox("Selected");
        FX.name(selected, "selected");

        Button add = new Button("Add");
        add.setOnAction((ev) -> {
            addChild(indeterminate.isSelected(), selected.isSelected());
        });

        Button remove = new Button("Remove");
        remove.setOnAction((ev) -> {
            removeChild();
        });

        toolbar().addAll(
            add,
            remove,
            indeterminate,
            selected
        );

        updatePane();
    }

    protected void updatePane() {
        control = new TreeView<>(new CheckBoxTreeItem<>("root"));
        control.setCellFactory(CheckBoxTreeCell.<String>forTreeView());

        Button button = new Button("0");
//        control.getRoot().setGraphic(button); // TODO add an option
        control.getRoot().setExpanded(true);
        control.getSelectionModel().select(control.getRoot());

        // add children for initial setup as needed
        addChild(true, true);

        setContent(control);
    }

    private void addChild(boolean indeterminate, boolean selected) {
        CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>("child " + childNum++);
        Button button = new Button("" + childNum);
//        item.setGraphic(button);
        item.setSelected(selected);
        item.setIndeterminate(indeterminate);
        item.setExpanded(true);

        if (control.getSelectionModel().getSelectedItem() != null) {
            control.getSelectionModel().getSelectedItem().getChildren().add(item);
        }
    }

    private void removeChild() {
        TreeItem<String> sel = control.getSelectionModel().getSelectedItem();
        if (sel != null) {
            TreeItem<String> parent = sel.getParent();
            if (parent != null) {
                parent.getChildren().remove(sel);
            }
        }
    }
}
