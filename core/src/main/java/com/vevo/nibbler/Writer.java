package com.vevo.nibbler;

import com.google.common.io.ByteStreams;
import com.vevo.nibbler.model.FileType;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

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

        String extension = "png";

        // Write to PNG for both PNG and BPG.
        if (type == FileType.JPEG) {
            extension = type.getExtension();
        }

        // If we're going to write out a JPEG (or a background was asked for), we need to replace transparency.
        if (type == FileType.JPEG || bg != null) {
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

        OutputStream out = outputStream;
        File tmpIn = null;
        File tmpOut = null;

        try {
            // For BPG, write to a file.
            if (type == FileType.BPG) {
                tmpIn = File.createTempFile("bpg", ".png");
                out = new FileOutputStream(tmpIn);
            }

            ImageWriter writer = ImageIO.getImageWritersByFormatName(extension).next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();

            if (type == FileType.JPEG) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(q);
            }

            ImageOutputStream os = new MemoryCacheImageOutputStream(out);
            writer.setOutput(os);
            IIOImage outputImage = new IIOImage(image, null, null);
            writer.write(null, outputImage, writeParam);
            writer.dispose();
            os.close();

            // If we're BPG, we're not done. Use the external executable to process the file we created.
            if (type == FileType.BPG) {
                tmpOut = File.createTempFile("bpg", ".bpg");

                Executor exec = new DefaultExecutor();

                CommandLine cl = new CommandLine("bpgenc")
                        .addArgument("-o").addArgument(tmpOut.getAbsolutePath())
                        .addArgument("-q").addArgument(String.format("%.0f", 51 - (q * 100) / 1.96))
                        .addArgument(tmpIn.getAbsolutePath());

                exec.execute(cl);

                // Write to the response.
                ByteStreams.copy(new FileInputStream(tmpOut), outputStream);
            }
        } finally {
            out.close();
            if (tmpIn != null)  tmpIn.delete();
            if (tmpOut != null) tmpOut.delete();
        }
    }

    private static float parseQuality(String quality, FileType type) {
        if (quality != null && quality.length() > 0) {
            try {
                int intQuality = Integer.parseInt(quality);
                return ((float) intQuality) / 100f;
            } catch (Exception e) {
                return defaultQuality(type);
            }
        }

        return defaultQuality(type);
    }

    private static float defaultQuality(FileType type) {
        return type == FileType.JPEG ? 0.7f : 0.43f;
    }
}
