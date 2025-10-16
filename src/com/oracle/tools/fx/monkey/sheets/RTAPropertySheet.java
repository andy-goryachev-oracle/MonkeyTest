/*
 * Copyright (c) 2025, Oracle and/or its affiliates. All rights reserved.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.DurationOption;
import com.oracle.tools.fx.monkey.options.FontOption;
import com.oracle.tools.fx.monkey.options.InsetsOption;
import com.oracle.tools.fx.monkey.options.ObjectOption;
import com.oracle.tools.fx.monkey.util.OptionPane;
import jfx.incubator.scene.control.richtext.CodeArea;
import jfx.incubator.scene.control.richtext.LineNumberDecorator;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.SideDecorator;
import jfx.incubator.scene.control.richtext.SyntaxDecorator;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.CodeTextModel;
import jfx.incubator.scene.control.richtext.model.RichParagraph;
import jfx.incubator.scene.control.richtext.model.RichTextModel;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;
import jfx.incubator.scene.control.richtext.model.StyledTextModel;

/**
 * RichTextArea/CodeArea property sheet.
 */
public class RTAPropertySheet {
    public static void appendTo(OptionPane op, RichTextArea r) {
        CodeArea c = (r instanceof CodeArea ca) ? ca : null;
        if (c != null) {
            SimpleObjectProperty<SyntaxDecorator> syntaxDecorator = new SimpleObjectProperty<>();
            syntaxDecorator.addListener((_,_,v) -> {
                c.setSyntaxDecorator(v);
            });

            op.section("CodeArea");
            op.option("Font:", new FontOption("font", false, c.fontProperty()));
            op.option(new BooleanOption("lineNumbers", "line numbers enabled", c.lineNumbersEnabledProperty()));
            op.option("Line Spacing:", Options.lineSpacing("lineSpacing", c.lineSpacingProperty()));
            op.option("Model:", createCodeModelOption("model", c.modelProperty()));
            // TODO op.option("Prompt Text:", Options.promptText("promptText", true, c.promptTextProperty()));
            op.option("Syntax Decorator:", createSyntaxDecoratorOption("syntaxDecorator", syntaxDecorator));
            op.option("Tab Size:", Options.tabSize("tabSize", c.tabSizeProperty()));
            // TODO op.option("Text:", Options.textOption("textSelector", true, true, c.textProperty()));
        }
        op.section("RichTextArea");
        op.option("Caret Blink Period:", new DurationOption("caretBlinkPeriod", r.caretBlinkPeriodProperty()));
        op.option("Content Padding:", new InsetsOption("contentPadding", false, r.contentPaddingProperty()));
        op.option(new BooleanOption("displayCaret", "display caret", r.displayCaretProperty()));
        op.option(new BooleanOption("editable", "editable", r.editableProperty()));
        op.option(new BooleanOption("highlightCurrentParagraph", "highlight current paragraph", r.highlightCurrentParagraphProperty()));
        op.option("Left Decorator:", createDecoratorOption("leftDecorator", r.leftDecoratorProperty()));
        if (c == null) {
            op.option("Model:", createModelOption("model", r.modelProperty()));
        }
        op.option("Right Decorator:", createDecoratorOption("rightDecorator", r.rightDecoratorProperty()));
        op.option(new BooleanOption("useContentHeight", "use content height", r.useContentHeightProperty()));
        op.option(new BooleanOption("useContentWidth", "use content width", r.useContentWidthProperty()));
        op.option(new BooleanOption("wrapText", "wrap text", r.wrapTextProperty()));

        ControlPropertySheet.appendTo(op, r);
    }

    private static ObjectOption<SideDecorator> createDecoratorOption(String name, ObjectProperty<SideDecorator> p) {
        ObjectOption<SideDecorator> op = new ObjectOption<>(name, p);
        op.addChoice("<null>", null);
        op.addChoiceSupplier("Color", ColorSideDecorator::new);
        op.addChoiceSupplier("Line Numbers", LineNumberDecorator::new);
        op.selectInitialValue();
        return op;
    }

    private static ObjectOption<SyntaxDecorator> createSyntaxDecoratorOption(String name, ObjectProperty<SyntaxDecorator> p) {
        ObjectOption<SyntaxDecorator> op = new ObjectOption<>(name, p) {
            private final ObjectProperty<SyntaxDecorator> avoidGC = p;
        };
        op.addChoice("<null>", null);
        op.addChoiceSupplier("Numbers + Keywords", DemoSyntaxDecorator::new);
        op.selectInitialValue();
        return op;
    }

