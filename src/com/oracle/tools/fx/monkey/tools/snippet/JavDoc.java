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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Java document that supports per-line transformation.
 */
class JavDoc {
    private static final Pattern LINE_BREAKS = Pattern.compile("\\R");
    private static final Pattern PACKAGE = Pattern.compile("^\\s*package\s+.*;.*");
    private static final Pattern CLASS_NAME = Pattern.compile("^.*class\\s+([A-Za-z0-9_$]+)\\s+.*");
    private final List<Ln> lines;
    private String name;
    private int nameStart = -1;
    private int nameEnd = -1;
    private int nameIndex = -1;

    private JavDoc(List<Ln> lines) {
        this.lines = lines;
    }

    // TODO ParseError
    public static JavDoc of(String text) throws Exception {
        String[] ss = LINE_BREAKS.split(text);
        ArrayList<Ln> lines = new ArrayList<>(ss.length + 1);

        // inject default imports
        Ln imports = new Ln(-1, "");
        imports.setTransformedText(
            "import javafx.application.*;",
            "import javafx.beans.binding.*;",
            "import javafx.collections.*;",
            "import javafx.event.*;",
            "import javafx.geometry.*;",
            "import javafx.scene.*;",
            "import javafx.scene.control.*;",
            "import javafx.scene.input.*;",
            "import javafx.scene.layout.*;",
            "import javafx.scene.text.*;",
            "import javafx.stage.*;"
        );
        lines.add(imports);

        for (int i = 0; i < ss.length; i++) {
            lines.add(new Ln(i, ss[i]));
        }
        JavDoc d = new JavDoc(lines);
        d.process();
        return d;
    }
    
    private void process() throws Exception {
        // remove package definition and determine the class name
        for(int i=0; i<lines.size(); i++) {
            Ln ln = lines.get(i);
            String text = ln.getOriginalText();
            if(PACKAGE.matcher(text).matches()) {
                // remove package name
                ln.setTransformedText("");
            } else {
                // extract class name
                Matcher m = CLASS_NAME.matcher(text);
                if(m.matches()) {
                    // done with modifications
                    name = m.group(1);
                    nameStart = m.start(1);
                    nameEnd = m.end(1);
                    nameIndex = i;
                    break;
                }
            }
        }
        
        if(name == null) {
            throw new Exception("class name not detected");
        }
    }

    public String getName() {
        return name;
    }

    public String replaceName(String replace) {
        Ln ln = lines.get(nameIndex);
        String text = ln.getOriginalText();
        String s = text.substring(0, nameStart) + replace + text.substring(nameEnd);
        ln.setTransformedText(s);
        return replace;
    }

    public String getTransformedSource() {
        StringBuilder sb = new StringBuilder(4096);
        for (Ln ln : lines) {
            String[] tr = ln.getTransformedLines();
            if (tr == null) {
                sb.append(ln.getOriginalText());
                sb.append('\n');
            } else {
                for (String s : tr) {
                    sb.append(s);
                    sb.append('\n');
                }
            }
        }
        return sb.toString();
    }

    static class Ln {
        private final int originalIndex;
        private final String originalText;
        private String[] transformed;
        private int delta;

        public Ln(int originalIndex, String originalText) {
            this.originalIndex = originalIndex;
            this.originalText = originalText;
        }
        
        public String getOriginalText() {
            return originalText;
        }

        public void setTransformedText(String ... s) {
            transformed = s;
            delta = s.length - 1;
        }
        
        public String[] getTransformedLines() {
            return transformed;
        }
    }
}
