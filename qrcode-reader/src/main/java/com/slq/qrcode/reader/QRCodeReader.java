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
import java.util.Base64;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntFunction;

public final class QRCodeReader {

    public static final int SLEEP_TIME = 1;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        Map<String, String> map = new TreeMap<>(Comparator.comparingInt((ToIntFunction<String>) Integer::parseInt));

        boolean isDone = false;
        while(!isDone){
            isDone = decodeScreen(map);
            TimeUnit.SECONDS.sleep(SLEEP_TIME);
        }
    }

    private static boolean decodeScreen(Map<String, String> map) throws AWTException, IOException, InterruptedException {
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
        System.out.printf("%s|%s%n", pointMsg, text);

        if (Objects.equals(text, "")) {
            return false;
        }

        String key = text.split("\\|")[0];
        String max = text.split("\\|")[1];
        String outputFilename = text.split("\\|")[2];
        String value = text.split("\\|")[3];

        if (!map.containsKey(key)) {
            map.put(key, value);
        }

        new File(outputFilename).delete();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try (FileOutputStream output = new FileOutputStream(outputFilename, true)) {
                output.write(Base64.getDecoder().decode(entry.getValue()));
            }
        }

        return key.equals(max);
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

}
