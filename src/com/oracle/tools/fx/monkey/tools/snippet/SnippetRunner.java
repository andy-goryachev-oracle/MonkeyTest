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

import java.lang.reflect.Method;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javafx.application.Application;
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
public class SnippetRunner extends BorderPane {
    private final TextArea sourceField;
    private final TextArea logField;

    public SnippetRunner() {
        sourceField = new TextArea();
        sourceField.setStyle("-fx-font-family:'Iosevka Fixed SS16',Monospace;");
        // TODO proof of concept
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
        
        logField = new TextArea();
        logField.setEditable(false);
        
        Button runButton = new Button("▶ Run");
        runButton.setOnAction((_) -> {
            execute();
        });

        Button closeButton = new Button("Close");
        closeButton.setOnAction((_) -> {
            // TODO hide();
        });
        
        ToolBar tb = new ToolBar();
        tb.getItems().setAll(
            runButton,
            closeButton
        );
        
        SplitPane split = new SplitPane(sourceField, logField);
        split.setOrientation(Orientation.VERTICAL);
        setCenter(split);
        setTop(tb);
    }

    private void execute() {
        String source = sourceField.getText();
        if(source.trim().length() > 0) {
            try {
                execute(source);
            } catch (Throwable e) {
                // TODO stack trace
                logField.appendText(e.toString());
            }
        }
    }

    private String extractName(String source) throws Exception {
        String prefix = "public class ";
        int start = source.indexOf(prefix);
        int len = prefix.length();
        if (start < 0) {
            throw new Exception("no public class defined");
        }
        int end = source.indexOf("extends Application", start + len);
        if (end < 0) {
            end = source.indexOf("{", start + len);
            if (end < 0) {
                throw new Exception("cannot find the class name");
            }
        }
        return source.substring(start + len, end).trim();
    }

    private void execute(String source) throws Exception {
        
        String name = extractName(source);
        
        JavaFileObject file = new StringJavaSource(name, source);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        InMemoryJavaFileManager fm = InMemoryJavaFileManager.init(compiler);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        CompilationTask task = compiler.getTask(null, fm, diagnostics, null, null, compilationUnits);
        
        boolean success = task.call();

        for (Diagnostic d: diagnostics.getDiagnostics()) {
            System.out.println("code=" + d.getCode());
            System.out.println("kind=" + d.getKind());
            System.out.println("pos=" + d.getPosition());
            System.out.println("start=" + d.getStartPosition());
            System.out.println("end=" + d.getEndPosition());
            System.out.println("source=" + d.getSource());
            System.out.println("message=" + d.getMessage(null));
        }

        // TODO this is a proof of concept,
        // should run in its own JVM
        
        if (success) {
            try {
                ClassLoader ldr = fm.getInMemClassLoader();
                Class tc = Class.forName(name, true, ldr);
                Method main = getMethod(tc, "main", String[].class);
                if (main != null) {
                    main.invoke(null, new Object[] { new String[0] });
                } else {
                    if (Application.class.isAssignableFrom(tc)) {
                        // TODO module path, lauch jdk, command line options
                        Application.launch(tc);
                    } else {
                        System.err.println("Don't know how to launch " + tc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Method getMethod(Class<?> c, String name, Class<?> ... args) {
        try {
            return c.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
