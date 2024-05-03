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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

/**
 * Monitors Public Properties
 */
public class PropertyMonitor extends BorderPane {
    private final TableView<Entry> table;

    public PropertyMonitor(Object owner) {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN);
        // TODO boolean checkbox for 'monitor'
        {
            TableColumn<Entry,String> c = new TableColumn<>("Name");
            c.setCellValueFactory((f) -> new SimpleStringProperty(f.getValue().getName()));
            c.setPrefWidth(120);
            table.getColumns().add(c);
        }
        {
            TableColumn<Entry,String> c = new TableColumn<>("Type");
            c.setCellValueFactory((f) -> new SimpleStringProperty(f.getValue().getType()));
            c.setPrefWidth(100);
            table.getColumns().add(c);
        }
        {
            TableColumn<Entry,Object> c = new TableColumn<>("Value");
            c.setCellValueFactory((f) -> f.getValue().getValue());
            c.setPrefWidth(300);
            table.getColumns().add(c);
        }

        table.getItems().setAll(collectProperties(owner));
        setCenter(table);
    }

    private static List<Entry> collectProperties(Object x) {
        ArrayList<Entry> a = new ArrayList<>();
        try {
            BeanInfo inf = Introspector.getBeanInfo(x.getClass());
            PropertyDescriptor[] ps = inf.getPropertyDescriptors();
            for (PropertyDescriptor p : ps) {
                Entry en = createEntry(x, p);
                if (en != null) {
                    a.add(en);
                }
            }
            Collections.sort(a, new Comparator<Entry>() {
                @Override
                public int compare(Entry a, Entry b) {
                    return a.getName().compareTo(b.getName());
                }
            });
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return a;
    }

    private static Entry createEntry(Object x, PropertyDescriptor pd) {
        Class<?> t = pd.getPropertyType();
        if (t.isAssignableFrom(EventHandler.class)) {
            return null;
        }
        if (t.isAssignableFrom(EventDispatcher.class)) {
            return null;
        }
        String name = pd.getName();
        String pname = name + "Property";
        try {
            Method m = x.getClass().getMethod(pname);
            if (m != null) {
                Object v = m.invoke(x);
                if (v instanceof ObservableValue val) {
                    return new Entry(pd.getName(), pd, val);
                }
            }
        } catch (Throwable e) {
            // ignore
        }
        return null;
    }

    static class Entry {
        private final String name;
        private final PropertyDescriptor pd;
        private final SimpleObjectProperty<Object> value = new SimpleObjectProperty<>();

        public Entry(String name, PropertyDescriptor pd, ObservableValue v) {
            this.name = name;
            this.pd = pd;
            v.addListener((s,p,c) -> {
                setValue(c);
            });
            Object y = v.getValue();
            setValue(v.getValue());
        }
        
        private void setValue(Object x) {
            if(x instanceof Node) {
                // do not set nodes!
                x = x.getClass().getSimpleName();
            }
            value.set(x);
        }

        public String getName() {
            return name;
        }

        public SimpleObjectProperty<Object> getValue() {
            // TODO add listeners upon request
            return value;
        }

        public String getType() {
            Class<?> t = pd.getPropertyType();
            return t == null ? "<null>" : t.getSimpleName();
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
