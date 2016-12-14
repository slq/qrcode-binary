package com.slq.qrcode

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import groovy.util.logging.Slf4j
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode
import org.apache.commons.io.FilenameUtils

import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder
import java.awt.Color
import java.awt.image.BufferedImage

import static javax.swing.JFrame.EXIT_ON_CLOSE

@Slf4j
class QRCodeGenerator {

    // batch processing

    static final int MAX_LENGTH = 2500
    static final int SLEEP_TIME = 3
    static private JLabel picLabel = new JLabel()
    static private JFrame frame = new JFrame()

    static void main(String[] args) {
        String INPUT_FILE_PATH = 'c:\\UBS\\Dev\\workspace\\JAlgoArena-light.7z'

        frame = new JFrame("QRCode")
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE)
        JPanel panel = new JPanel()
        panel.setBackground(Color.BLACK)
        BufferedImage myPicture = ImageIO.read(new File("c:\\UBS\\Dev\\workspace\\qrcode-binary\\images\\JAlgoArena-light.7z_80.png"));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        picLabel = new JLabel()
        panel.add(picLabel)
        frame.setSize(500, 500)
        frame.setContentPane(panel)
        frame.setVisible(true)

        new QRCodeGenerator().encode(INPUT_FILE_PATH, 'images')
    }

    private void encode(String inputFile, String output) {
        String fileName = FilenameUtils.getName(inputFile)
        String content = "";
        if (isBinary(inputFile)) {
            content = Base64.getEncoder().encodeToString(new File(inputFile).bytes)
        } else {
            content = new File(inputFile).getText('UTF-8')
        }
        encodeString(content, fileName, output)
    }

    private void encodeString(String content, String outputFilename, String outputDirectory) {
        int length = content.length()
        int max = length / MAX_LENGTH + 1
        int start = 0
        int end = Math.min(length, MAX_LENGTH)
        int i = 1

        while (start < end && end <= length) {
            String messageToEncode = "${i}|${max}|${outputFilename}|${content.substring(start, end)}"
            log.info("{}/{} - {}", i, max, messageToEncode)

            def outputPath = new File(outputDirectory)
            outputPath.mkdirs()


            PipedInputStream inS = new PipedInputStream();
            PipedOutputStream out = new PipedOutputStream(inS);
            new Thread(
                    new Runnable(){
                        public void run(){
                            QRCode.from(messageToEncode).to(ImageType.PNG)
                                    .withSize(400, 400)
                                    .withCharset("UTF-8")
                                    .withErrorCorrection(ErrorCorrectionLevel.L)
                                    .writeTo(out)
                        }
                    }
            ).start();

            picLabel.setIcon(new ImageIcon(ImageIO.read(inS)))

            frame.repaint()

            sleep(SLEEP_TIME * 1000)

            i++
            start = end
            end = Math.min(length, end + MAX_LENGTH)
        }
    }

    private boolean isBinary(String inputFilePath) {
        String string = new File(inputFilePath).getText('UTF-8')

        def alphanumeric = string.findAll { Character.isLetterOrDigit(it as char) }
        def nonalphanumeric = string.findAll { !Character.isLetterOrDigit(it as char) }

        boolean isBinary = nonalphanumeric.size() > alphanumeric.size()
        if (isBinary) {
            println "Binary file detected ${inputFilePath}"
        } else {
            println "Text file detected ${inputFilePath}"
        }

        isBinary
    }
}
