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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * In-memory JavaFileManager.
 */
class InMemoryJavaFileManager implements JavaFileManager {
    public static final String PROTOCOL = "in-mem";
    private final StandardJavaFileManager fm;
    private final HashMap<String,InMemoryJavaFileOutput> files = new HashMap<>();

    private InMemoryJavaFileManager(StandardJavaFileManager fm) {
        this.fm = fm;
    }
    
    public static InMemoryJavaFileManager create(JavaCompiler compiler) {
        StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null);
        return new InMemoryJavaFileManager(fm);
    }

    public static String createUrl(String name, boolean source) {
        return PROTOCOL + ":///" + name.replace('.', '/') + (source ? ".java" : ".class");
    }

    // TODO
    private static void p(String s) {
        if (true) {
            System.out.println(s);
        }
    }

    @Override
    public int isSupportedOption(String option) {
        var v = fm.isSupportedOption(option);
        p("FM.isSupportedOption " + option + " " + v);
        return v;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        var v = fm.getClassLoader(location);
        p("FM.getClassLoader " + location + " " + v);
        return getInMemClassLoader();
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        p("FM.list " + location);
        var v = fm.list(location, packageName, kinds, recurse);
        //p("list " + location + " " + v);
        return v;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        var v = fm.inferBinaryName(location, file);
        p("FM.inferBinaryName " + location + " file=" + file + " " + v);
        return v;
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        var v = fm.isSameFile(a, b);
        p("FM.isSameFile " + a + " " + b + " " + v);
        return v;
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        p("FM.handleOption " + current);
        return fm.handleOption(current, remaining);
    }

    @Override
    public boolean hasLocation(Location location) {
        //p("FM.hasLocation " + location);
        var v = fm.hasLocation(location);
        p("FM.hasLocation " + location + " " + v);
        return v;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        var v = fm.getJavaFileForInput(location, className, kind);
        p("FM.getJavaFileForInput " + location + " " + v);
        return v;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        p("FM.getJavaFileForOutput " + location + " className=" + className + " kind=" + kind + " sibling=" + sibling);
        if (location.isOutputLocation()) {
            URI uri = URI.create(createUrl(className, false));
            InMemoryJavaFileOutput f = new InMemoryJavaFileOutput(uri, kind);
            files.put(className, f);
            return f;
        }
        var v = fm.getJavaFileForOutput(location, className, kind, sibling);
        p("FM.getJavaFileForOutput " + location + " className=" + className + " kind=" + kind + " sibling=" + sibling + " " + v);
        return v;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        var v = fm.getFileForInput(location, packageName, relativeName);
        p("FM.getFileForInput " + location + " " + v);
        return v;
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        var v = fm.getFileForOutput(location, packageName, relativeName, sibling);
        p("FM.getFileForOutput " + location + " packageName=" + packageName + " relativeName=" + relativeName + " sibling=" + sibling + " " + v);
        return v;
    }

    @Override
    public void flush() throws IOException {
        fm.flush();
    }

    @Override
    public void close() throws IOException {
        fm.close();
    }

    @Override
    public Iterable<Set<Location>> listLocationsForModules(Location location) throws IOException {
        p("FM.listLocationsForModules " + location);
        return fm.listLocationsForModules(location);
    }

    @Override
    public String inferModuleName(Location location) throws IOException {
        p("FM.inferModuleName " + location);
        return fm.inferModuleName(location);
    }
    
    public ClassLoader getInMemClassLoader() {
        return new ClassLoader() {
            @Override
            protected URL findResource(String name) {
                p("CL.findResource name=" + name);
                try {
                    return URL.of(URI.create(createUrl(name, false)), null);
                } catch(Exception e) {
                    throw new Error(e);
                }
            }
            
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                String path = name.replace('.', '/').concat(".class");
                InMemoryJavaFileOutput f = files.get(name);
                if (f == null) {
                    throw new ClassNotFoundException(name);
                } else {
                    byte[] b = f.getBytes();
                    return defineClass(name, b, 0, b.length);
                }
            }
        };
    }
}