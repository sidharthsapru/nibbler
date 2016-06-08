package com.vevo.nibbler;

import com.vevo.nibbler.model.FileType;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Phil Kulak on 12/22/15.
 */
public class Writer {
    /**
     * Write the image to an output stream.
     *
     * @param image the image to write
     * @param type the output format (jpg, png or bpg)
     * @param quality the compression quality of the output (default 70 for JPEG, 43 for BPG)
     * @param bg the background color for the image (null for white or transparent)
     * @param outputStream the stream to write to
     * @throws IOException
     */
    public static void write(BufferedImage image, FileType type, String quality, String bg, OutputStream outputStream)
            throws IOException {

        float q = parseQuality(quality, type);

        // If we're going to write out a JPEG (or a background was asked for), we need to replace transparency.
        if ((type == FileType.JPEG || bg != null) && image.getColorModel().hasAlpha()) {
            Color bgColor = Color.white;

            // Try out best to use the requested background color.
            if (bg != null) {
                if (!bg.startsWith("#")) {
                    bg = "#" + bg;
                }

                try {
                    bgColor = Color.decode(bg);
                } catch (NumberFormatException e) { /* oh well */ }
            }

            BufferedImage cleaned = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = cleaned.createGraphics();
            graphics2D.setPaint(bgColor);
            graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics2D.drawImage(image, 0, 0, null);
            image = cleaned;
        }

        try {
            ImageWriter writer = ImageIO.getImageWritersByFormatName(type.getExtension()).next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();

            if (type != FileType.PNG) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                if (type == FileType.WEBP) {
                    writeParam.setCompressionType("Lossy");
                }

                writeParam.setCompressionQuality(q);
            }

            ImageOutputStream os = new MemoryCacheImageOutputStream(outputStream);
            writer.setOutput(os);
            IIOImage outputImage = new IIOImage(image, null, null);
            writer.write(null, outputImage, writeParam);
            writer.dispose();
            os.close();
        } finally {
            outputStream.close();
        }
    }

    private static float parseQuality(String quality, FileType type) {
        if (quality != null && quality.length() > 0) {
            try {
                int intQuality = Integer.parseInt(quality);
                return ((float) intQuality) / 100f;
            } catch (Exception e) {
                return defaultQuality();
            }
        }

        return defaultQuality();
    }

    private static float defaultQuality() {
        return 0.8f;
    }
}
