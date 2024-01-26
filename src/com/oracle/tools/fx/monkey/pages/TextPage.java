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
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import com.oracle.tools.fx.monkey.util.CheckBoxSelector;
import com.oracle.tools.fx.monkey.util.EnterTextDialog;
import com.oracle.tools.fx.monkey.util.EnumSelector;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.FontSelector;
import com.oracle.tools.fx.monkey.util.ItemSelector;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.ShowCharacterRuns;
import com.oracle.tools.fx.monkey.util.Templates;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.TextSelector;
import com.oracle.tools.fx.monkey.util.Utils;

/**
 * Text Page
 */
public class TextPage extends TestPaneBase {
    private final TextSelector textSelector;
    private final TextField styleField;
    private final FontSelector fontSelector;
    private final EnumSelector<FontSmoothingType> fontSmoothing;
    private final ItemSelector<Double> lineSpacing;
    private final ItemSelector<Integer> tabSize;
    private final EnumSelector<TextAlignment> textAlignment;
    private final EnumSelector<TextBoundsType> textBounds;
    private final CheckBoxSelector strikeThrough;
    private final CheckBoxSelector underline;
    private final CheckBoxSelector showChars;
    private final ScrollPane scroll;
    private final CheckBoxSelector wrap;
    private final Path caretPath;
    private final Label hitInfo;
    private Text control;
    private String currentText;

    public TextPage() {
        FX.name(this, "TextPage");

        hitInfo = new Label();

        styleField = new TextField();
        styleField.setOnAction((ev) -> {
            String s = styleField.getText();
            if (Utils.isBlank(s)) {
                s = null;
            }
            control.setStyle(s);
        });

        caretPath = new Path();
        caretPath.setStrokeWidth(1);
        caretPath.setStroke(Color.RED);
        caretPath.setManaged(false);

        textSelector = TextSelector.fromPairs(
            "textSelector",
            (t) -> updateText(),
            Templates.multiLineTextPairs()
        );

        fontSelector = new FontSelector("font", (f) -> updateControl());

        Button editButton = new Button("Enter Text");
        editButton.setOnAction((ev) -> {
            new EnterTextDialog(this, (s) -> {
                currentText = s;
                updateControl();
            }).show();
        });
        
        fontSmoothing = new EnumSelector<>(FontSmoothingType.class, "fontSmoothing", (v) -> updateControl());
        
        lineSpacing = new ItemSelector<Double>(
            "lineSpacing",
            (v) -> updateControl(),
            0.0,
            1.0,
            2.5,
            3.3333333,
            10.0,
            100.0
        );
        
        tabSize = new ItemSelector<Integer>(
            "tabSize",
            (v) -> updateControl(),
            0,
            1,
            2,
            3,
            4,
            8,
            16,
            32,
            64
        );
        
        textAlignment = new EnumSelector<>(TextAlignment.class, "textAlignment", (v) -> updateControl());
        
        textBounds = new EnumSelector<>(TextBoundsType.class, "textBounds", (v) -> updateControl());
        
        strikeThrough = new CheckBoxSelector("strikeThrough", "strike through", (v) -> updateControl());

        showChars = new CheckBoxSelector("showChars", "show characters", (v) -> updateControl());

        wrap = new CheckBoxSelector("wrap", "wrap width", (v) -> updateWrap(v));
        
        underline = new CheckBoxSelector("underline", "underline", (v) -> updateControl());

        OptionPane op = new OptionPane();
        op.label("Text:");
        op.option(textSelector.node());
        op.option(editButton);
        op.label("Font:");
        op.option(fontSelector.fontNode());
        op.label("Font Size:");
        op.option(fontSelector.sizeNode());
        op.label("Font Smoothing:");
        op.option(fontSmoothing.node());
        op.label("Line Spacing:");
        op.option(lineSpacing.node());
        // TODO selection fill
        op.option(strikeThrough.node());
        op.label("Tab Size:");
        op.option(tabSize.node());
        op.label("Text Alignment:");
        op.option(textAlignment.node());
        op.label("Text Bounds Type:");
        op.option(textBounds.node());
        // TODO textOrigin
        op.option(underline.node());
        op.option(wrap.node());
        op.option(showChars.node());
        op.label("Direct Style:");
        op.option(styleField);
        op.label("Text.hitTest:");
        op.option(hitInfo);
        op.label("Note: " + (FX.isMac() ? "⌘" : "ctrl") + "-click for caret shape");

        scroll = new ScrollPane();
        scroll.setBorder(Border.EMPTY);
        scroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scroll.setFitToWidth(false);

        setContent(scroll);
        setOptions(op);

        textSelector.selectFirst();
        fontSelector.selectSystemFont();
    }

    private void updateText() {
        currentText = textSelector.getSelectedText();
        updateControl();
    }

    private void updateControl() {
        control = new Text(currentText);
        control.setFont(fontSelector.getFont());
        control.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
        control.setFontSmoothingType(fontSmoothing.getValue());
        control.setLineSpacing(lineSpacing.getValue(0.0));
        control.setStrikethrough(strikeThrough.getValue());
        control.setUnderline(underline.getValue());
        control.setBoundsType(textBounds.getValue(TextBoundsType.LOGICAL));
        control.setTabSize(tabSize.getValue(8));
        control.setTextAlignment(textAlignment.getValue(TextAlignment.LEFT));

        Group group = new Group(control, caretPath);
        scroll.setContent(group);

        updateWrap(wrap.getValue());

        if (showChars.getValue()) {
            Group g = ShowCharacterRuns.createFor(control);
            group.getChildren().add(g);
        }
    }

    private void updateWrap(boolean on) {
        if (on) {
            control.wrappingWidthProperty().bind(scroll.viewportBoundsProperty().map((b) -> b.getWidth()));
        } else {
            control.wrappingWidthProperty().unbind();
            control.setWrappingWidth(0);
        }
    }

    private void showCaretShape(Point2D p) {
        HitInfo h = control.hitTest(p);
        System.out.println("hit=" + h);
        PathElement[] pe = control.caretShape(h.getCharIndex(), h.isLeading());
        caretPath.getElements().setAll(pe);
    }

    private void handleMouseEvent(MouseEvent ev) {
        Point2D p = new Point2D(ev.getX(), ev.getY());
        HitInfo h = control.hitTest(p);
        hitInfo.setText(String.valueOf(h));

        if (ev.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (ev.isShortcutDown()) {
                showCaretShape(new Point2D(ev.getX(), ev.getY()));
            }
        }
    }
}
