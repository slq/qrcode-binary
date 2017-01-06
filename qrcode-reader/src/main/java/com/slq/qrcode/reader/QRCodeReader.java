package com.slq.qrcode.reader;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.slq.qrcode.utils.zxing.BufferedImageLuminanceSource;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;

public final class QRCodeReader {

    public static final int SLEEP_TIME = 1;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        Item item = new Item();

        boolean isDone = false;
        while (!isDone) {
            isDone = decodeScreen(item);
            TimeUnit.SECONDS.sleep(SLEEP_TIME);
        }

        item.persist();
    }

    private static boolean decodeScreen(Item item) throws AWTException, IOException, InterruptedException {
        QRCodeReader runner = new QRCodeReader();

        Point startingPoint = new WhiteboxLocation().locateWhitebox();
        if (startingPoint == null) {
            return false;
        }

        Dimension dimension = new Dimension(400, 400);
        Rectangle rectangle = new Rectangle(startingPoint, dimension);
        BufferedImage image = new Robot().createScreenCapture(rectangle);

        String text = runner.getDecodeText(image);

        String pointMsg = String.format("x=%.0f, y=%.0f", startingPoint.getX(), startingPoint.getY());

        if (text.length() > 150) {
            System.out.printf("%s|%s ... %s%n", pointMsg, text.substring(0, 100), text.substring(text.length() - 40));
        } else {
            System.out.printf("%s| short! %s%n", pointMsg, text);
        }

        if (Objects.equals(text, "")) {
            return false;
        }

        return item.updateIfNeeded(text);
    }

    private String getDecodeText(BufferedImage image) {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        try {
            MultiFormatReader reader = new MultiFormatReader();
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.QR_CODE));
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
            result = reader.decode(bitmap, hints);
        } catch (ReaderException re) {
            return "";
        }
        return String.valueOf(result.getText());
    }

    private static class Item {
        Map<String, String> map = new TreeMap<>(Comparator.comparingInt((ToIntFunction<String>) Integer::parseInt));
        private String outputFilename = "";


        public void updateOutputFilenameIfNeeded(String outputFilename) {
            if (outputFilename.length() > this.outputFilename.length()) {
                this.outputFilename = outputFilename;
            }
        }

        public void updateMapIfNeeded(String key, String value) {
            if (!map.containsKey(key)) {
                map.put(key, value);
            } else {
                String currentValue = map.get(key);
                if (value.length() > currentValue.length()) {
                    map.put(key, value);
                }
            }
        }

        public boolean updateIfNeeded(String text) {
            String key = text.split("\\|")[0];
            String max = text.split("\\|")[1];
            String outputFilename = text.split("\\|")[2];
            String value = text.split("\\|")[3];

            updateMapIfNeeded(key, value);
            updateOutputFilenameIfNeeded(outputFilename);

            return key.equals(max);
        }

        public void persist() throws IOException {
            new File(outputFilename).delete();
            for (String value : map.values()) {
                try (FileOutputStream output = new FileOutputStream(outputFilename, true)) {
                    output.write(Base64.getDecoder().decode(value));
                }
            }
        }
    }

}
