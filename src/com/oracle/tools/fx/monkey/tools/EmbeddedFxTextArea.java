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
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    
    enum Ori {
        LTR,
        RTL,
        INHERIT
    }

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
        
        Ori[] choices = {
            Ori.INHERIT,
            Ori.LTR,
            Ori.RTL,
        };

        JComboBox<Ori> rtl = new JComboBox<Ori>(choices);
        rtl.addActionListener((ev) -> {
            Ori v = (Ori)rtl.getSelectedItem();
            ComponentOrientation ori = toComponentOrientation(v);
            frame.applyComponentOrientation(ori);
            frame.validate();
            frame.repaint();
        });

        JComboBox<Ori> rtl2 = new JComboBox<Ori>(choices);
        rtl2.addActionListener((ev) -> {
            Ori v = (Ori)rtl.getSelectedItem();
            NodeOrientation ori = toNodeOrientaiton(v);
            Platform.runLater(() -> {
                textArea.setNodeOrientation(ori);
            });
        });

        JPanel tb = new JPanel(new GridLayout(2, 2));
        tb.add(new JLabel("JFrame.componentOrientation:"));
        tb.add(rtl);
        tb.add(new JLabel("FX.nodeOrientation"));
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

    private static ComponentOrientation toComponentOrientation(Ori x) {
        if (x != null) {
            switch (x) {
            case LTR:
                return ComponentOrientation.LEFT_TO_RIGHT;
            case RTL:
                return ComponentOrientation.RIGHT_TO_LEFT;
            }
        }
        return ComponentOrientation.UNKNOWN;
    }

    private static NodeOrientation toNodeOrientaiton(Ori x) {
        if (x != null) {
            switch (x) {
            case LTR:
                return NodeOrientation.LEFT_TO_RIGHT;
            case RTL:
                return NodeOrientation.RIGHT_TO_LEFT;
            }
        }
        return NodeOrientation.INHERIT;
    }

    private static void initFX() {
        textArea = new TextArea("Arabic: العربية\nHebrew: עברית");
        textArea.setStyle("-fx-font-size:200%;");
        jfxPanel.setScene(new Scene(textArea));
    }
}
