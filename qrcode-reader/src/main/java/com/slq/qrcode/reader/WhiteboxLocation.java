package com.slq.qrcode.reader;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// CHECKSTYLE:BooleanExpressionComplexity:OFF
public final class WhiteboxLocation {

    private static final int WIDTH = 399;
    private static final int HEIGHT = 399;

    public Point locateWhitebox() throws AWTException, IOException {
        Robot robot = new Robot();
        BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenshot, "PNG", new File("what-decoder-can-see.PNG"));

        Point point = locateWhitebox(screenshot);
        if (point == null) {
            System.out.println("Not found!");
        }

        return point;
    }

    private static Point locateWhitebox(BufferedImage screenshot) {
        for (int x = 0; x < screenshot.getWidth(); x++) {
            for (int y = 0; y < screenshot.getHeight(); y++) {
                if (isAllWhite(screenshot, x, y)) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    private static boolean isAllWhite(BufferedImage screenshot, int x, int y) {
        return isColor(screenshot, x, y, Color.WHITE)
                && isColor(screenshot, x + WIDTH, y, Color.WHITE)
                && isColor(screenshot, x, y + HEIGHT, Color.WHITE)
                && isColor(screenshot, x + WIDTH, y + HEIGHT, Color.WHITE)

                && isColor(screenshot, x, y - 1, Color.BLACK)
                && isColor(screenshot, x + 1 + WIDTH, y, Color.BLACK)
                && isColor(screenshot, x - 1, y + HEIGHT, Color.BLACK)
                && isColor(screenshot, x + WIDTH, y + 1 + HEIGHT, Color.BLACK);
    }

    private static boolean isColor(BufferedImage screenshot, int x, int y, Color color) {
        try {
            return screenshot.getRGB(x, y) == color.getRGB();
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        new WhiteboxLocation().locateWhitebox();
    }
}
// CHECKSTYLE:BooleanExpressionComplexity:ON