    private static ObjectOption<StyledTextModel> createCodeModelOption(String name, ObjectProperty<StyledTextModel> p) {
        var initial = p.get();
        ObjectOption<StyledTextModel> op = new ObjectOption<>(name, p);
        op.addChoice("<null>", null);
        op.addChoiceSupplier("CodeModel", CodeTextModel::new);
        // TODO large, all attributes
        if (initial != null) {
            op.addChoice("<initial>", initial);
        }
        op.selectInitialValue();
        return op;
    }

    private static ObjectOption<StyledTextModel> createModelOption(String name, ObjectProperty<StyledTextModel> p) {
        var initial = p.get();
        ObjectOption<StyledTextModel> op = new ObjectOption<>(name, p);
        op.addChoice("<null>", null);
        op.addChoiceSupplier("RichTextModel", RichTextModel::new);
        // TODO large, all attributes
        if (initial != null) {
            op.addChoice("<initial>", initial);
        }
        op.selectInitialValue();
        return op;
    }

    // colorful side decorator
    public static class ColorSideDecorator implements SideDecorator {

        @Override
        public double getPrefWidth(double viewWidth) {
            return 20.0;
        }

        @Override
        public Node getNode(int index) {
            int num = 36;
            double a = 360.0 * (index % num) / num;
            Color c = Color.hsb(a, 0.5, 1.0);

            Region r = new Region();
            r.setOpacity(1.0);
            r.setBackground(new Background(new BackgroundFill(c, null, null)));
            return r;
        }

        @Override
        public Node getMeasurementNode(int index) {
            return null;
        }
    }
    
    /** Simple {@code SyntaxDecorator} which emphasizes digits and keywords. */
    private static class DemoSyntaxDecorator implements SyntaxDecorator {
        private static final StyleAttributeMap DIGITS = StyleAttributeMap.builder().setTextColor(Color.MAGENTA).build();
        private static final StyleAttributeMap KEYWORDS = StyleAttributeMap.builder().setTextColor(Color.GREEN).build();
        private static Pattern PATTERN = initPattern();

        public DemoSyntaxDecorator() {
        }

        @Override
        public String toString() {
            return "DemoSyntaxDecorator";
        }

        @Override
        public RichParagraph createRichParagraph(CodeTextModel model, int index) {
            String text = model.getPlainText(index);
            RichParagraph.Builder b = RichParagraph.builder();
            int len = text.length();
            if (len > 0) {
                Matcher m = PATTERN.matcher(text);
                int beg = 0;
                while (m.find(beg)) {
                    int start = m.start();
                    if (start > beg) {
                        b.addSegment(text, beg, start, null);
                    }
                    int end = m.end();
                    boolean digit = (m.end(1) >= 0);
                    b.addSegment(text, start, end, digit ? DIGITS : KEYWORDS);
                    beg = end;
                }
                if (beg < len) {
                    b.addSegment(text, beg, len, null);
                }
            }
            return b.build();
        }

        private static Pattern initPattern() {
            String[] keywords = {
                "abstract",
                "assert",
                "boolean",
                "break",
                "byte",
                "case",
                "catch",
                "char",
                "class",
                "const",
                "continue",
                "default",
                "do",
                "double",
                "else",
                "enum",
                "extends",
                "final",
                "finally",
                "float",
                "for",
                "goto",
                "if",
                "implements",
                "import",
                "instanceof",
                "int",
                "interface",
                "long",
                "native",
                "new",
                "package",
                "private",
                "protected",
                "public",
                "return",
                "short",
                "static",
                "strictfpv",
                "super",
                "switch",
                "synchronized",
                "this",
                "throw",
                "throws",
                "transient",
                "try",
                "void",
                "volatile",
                "while"
            };

            StringBuilder sb = new StringBuilder();
            // digits
            sb.append("(\\b\\d+\\b)");

            // keywords
            for (String k : keywords) {
                sb.append("|\\b(");
                sb.append(k);
                sb.append(")\\b");
            }
            return Pattern.compile(sb.toString());
        }

        @Override
        public void handleChange(CodeTextModel m, TextPos start, TextPos end, int charsTop, int linesAdded, int charsBottom) {
            // no-op
        }
    }
}
