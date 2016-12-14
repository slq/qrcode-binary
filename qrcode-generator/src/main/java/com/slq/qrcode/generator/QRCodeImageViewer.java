package com.slq.qrcode.generator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.PipedInputStream;

public class QRCodeImageViewer extends JFrame {

    private JLabel picLabel = new JLabel();

    public QRCodeImageViewer() throws HeadlessException {
        setTitle("QRCode");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        picLabel = new JLabel();
        panel.add(picLabel);
        setSize(500, 500);
        setContentPane(panel);
        setVisible(true);
    }

    public void readImageFrom(PipedInputStream imageInputStream) throws IOException {
        picLabel.setIcon(new ImageIcon(ImageIO.read(imageInputStream)));
        repaint();
    }
}
