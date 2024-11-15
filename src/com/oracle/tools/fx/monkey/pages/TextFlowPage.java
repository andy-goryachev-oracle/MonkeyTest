/*
 * Copyright (c) 2023, 2024, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.tools.fx.monkey.pages;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import com.oracle.tools.fx.monkey.Loggers;
import com.oracle.tools.fx.monkey.options.ActionSelector;
import com.oracle.tools.fx.monkey.options.BooleanOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.options.FontOption;
import com.oracle.tools.fx.monkey.sheets.Options;
import com.oracle.tools.fx.monkey.sheets.RegionPropertySheet;
import com.oracle.tools.fx.monkey.tools.AccessibilityPropertyViewer;
import com.oracle.tools.fx.monkey.util.EnterTextDialog;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.LayoutInfoVisualizer;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.ShowCaretPaths;
import com.oracle.tools.fx.monkey.util.ShowCharacterRuns;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.TextTemplates;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 * TextFlow Page.
 */
public class TextFlowPage extends TestPaneBase {
    private final ActionSelector contentOption;
    private final FontOption fontOption;
    private final Label pickResult;
    private final Label hitInfo;
    private final Label hitInfo2;
    private final TextFlow textFlow;
    private final BorderPane container;
    private final LayoutInfoVisualizer visualizer;

    public TextFlowPage() {
        super("TextFlowPage");

        textFlow = new TextFlow() {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute a, Object... ps) {
                Object v = super.queryAccessibleAttribute(a, ps);
                Loggers.accessibility.log(a, v);
                return v;
            }
        };
        textFlow.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
        FX.setPopupMenu(textFlow, this::createPopupMenu);

        pickResult = new Label();

        hitInfo = new Label();

        hitInfo2 = new Label();

        visualizer = new LayoutInfoVisualizer();

        contentOption = new ActionSelector("content");
        contentOption.addButton("Edit", () -> {
            new EnterTextDialog(this, getText(), (s) -> {
                setContent(s);
            }).show();
        });
        Utils.fromPairs(TextTemplates.multiLineTextPairs(), (k,v) -> contentOption.addChoice(k, () -> setContent(v)));
        contentOption.addChoice("Inline Nodes", () -> setContent(mkInlineNodes()));
        contentOption.addChoice("Rich Text", () -> setContent(createRichText()));
        contentOption.addChoice("Rich Text (Complex)", () -> setContent(createRichTextComplex()));
        contentOption.addChoice("Accadian", () -> setContent(TextTemplates.AKKADIAN));

        fontOption = new FontOption("font", false, null);
        fontOption.getProperty().addListener((s,p,v) -> {
            Runnable r = contentOption.getValue();
            if (r != null) {
                r.run();
            }
        });

        OptionPane op = new OptionPane();
        op.section("TextFlow");
        op.option("Content:", contentOption);
        op.option("Font:", fontOption);
        op.option("Line Spacing:", Options.lineSpacing("lineSpacing", textFlow.lineSpacingProperty()));
        op.option("Tab Size:", Options.tabSize("tabSize", textFlow.tabSizeProperty()));
        op.option("Text Alignment:", new EnumOption<>("textAlignment", TextAlignment.class, textFlow.textAlignmentProperty()));
        op.separator();
        op.option(new BooleanOption("showCaretAndRange", visualizer.caretOptionText(), visualizer.showCaretAndRange));
//        op.option(new BooleanOption("useLegacyAPI", "(use TextFlow API)", visualizer.legacyAPI));
//        op.option(new BooleanOption("showLines", "show text lines", visualizer.showLines));
//        op.option(new BooleanOption("showBounds", "show layout bounds", visualizer.showLayoutBounds));
//        op.option(new BooleanOption("includeLineSpacing", "include lineSpacing ", visualizer.includeLineSpace));
        op.separator();
        op.option("Pick Result:", pickResult);
        op.option("Text.hitTest:", hitInfo2);
        op.option("TextFlow.hitTest:", hitInfo);

        RegionPropertySheet.appendTo(op, textFlow);

