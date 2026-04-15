/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.tools.fx.monkey.tools.snippet;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

/**
 * Here we can run FX snippets from source.
 * 
 * TODO
 * - launch in its own JVM
 * - run the main(String...) method
 * - run javafx Application
 */
public class SnippetRunnerPane extends BorderPane {
    private final TextArea sourceField;
    // TODO CodeArea maybe?
    private final TextArea logField;

    public SnippetRunnerPane() {
        sourceField = new TextArea();
        sourceField.setStyle("-fx-font-family:'Iosevka Fixed SS16',Monospace;");
        // TODO proof of concept
        /*
        sourceField.setText("""
        public class CompilerTest {
            static {
                IO.println("static");
            }
            
            public static void main(String[] args) {
                IO.println("instance");
            }
        }
        """);
        */

        logField = new TextArea();
        logField.setEditable(false);
        
        Button runButton = new Button("▶ Run");
        runButton.setOnAction((_) -> {
            execute();
        });

        ToolBar tb = new ToolBar();
        tb.getItems().setAll(
            runButton
        );

        SplitPane split = new SplitPane(sourceField, logField);
        split.setOrientation(Orientation.VERTICAL);
        setCenter(split);
        setTop(tb);
    }

    private void execute() {
        String source = sourceField.getText();
        if (source.trim().length() > 0) {
            try {
                SnippetRunner.execute(source, new SnippetRunner.Logger() {
                    @Override
                    public void log(String message) {
                        Platform.runLater(() -> {
                            logField.appendText(message);
                        });
                    }
                });
            } catch (Throwable e) {
                // TODO stack trace
                logField.appendText(e.toString());
            }
        }
    }
}
