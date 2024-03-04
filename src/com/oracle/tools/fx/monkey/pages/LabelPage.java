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

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import com.oracle.tools.fx.monkey.options.ControlOptions;
import com.oracle.tools.fx.monkey.util.EnterTextDialog;
import com.oracle.tools.fx.monkey.util.EnumSelector;
import com.oracle.tools.fx.monkey.util.FX;
import com.oracle.tools.fx.monkey.util.FontSelector;
import com.oracle.tools.fx.monkey.util.OptionPane;
import com.oracle.tools.fx.monkey.util.Templates;
import com.oracle.tools.fx.monkey.util.TestPaneBase;
import com.oracle.tools.fx.monkey.util.TextOption;
import com.oracle.tools.fx.monkey.util.TextSelector;

/**
 * Label Page
 */
public class LabelPage extends TestPaneBase {
    enum Demo {
        TEXT_ONLY("text only"),
        TEXT_GRAPHIC_LEFT("text + graphic left"),
        TEXT_GRAPHIC_RIGHT("text + graphic right"),
        TEXT_GRAPHIC_TOP("text + graphic top"),
        TEXT_GRAPHIC_BOTTOM("text + graphic bottom"),
        TEXT_GRAPHIC_TEXT_ONLY("text + graphic (text only)"),
        TEXT_GRAPHIC_GRAPHIC_ONLY("text + graphic (graphic only)"),
        GRAPHIC("graphic"),
        ;
        private final String text;
        Demo(String text) { this.text = text; }
        @Override public String toString() { return text; }
    }

    private final Label control;

    public LabelPage() {
        FX.name(this, "LabelPage");

        control = new Label();
        setContent(control);

        control.truncatedProperty().addListener((s,p,c) -> {
            System.err.println("truncated: " + c);
        });
        
        TextSelector textSelector = TextSelector.fromPairs(
            "textSelector",
            (v) -> control.setText(v),
            Templates.multiLineTextPairs()
        );

        Button editButton = new Button("Enter Text");
        editButton.setOnAction((ev) -> {
            String text = control.getText();
            new EnterTextDialog(this, text, (v) -> {
                control.setText(v);
            }).show();
        });

        // TODO default value?
        EnumSelector<Pos> alignment = new EnumSelector<Pos>(Pos.class, "alignment", (v) -> {
            control.setAlignment(v);
        });

        TextOption ellipsisString = new TextOption(
            "ellipsisString",
            control.ellipsisStringProperty()
        );
        
        EnumSelector<OverrunStyle> overrun = new EnumSelector<OverrunStyle>(OverrunStyle.class, "overrun", (v) -> {
            control.setTextOverrun(v);
        });
        
        FontSelector fontSelector = new FontSelector("font", (v) -> {
            control.setFont(v);
        });

        CheckBox wrapText = new CheckBox("wrap text");
        FX.name(wrapText, "wrap");
        wrapText.selectedProperty().bindBidirectional(control.wrapTextProperty());

        OptionPane op = new OptionPane();
        op.section("Label");
        op.label("Text:");
        op.option(textSelector.node());
        op.option(editButton);
        op.label("Alignment:");
        op.option(alignment.node());
        op.label("Graphic:");
        // TODO
        // TODO content display
        op.label("Ellipsis String:");
        op.option(ellipsisString);
        op.label("Font:");
        op.option(fontSelector.fontNode());
        op.label("Font Size:");
        op.option(fontSelector.sizeNode());
        // TODO graphic
        // TODO padding
        // TODO line spacing
        // TODO text fill
        // TODO text overrun
        op.label("Overrun:");
        op.option(overrun.node());
        // TODO mnemonic parsing
        // TODO text alignment
        // TODO underline
        op.option(wrapText);

        // control
        ControlOptions.appendTo(control, op);
        setOptions(op);
    }

    protected Label create(Demo d) {
        if(d == null) {
            return new Label();
        }

        // FIX
        Image im = createImage();

        switch(d) {
        case TEXT_GRAPHIC_LEFT:
            {
                Label t = new Label("text + graphic left");
                t.setGraphic(new ImageView(im));
                t.setContentDisplay(ContentDisplay.LEFT);
                return t;
            }
        case TEXT_GRAPHIC_RIGHT:
            {
                Label t = new Label("text + graphic right");
                t.setGraphic(new ImageView(im));
                t.setContentDisplay(ContentDisplay.RIGHT);
                return t;
            }
        case TEXT_ONLY:
            {
                return new Label("text only");
            }
        case TEXT_GRAPHIC_TOP:
            {
                Label t = new Label("text + graphic top");
                t.setGraphic(new ImageView(im));
                t.setContentDisplay(ContentDisplay.TOP);
                return t;
            }
        case TEXT_GRAPHIC_BOTTOM:
            {
                Label t = new Label("text + graphic bottom");
                t.setGraphic(new ImageView(im));
                t.setContentDisplay(ContentDisplay.BOTTOM);
                return t;
            }
        case TEXT_GRAPHIC_TEXT_ONLY:
            {
                Label t = new Label("text + graphic text only");
                t.setGraphic(new ImageView(im));
                t.setContentDisplay(ContentDisplay.TEXT_ONLY);
                return t;
            }
        case TEXT_GRAPHIC_GRAPHIC_ONLY:
            {
                Label t = new Label("text + graphic (graphic only)");
                t.setGraphic(new ImageView(im));
                t.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                return t;
            }
        case GRAPHIC:
            {
                Label t = new Label();
                t.setGraphic(new ImageView(im));
                return t;
            }
        default:
            return new Label("??" + d);
        }
    }

    private static Image createImage() {
        int w = 24;
        int h = 16;
        Color c = Color.GREEN;

        WritableImage im = new WritableImage(w, h);
        PixelWriter wr = im.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                wr.setColor(x, y, c);
            }
        }

        return im;
    }
}
