package com.slq.qrcode.generator;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class QRCodeGenerator {

    // batch processing

    private static Logger log = LoggerFactory.getLogger(QRCodeGenerator.class);

    private static final int MAX_LENGTH = 2800;
    private static final int SLEEP_TIME = 5;
    private static QRCodeImageViewer frame = new QRCodeImageViewer();

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            log.info("Generating QRCodes for: {}", args[0]);
            new QRCodeGenerator().encode(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            frame.close();
        }
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
            log.info("{}/{} - {}...{}", i, max, messageToEncode.substring(0, 100), messageToEncode.substring(messageToEncode.length()-40));

            PipedInputStream imageInputStream = new PipedInputStream();
            PipedOutputStream imageOutputStream = new PipedOutputStream(imageInputStream);
            new Thread(() -> QRCode.from(messageToEncode)
                    .to(ImageType.PNG)
                    .withSize(400, 400)
                    .withCharset("UTF-8")
                    .withErrorCorrection(ErrorCorrectionLevel.L)
                    .writeTo(imageOutputStream)
            ).start();

            frame.readImageFrom(imageInputStream);

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

        boolean isBinary = (2 * nonalphanumeric) > alphanumeric;
        if (isBinary) {
            log.info("Binary file detected {}", inputFilePath);
        } else {
            log.info("Text file detected {}", inputFilePath);
        }

        return isBinary;
    }
}
