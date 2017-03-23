package com.vevo.nibbler;


import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ImageCropTest {

    @Test
    public void testWidthCrop() {
        BufferedImage testImage = loadImage("src/test/resources/cropTest.jpg");
        int height = testImage.getHeight();
        int width = testImage.getWidth();
        BufferedImage cropped = ImageCrop.cropBlackBorder(testImage, 0.04);
        int croppedHeight = cropped.getHeight();
        int croppedWidth = cropped.getWidth();
        assertNotEquals(height, croppedHeight);
        assertEquals(width, croppedWidth);

    }

    @Test
    public void testHeightCrop() {
        BufferedImage testImage = loadImage("src/test/resources/tmv.jpg");
        int height = testImage.getHeight();
        int width = testImage.getWidth();
        BufferedImage cropped = ImageCrop.cropBlackBorder(testImage, 0.04);
        int croppedHeight = cropped.getHeight();
        int croppedWidth = cropped.getWidth();
        assertEquals(height, croppedHeight);
        assertNotEquals(width, croppedWidth);

    }

    @Test
    public void testNoCrop() {
        BufferedImage testImage = loadImage("src/test/resources/noCrop.jpg");
        int height = testImage.getHeight();
        int width = testImage.getWidth();
        BufferedImage cropped = ImageCrop.cropBlackBorder(testImage, 0.04);
        int croppedHeight = cropped.getHeight();
        int croppedWidth = cropped.getWidth();
        assertEquals(height, croppedHeight);
        assertEquals(width, croppedWidth);
    }



    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
