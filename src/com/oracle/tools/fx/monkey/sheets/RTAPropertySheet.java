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
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.DurationOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.options.FontOption;
import com.oracle.tools.fx.monkey.options.InsetsOption;
import com.oracle.tools.fx.monkey.options.ObjectOption;
import com.oracle.tools.fx.monkey.util.ContextMenuOptions;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.OptionPane;
import jfx.incubator.scene.control.richtext.CodeArea;
import jfx.incubator.scene.control.richtext.LineEnding;
import jfx.incubator.scene.control.richtext.LineNumberDecorator;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.SideDecorator;
import jfx.incubator.scene.control.richtext.SyntaxDecorator;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.CodeTextModel;
import jfx.incubator.scene.control.richtext.model.RichParagraph;
import jfx.incubator.scene.control.richtext.model.RichTextFormatHandler;
import jfx.incubator.scene.control.richtext.model.RichTextModel;
import jfx.incubator.scene.control.richtext.model.SimpleViewOnlyStyledModel;
import jfx.incubator.scene.control.richtext.model.StyleAttribute;
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
        op.option("Line Ending:", Options.ofEnum("lineEnding", true, LineEnding.class, LineEnding.system(), (le) -> {
            r.setLineEnding(le);
        }));
        if (c == null) {
            op.option("Model:", createModelOption("model", r.modelProperty()));
        }
        op.option("Right Decorator:", createDecoratorOption("rightDecorator", r.rightDecoratorProperty()));
        op.option(new BooleanOption("useContentHeight", "use content height", r.useContentHeightProperty()));
        op.option(new BooleanOption("useContentWidth", "use content width", r.useContentWidthProperty()));
        op.option(new BooleanOption("wrapText", "wrap text", r.wrapTextProperty()));
        ControlPropertySheet.appendTo(op, r, contextMenuOptions("contextMenu", r));
    }

    private static ContextMenuOptions contextMenuOptions(String name, RichTextArea r) {
        ContextMenuOptions c = new ContextMenuOptions(name, r);
        if (!(r instanceof CodeArea)) {
            c.addChoice("RichTextArea", createContextMenu(r));
        }
        return c;
    }

    private static ContextMenu createContextMenu(RichTextArea r) {
        ContextMenu m = new ContextMenu();
        FX.item(m, "Undo", r::undo);
        FX.item(m, "Redo", r::redo);
        FX.separator(m);
        FX.item(m, "Cut", r::cut);
        FX.item(m, "Copy", r::copy);
        FX.item(m, "Paste", r::paste);
        FX.item(m, "Paste and Retain Style", r::pastePlainText);
        FX.separator(m);
        FX.item(m, "Select All", r::selectAll);
        FX.separator(m);
        FX.item(m, "Bold", () -> toggle(r, StyleAttributeMap.BOLD));
        FX.item(m, "Italic", () -> toggle(r, StyleAttributeMap.ITALIC));
        FX.item(m, "Strike Through", () -> toggle(r, StyleAttributeMap.STRIKE_THROUGH));
        FX.item(m, "Underline", () -> toggle(r, StyleAttributeMap.UNDERLINE));
        return m;
    }

    private static void toggle(RichTextArea control, StyleAttribute<Boolean> attr) {
        TextPos start = control.getAnchorPosition();
        TextPos end = control.getCaretPosition();
        if (start == null) {
            return;
        } else if (start.equals(end)) {
            // apply to the whole paragraph
            int ix = start.index();
            start = TextPos.ofLeading(ix, 0);
            end = control.getParagraphEnd(ix);
        }

        StyleAttributeMap a = control.getActiveStyleAttributeMap();
        boolean on = !a.getBoolean(attr);
        a = StyleAttributeMap.builder().set(attr, on).build();
        control.applyStyle(start, end, a);
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
        op.addChoiceSupplier("Read-Only Model", SampleModel::new);
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

    private static class SampleModel extends SimpleViewOnlyStyledModel {
        public SampleModel() {
            // Styles: see MainWindow.stylesheet()
            String BOLD = "bold";
            String CODE = "code";
            String GRAY = "gray";
            String GREEN = "green";
            String ITALIC = "italic";
            String LARGE = "large";
            String RED = "red";
            String STRIKETHROUGH = "strikethrough";
            String UNDERLINE = "underline";

            addWithInlineAndStyleNames("Read-Only Model", "-fx-font-size:200%;", UNDERLINE);
            nl(2);

            addWithStyleNames("/**", RED, CODE);
            nl();
            addWithStyleNames(" * Syntax Highlight Demo.", RED, CODE);
            nl();
            addWithStyleNames(" */", RED, CODE);
            nl();
            addWithStyleNames("public class ", GREEN, CODE);
            addWithStyleNames("SyntaxHighlightDemo ", CODE);
            addWithStyleNames("extends ", GREEN, CODE);
            addWithStyleNames("Application {", CODE);
            nl();
            addWithStyleNames("\tpublic static void", GREEN, CODE);
            addWithStyleNames(" main(String[] args) {", CODE);
            nl();
            addWithStyleNames("\t\tApplication.launch(SyntaxHighlightDemo.", CODE);
            addWithStyleNames("class", CODE, GREEN);
            addWithStyleNames(", args);", CODE);
            nl();
            addWithStyleNames("\t}", CODE);
            nl();
            addWithStyleNames("}", CODE);
            nl(2);
            // font attributes
            addWithStyleNames("BOLD ", BOLD);
            addWithStyleNames("ITALIC ", ITALIC);
            addWithStyleNames("STRIKETHROUGH ", STRIKETHROUGH);
            addWithStyleNames("UNDERLINE ", UNDERLINE);
            addWithStyleNames("ALL OF THEM ", BOLD, ITALIC, STRIKETHROUGH, UNDERLINE);
            nl(2);
            // inline nodes
            addSegment("Inline Nodes:  ");
            addNodeSegment(() -> {
                TextField f = new TextField();
                f.setPrefColumnCount(20);
                return f;
            });
            addSegment(" ");
            addNodeSegment(() -> new Button("OK"));
            addSegment(" ");
            nl(2);
            addWithInlineStyle("ABCDEFGHIJKLMNO", "-fx-font-family:monospaced;").nl();
            addWithStyleNames("        leading and trailing whitespace         ", CODE).nl();
            nl(2);
            addWithStyleNames("Various highlights, some overlapping.", LARGE);
            highlight(8, 10, Color.rgb(255, 255, 128, 0.7));
            highlight(12, 12, Color.rgb(0, 0, 128, 0.1));
            addWavyUnderline(25, 100, Color.RED);
            nl(2);
            addSegment("Styled with CSS");
            addWavyUnderline(0, 6, "squiggly-css");
            highlight(12, 3, "highlight1", "highlight2");
            nl(2);
            addSegment("Paragraph Node:");
            addParagraph(JumpingLabel::new);
            nl(2);
            addSegment("Trailing node: ");
            addNodeSegment(JumpingLabel::new);

            // rich text data handler
            registerDataFormatHandler(RichTextFormatHandler.getInstance(), true, false, 2000);
        }
    }

    static class JumpingLabel extends Label {
        public JumpingLabel() {
            String text = "(click me)";
            setText(text);
            setBackground(Background.fill(new Color(1.0, 0.627451, 0.47843137, 0.5)));
            setOnMouseClicked((_) -> {
                if (text.equals(getText())) {
                    setMinWidth(200);
                    setMinHeight(100);
                    setText("(click me again)");
                } else {
                    setMinWidth(Label.USE_PREF_SIZE);
                    setText(text);
                    setMinHeight(Label.USE_PREF_SIZE);
                }
            });
        }
    }
}
