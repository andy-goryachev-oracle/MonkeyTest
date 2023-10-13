/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;

/**
 * https://bugs.openjdk.org/browse/JDK-8317836
 */
public class EmbeddedFxTextArea {
    private static JFXPanel jfxPanel;
    private static TextArea textArea;

    public static void start() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
        EventQueue.invokeLater(EmbeddedFxTextArea::initSwing);
    }

    private static void initSwing() {
        JFrame frame = new JFrame();

        jfxPanel = new JFXPanel();
        
        Platform.runLater(EmbeddedFxTextArea::initFX);

        JCheckBox rtl = new JCheckBox("RTL (JFrame.componentOrientation)");
        rtl.addActionListener((ev) -> {
            ComponentOrientation ori = rtl.isSelected() ? ComponentOrientation.RIGHT_TO_LEFT : ComponentOrientation.LEFT_TO_RIGHT;
            frame.applyComponentOrientation(ori);
            frame.validate();
            frame.repaint();
        });
        
        JCheckBox rtl2 = new JCheckBox("RTL (FX.nodeOrientation)");
        rtl2.addActionListener((ev) -> {
            NodeOrientation ori = rtl2.isSelected() ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;
            Platform.runLater(() -> {
                textArea.setNodeOrientation(ori);
            });
        });

        JToolBar tb = new JToolBar();
        tb.add(rtl);
        tb.add(rtl2);

        JPanel p = new JPanel(new BorderLayout());
        p.add(jfxPanel, BorderLayout.CENTER);
        p.add(tb, BorderLayout.NORTH);

        frame.setContentPane(p);
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setTitle("FX TextArea Embedded in JFXPanel");
        frame.setVisible(true);
    }

    private static void initFX() {
        textArea = new TextArea("Arabic: العربية\nHebrew: עברית");
        textArea.setStyle("-fx-font-size:200%;");
        jfxPanel.setScene(new Scene(textArea));
    }
}
