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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.text.Font;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import com.oracle.tools.fx.monkey.options.ActionSelector;
import com.oracle.tools.fx.monkey.options.DoubleOption;
import com.oracle.tools.fx.monkey.options.EnumOption;
import com.oracle.tools.fx.monkey.options.IntOption;
import com.oracle.tools.fx.monkey.sheets.RegionOptions;
import com.oracle.tools.fx.monkey.util.CheckBoxSelector;
import com.oracle.tools.fx.monkey.util.EnterTextDialog;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.FontSelector;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.ShowCaretPaths;
import com.oracle.tools.fx.monkey.util.ShowCharacterRuns;
import com.oracle.tools.fx.monkey.util.Templates;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 * TextFlow Page
 */
public class TextFlowPage extends TestPaneBase {
    private final ActionSelector contentOption;
    private final FontSelector fontSelector;
    private final CheckBoxSelector showChars;
    private final CheckBoxSelector showCaretPaths;
    private final Label pickResult;
    private final Label hitInfo;
    private final Label hitInfo2;
    private final TextFlow textFlow;

    public TextFlowPage() {
        FX.name(this, "TextFlowPage");

        textFlow = new TextFlow();
        textFlow.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);

        pickResult = new Label();

        hitInfo = new Label();

        hitInfo2 = new Label();

        contentOption = new ActionSelector("content");
        contentOption.addButton("Edit", this::openEditDialog);
        Utils.fromPairs(Templates.multiLineTextPairs(), (k,v) -> contentOption.addChoice(k, () -> setContent(v)));
        contentOption.addChoice("Inline Nodes", () -> setContent(mkInlineNodes()));
        contentOption.addChoice("Rich Text", () -> setContent(createRichText()));
        contentOption.addChoice("Rich Text (Complex)", () -> setContent(createRichTextComplex()));
        contentOption.addChoice("Accadian", () -> setContent(Templates.AKKADIAN));

        // FIX inline button, or use the FontOption with local or null property
        fontSelector = new FontSelector("font", (f) -> updateText());

        Button editButton = new Button("Enter Text");
        editButton.setOnAction((ev) -> {
            new EnterTextDialog(this, getText(), (s) -> {
                updateText();
            }).show();
        });

        showChars = new CheckBoxSelector("showChars", "show characters", (v) -> updateShowCharacters());

        showCaretPaths = new CheckBoxSelector("showCaretPaths", "show caret paths", (v) -> updateShowCaretPaths());

        OptionPane op = new OptionPane();
        op.section("TextFlow");
        
        op.option("Content:", contentOption);
        
        op.label("Font:");
        op.option(fontSelector.fontNode());
        op.label("Font Size:");
        op.option(fontSelector.sizeNode());
        
        op.option("Line Spacing:", DoubleOption.lineSpacing("lineSpacing", textFlow.lineSpacingProperty()));
        
        op.option("Tab Size:", IntOption.tabSize("tabSize", textFlow.tabSizeProperty()));

        op.option("Text Alignment:", new EnumOption<>("textAlignment", TextAlignment.class, textFlow.textAlignmentProperty()));
        //
        op.separator();
        op.option(showChars.node());
        op.option(showCaretPaths.node());
        op.separator();
        op.option("Pick Result:", pickResult);
        op.option("Text.hitTest:", hitInfo2);
        op.option("TextFlow.hitTest:", hitInfo);

        // region
        RegionOptions.appendTo(op, textFlow);

        setContent(textFlow);
        setOptions(op);

        fontSelector.selectSystemFont();
    }

    private void updateText() {
        Runnable r = contentOption.getValue();
        if (r != null) {
            r.run();
        }
    }

    private void setContent(String text) {
        Font f = getFont();
        textFlow.getChildren().setAll(t(text, f));
    }

    private void setContent(Node[] content) {
        textFlow.getChildren().setAll(content);
    }

    private Font getFont() {
        return fontSelector.getFont();
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
            t(Templates.RIGHT_TO_LEFT, f),
            t(Templates.RIGHT_TO_LEFT, f)
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
            t(Templates.TWO_EMOJIS, f),
            t(Templates.CLUSTERS, f)
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

    private void updateShowCaretPaths() {
        if (showCaretPaths.getValue()) {
            ShowCaretPaths.createFor(textFlow);
        } else {
            ShowCaretPaths.remove(textFlow);
        }
    }

    private void updateShowCharacters() {
        if (showChars.getValue()) {
            ShowCharacterRuns.createFor(textFlow);
        } else {
            ShowCharacterRuns.remove(textFlow);
        }
    }

    private void openEditDialog() {
        new EnterTextDialog(this, getText(), (s) -> {
            // TODO update text
        }).show();
    }
}
