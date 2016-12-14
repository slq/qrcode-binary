package com.slq.qrcode.generator;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class QRCodeGenerator {

    // batch processing

    private static Logger log = LoggerFactory.getLogger(QRCodeGenerator.class);

    static final int MAX_LENGTH = 2500;
    static final int SLEEP_TIME = 3;
    static private JLabel picLabel = new JLabel();
    static private JFrame frame = new JFrame();

    public static void main(String[] args) throws IOException, InterruptedException {
        String INPUT_FILE_PATH = "~/ABC.pdf";

        frame = new JFrame("QRCode");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        picLabel = new JLabel();
        panel.add(picLabel);
        frame.setSize(500, 500);
        frame.setContentPane(panel);
        frame.setVisible(true);

        new QRCodeGenerator().encode(INPUT_FILE_PATH);
    }

    private void encode(String inputFile) throws IOException, InterruptedException {
        String fileName = FilenameUtils.getName(inputFile);
        String content = "";
        if (isBinary(inputFile)) {
            content = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(inputFile)));
        } else {
            content = new String(Files.readAllBytes(Paths.get(inputFile)));
        }
        encodeString(content, fileName);
    }

    private void encodeString(String content, String outputFilename) throws IOException, InterruptedException {
        int length = content.length();
        int max = length / MAX_LENGTH + 1;
        int start = 0;
        int end = Math.min(length, MAX_LENGTH);
        int i = 1;

        while (start < end && end <= length) {
            String messageToEncode = String.format("%d|%d|%s|%s", i, max, outputFilename, content.substring(start, end));
            log.info("{}/{} - {}", i, max, messageToEncode);

            PipedInputStream inS = new PipedInputStream();
            PipedOutputStream out = new PipedOutputStream(inS);
            new Thread(() -> QRCode.from(messageToEncode).to(ImageType.PNG)
                            .withSize(400, 400)
                            .withCharset("UTF-8")
                            .withErrorCorrection(ErrorCorrectionLevel.L)
                            .writeTo(out)
            ).start();

            picLabel.setIcon(new ImageIcon(ImageIO.read(inS)));

            frame.repaint();

            TimeUnit.SECONDS.sleep(SLEEP_TIME);

            i++;
            start = end;
            end = Math.min(length, end + MAX_LENGTH);
        }
    }

    private boolean isBinary(String inputFilePath) throws IOException {
        String string = new String(Files.readAllBytes(Paths.get(inputFilePath)));

        int alphanumeric = 0;
        int nonalphanumeric = 0;

        for (char c : string.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                alphanumeric++;
            } else {
                nonalphanumeric++;
            }
        }

        boolean isBinary = nonalphanumeric > alphanumeric;
        if (isBinary) {
            log.info("Binary file detected {}", inputFilePath);
        } else {
            log.info("Text file detected {}", inputFilePath);
        }

        return isBinary;
    }
}
