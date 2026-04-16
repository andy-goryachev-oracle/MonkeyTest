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
import com.oracle.tools.fx.monkey.util.Utils;

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
        sourceField.setText(getText());

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

    // TODO remove later
    private static String getText() {
        return switch(2) {
        case 1 ->
            """
            public class CompilerTest {
                static {
                    IO.println("static");
                }
                
                public static void main(String[] args) {
                    IO.println("instance");
                }
            }
            """;
        case 2 ->
            """
            package goryachev.bugs;
    
            import javafx.application.Application;
            import javafx.scene.Scene;
            import javafx.scene.control.Button;
            import javafx.scene.control.ComboBox;
            import javafx.scene.control.ToolBar;
            import javafx.scene.layout.BorderPane;
            import javafx.stage.Stage;
    
            /// https://bugs.openjdk.org/browse/JDK-8374214
            public class ToolBar_OverflowButton_8374214 extends Application {
                @Override
                public void start(Stage stage) throws Exception {
                    ComboBox<String> cbox = new ComboBox<>();
                    cbox.getItems().add("Lalalalalalalalalalalalalalalalalalalalalalalalalalala");
                    
                    // BUG: messes up the toolbar overflow button logic 
                    cbox.setMaxWidth(100);
                    // this code works correctly
                    //cbox.setPrefWidth(100);
    
                    ToolBar tb = new ToolBar();
                    tb.getItems().addAll(
                        cbox,
                        new Button("1")
                    );
                    
                    BorderPane bp = new BorderPane();
                    bp.setTop(tb);
    
                    stage.setScene(new Scene(bp, 200, 200));
                    stage.show();
                }
            }
            """;
        default -> null;
        };
    }

    private void append(String message) {
        logField.appendText(message);
        logField.appendText("\n");
    }

    private void execute() {
        logField.setText(null);
        String source = sourceField.getText();
        if (source.trim().length() > 0) {
            try {
                SnippetRunner.execute(source, new SnippetRunner.Logger() {
                    @Override
                    public void log(String message) {
                        Platform.runLater(() -> {
                            append(message);
                        });
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
                append(Utils.printStackTrace(e));
            }
        }
    }
}
