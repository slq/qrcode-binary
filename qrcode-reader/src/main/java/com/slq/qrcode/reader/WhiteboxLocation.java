package com.slq.qrcode.reader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class WhiteboxLocation {

    private static final int WIDTH = 399;
    private static final int HEIGHT = 399;

    public WhiteboxLocation() {

    }

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        new WhiteboxLocation().locateWhitebox();
    }

    public Point locateWhitebox() throws AWTException, IOException {
        Robot robot = new Robot();
        BufferedImage screenshot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenshot, "PNG", new File("what-decoder-can-see.PNG"));

        Point point = locateWhitebox(screenshot);
        if (point != null) {
//            String msg = String.format("x=%.0f, y=%.0f", point.getX(), point.getY());
//            System.out.println(msg);
        } else {
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
//            return isAllWhite(screenshot.getRGB(x, y, 10, 10, null, 0, 0));
        } catch (ArrayIndexOutOfBoundsException ex) {
//            System.out.println("Out of bounds! " + x+ " " + y);
            return false;
        }
    }
}