        container = new BorderPane(textFlow);

        setContent(container);
        setOptions(op);

        fontOption.selectSystemFont();
        visualizer.attach(container, textFlow);
    }

    private void setContent(String text) {
        Font f = getFont();
        textFlow.getChildren().setAll(t(text, f));
    }

    private void setContent(Node[] content) {
        textFlow.getChildren().setAll(content);
    }

    private Font getFont() {
        return fontOption.getFont();
    }

    private Node[] mkInlineNodes() {
        Font f = getFont();
        return new Node[] {
            t("Inline Nodes:", f),
            new Button("Left"),
            t(" ", f),
            new Button("Right"),
            t("trailing", f)
        };
    }

    private Node[] createRichText() {
        Font f = getFont();
        return new Node[] {
            t("Rich Text: ", f),
            t("BOLD ", f, "-fx-font-weight:bold;"),
            t("BOLD ", f, "-fx-font-weight:bold;"),
            t("BOLD ", f, "-fx-font-weight:bold;"),
            t("italic ", f, "-fx-font-style:italic;"),
            t("underline ", f, "-fx-underline:true;"),
            t("The quick brown fox jumped over the lazy dog ", f),
            t("The quick brown fox jumped over the lazy dog ", f),
            t("The quick brown fox jumped over the lazy dog ", f),
            t(TextTemplates.RIGHT_TO_LEFT, f),
            t(TextTemplates.RIGHT_TO_LEFT, f)
        };
    }

    private Node[] createRichTextComplex() {
        Font f = getFont();
        return new Node[] {
            t("Rich Text: ", f),
            t("BOLD ", f, "-fx-font-weight:bold;"),
            t("BOLD ", f, "-fx-font-weight:100; -fx-scale-x:200%;"),
            t("BOLD ", f, "-fx-font-weight:900;"),
            t("italic ", f, "-fx-font-style:italic;"),
            t("underline ", f, "-fx-underline:true;"),
            t(TextTemplates.TWO_EMOJIS, f),
            t(TextTemplates.CLUSTERS, f)
        };
    }

    private static Text t(String text, Font f) {
        Text t = new Text(text);
        t.setFont(f);
        return t;
    }

    private static Text t(String text, Font f, String style) {
        Text t = new Text(text);
        t.setFont(f);
        t.setStyle(style);
        return t;
    }

    private void handleMouseEvent(MouseEvent ev) {
        PickResult pick = ev.getPickResult();
        Node n = pick.getIntersectedNode();
        hitInfo2.setText(null);
        if (n == null) {
            pickResult.setText("null");
        } else {
            pickResult.setText(n.getClass().getSimpleName() + "." + n.hashCode());
            if (n instanceof Text t) {
                Point3D p3 = pick.getIntersectedPoint();
                Point2D p = new Point2D(p3.getX(), p3.getY());
                HitInfo h = t.hitTest(p);
                hitInfo2.setText(String.valueOf(h));
            }
        }

        Point2D p = new Point2D(ev.getX(), ev.getY());
        HitInfo h = textFlow.hitTest(p);
        hitInfo.setText(String.valueOf(h));
    }

    private String getText() {
        StringBuilder sb = new StringBuilder();
        for (Node n : textFlow.getChildrenUnmodifiable()) {
            if (n instanceof Text t) {
                sb.append(t.getText());
            } else {
                // inline node is treated as a single character
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    private void updateShowCaretPaths(boolean on) {
        if (on) {
            ShowCaretPaths.createFor(textFlow);
        } else {
            ShowCaretPaths.remove(textFlow);
        }
    }

    private void updateShowCharacters(boolean on) {
        if (on) {
            ShowCharacterRuns.createFor(textFlow);
        } else {
            ShowCharacterRuns.remove(textFlow);
        }
    }

    private ContextMenu createPopupMenu(PickResult pick) {
        ContextMenu m = new ContextMenu();
        FX.item(m, "Accessibility Attributes", () -> AccessibilityPropertyViewer.open(pick));
        return m;
    }
}
