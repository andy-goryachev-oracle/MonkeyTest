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
package com.oracle.tools.fx.monkey.util;

import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

/**
 * This Selector allows for picking a value from the list.
 * Items' values are displayed according to their toString().
 */
// TODO add StringConverter for display values?
public class ItemSelector<T> {
    private final ComboBox<T> selector = new ComboBox<>();
    
    public ItemSelector(String name, Consumer<T> client, T ... items) {
        
        FX.name(selector, name);
        selector.getItems().setAll(items);
        selector.getSelectionModel().selectedItemProperty().addListener((s, p, v) -> {
            client.accept(v);
        });
    }
    
    public Node node() {
        return selector;
    }

    public T getValue() {
        return selector.getSelectionModel().getSelectedItem();
    }
    
    public T getValue(T defaultValue) {
        T v = getValue();
        return (v == null) ? defaultValue : v;
    }
    
    public void selectFirst() {
        selector.getSelectionModel().selectFirst();
    }
}
