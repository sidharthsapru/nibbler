package com.vevo.nibbler;


import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImageCropTest {
    @Test
    public void testCrop() {
        BufferedImage testImage = loadImage("src/test/resources/cropTest.jpg");
        int width = testImage.getWidth();

        BufferedImage cropped = ImageCrop.cropBlackBorder(testImage, 0.04);
        int croppedHeight = cropped.getHeight();
        int croppedWidth = cropped.getWidth();

        assertEquals(548, croppedHeight);
        assertEquals(width, croppedWidth);
    }

    @Test
    public void testCropHigherTolerance() {
        BufferedImage testImage = loadImage("src/test/resources/cropTest2.jpg");
        int width = testImage.getWidth();

        BufferedImage cropped = ImageCrop.cropBlackBorder(testImage, 0.1);
        int croppedHeight = cropped.getHeight();
        int croppedWidth = cropped.getWidth();

       // saveImage(cropped);

        assertEquals(248, croppedHeight);
        assertEquals(width, croppedWidth);

        BufferedImage weirdScreenShot = loadImage("src/test/resources/cropTest3.jpg");
        BufferedImage weiredCropped = ImageCrop.cropBlackBorder(weirdScreenShot, 0.1);
        assertEquals(weirdScreenShot.getHeight(), weiredCropped.getHeight());
        assertEquals(879, weiredCropped.getWidth());

    }


    @Test
    public void testNoCrop() {
        BufferedImage testImage = loadImage("src/test/resources/noCrop.jpg");
        BufferedImage cropped = ImageCrop.cropBlackBorder(testImage, 0.1);
        assertTrue(testImage == cropped);
    }

    @Test
    public void testNoCropUneven() {
        BufferedImage testImage = loadImage("src/test/resources/cropTestUneven.jpg");
        BufferedImage cropped = ImageCrop.cropBlackBorder(testImage, 0.1);
        assertTrue(cropped.getHeight() == 430);
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
