package com.slq.qrcode.generator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.PipedInputStream;

import static java.awt.Color.BLACK;

public class QRCodeImageViewer extends JFrame {

    public static final int MARGIN_SIZE = 20;
    public static final int FRAME_SIZE = 500;
    private JLabel picLabel;

    public QRCodeImageViewer() throws HeadlessException {
        picLabel = new JLabel();

        JPanel panel = new JPanel();
        panel.setBackground(BLACK);
        panel.setBorder(new EmptyBorder(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE));
        panel.add(picLabel);

        setTitle("QRCode Image Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(FRAME_SIZE, FRAME_SIZE);
        setContentPane(panel);
        setVisible(true);
    }

    public void readImageFrom(PipedInputStream imageInputStream) throws IOException {
        picLabel.setIcon(new ImageIcon(ImageIO.read(imageInputStream)));
        repaint();
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}
