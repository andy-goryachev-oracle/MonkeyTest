/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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

package com.oracle.tools.fx.monkey.tools;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;
import java.util.HexFormat;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.oracle.tools.fx.monkey.util.FX;

/**
 * Image Decoder Window.
 */
public class ImageDecoderWindow extends Stage {
    private final TextArea textField;
    private final ImageView imageField;
    private final Label status;
    private final BorderPane imagePane;
    private final BorderPane resultPane;

    public ImageDecoderWindow() {
        FX.name(this, "ImageDecoderWindow");

        textField = new TextArea();
        textField.setTooltip(new Tooltip("Paste Base64-encoded image data here."));
        textField.setWrapText(true);

        imageField = new ImageView();
        imageField.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);

        status = new Label();
        status.setPadding(new Insets(2, 10, 2, 10));

        ScrollPane scroll = new ScrollPane(imageField);
        scroll.setBackground(Background.fill(Color.DARKGRAY));

        imagePane = new BorderPane(scroll);
        imagePane.setBottom(status);

        resultPane = new BorderPane();

        TabPane tp = new TabPane();
        tp.getTabs().setAll(
            new Tab("Base64", textField),
            new Tab("Image", resultPane)
        );
        tp.getSelectionModel().selectedIndexProperty().addListener((s,p,tab) -> {
            handleSelection(tab);
        });

        setTitle("Image Decoder");
        setScene(new Scene(tp));
        setWidth(1200);
        setHeight(1000);
    }

    private void handleSelection(Number ix) {
        if ((ix != null) && (ix.intValue() == 1)) {
            decode();
        }
    }

    private void handleMouseEvent(MouseEvent ev) {
        int x = (int)ev.getX();
        int y = (int)ev.getY();
        String s = getInfo(x, y);
        status.setText(s);
    }

    private String getInfo(int x, int y) {
        Image im = imageField.getImage();
        if(im != null) {
            if((x > 0) && (x < (int)im.getWidth())) {
                if((y > 0) && (y < (int)im.getHeight())) {
                    int argb = imageField.getImage().getPixelReader().getArgb(x, y);
                    return 
                        "x=" + x +
                        ", y=" + y +
                        ", red=" + ((argb >> 16) & 0xff) +
                        ", green=" + ((argb >> 8) & 0xff) +
                        ", blue=" + (argb & 0xff) +
                        " (argb=" + HexFormat.of().toHexDigits(argb) + ")";
                }
            }
        }
        return null;
    }

    private void decode() {
        String err;
        try {
            String text = textField.getText().trim();
            byte[] b = Base64.getDecoder().decode(text);
            if ((b == null) || (b.length == 0)) {
                err = "No valid image data.";
            } else {
                Image im = new Image(new ByteArrayInputStream(b));
                imageField.setImage(im);
                resultPane.setCenter(imagePane);
                return;
            }
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter wr = new PrintWriter(sw);
            e.printStackTrace(wr);
            err = sw.toString();
        }
        TextArea t = new TextArea(err);
        t.setFont(new Font("monospaced", 12));
        resultPane.setCenter(t);
    }
}
