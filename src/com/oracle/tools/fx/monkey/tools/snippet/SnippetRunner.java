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

/**
 * Supports running JavaFX snippets from source.
 */
public class SnippetRunner {
    
    public interface Logger {
        public void log(String message); 
    }

    public static void execute(String sourceText, Logger logger) throws Throwable {
        JavDoc jd = JavDoc.of(sourceText);
        String name = jd.getName();
        String src = jd.getTransformedSource();
        System.out.println("\n\n---\n\n" + src + "\n\n---\n\n"); // FIX
        JavaFileObject file = new StringJavaSource(name, src);
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        InMemoryJavaFileManager fm = InMemoryJavaFileManager.init(compiler);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        CompilationTask task = compiler.getTask(null, fm, diagnostics, null, null, compilationUnits);
        
        boolean success = task.call();

        // TODO log errors
        for (Diagnostic d: diagnostics.getDiagnostics()) {
            //    code=compiler.err.expected
            //    kind=ERROR
            //    pos=6
            //    start=6
            //    end=6
            //    source=com.oracle.tools.fx.monkey.tools.snippet.StringJavaSource[in-mem:///CompilerTest.java]
            //    message=<identifier> expected

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
                    return;
                } else if (Application.class.isAssignableFrom(tc)) {
                    Application.launch(tc);
                    return;
                }
                System.err.println("Class must have main(String) or extend Application: " + tc);
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
