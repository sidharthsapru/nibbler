package com.vevo.nibbler;

import com.vevo.nibbler.model.ResizeMethod;
import com.vevo.nibbler.model.ResizeParams;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Phil Kulak on 1/5/16.
 */
public class ResizerTest {
    private static BufferedImage image;

    static {
        try {
            image = ImageIO.read(new File("src/test/resources/tmv.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFit() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(50, 50, ResizeMethod.FIT));

        assertEquals(50, resized.getWidth());
        assertEquals(50, resized.getHeight());
    }

    @Test
    public void testFitOverSizeEqualRatio() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(5000, 5000, ResizeMethod.FIT));

        assertEquals(1920, resized.getWidth());
        assertEquals(1920, resized.getHeight());
    }

    @Test
    public void testFitOverSizePillarBox() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(5000, 2500, ResizeMethod.FIT));

        assertEquals(2160, resized.getWidth());
        assertEquals(1080, resized.getHeight());

        // Bars left and right
        assertEquals(0, resized.getRGB(0, 540));
        assertEquals(0, resized.getRGB(2159, 540));

        // No bars top and bottom.
        assertNotEquals(0, resized.getRGB(1080, 0));
        assertNotEquals(0, resized.getRGB(1080, 1079));
    }

    @Test
    public void testFitOverSizeLetterBox() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(2500, 5000, ResizeMethod.FIT));

        assertEquals(1920, resized.getWidth());
        assertEquals(3840, resized.getHeight());

        // No bars left and right.
        assertNotEquals(0, resized.getRGB(0, 1920));
        assertNotEquals(0, resized.getRGB(1919, 1920));

        // Bars top and bottom.
        assertEquals(0, resized.getRGB(960, 0));
        assertEquals(0, resized.getRGB(960, 3839));
    }

    @Test
    public void testFitWidth() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(50, 50, ResizeMethod.FIT_WIDTH));

        assertEquals(50, resized.getWidth());
        assertEquals(28, resized.getHeight());
    }

    @Test
    public void testFitWidthOverSize() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(5000, 5000, ResizeMethod.FIT_WIDTH));

        assertEquals(1920, resized.getWidth());
        assertEquals(1080, resized.getHeight());
    }

    @Test
    public void testFitHeight() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(50, 50, ResizeMethod.FIT_HEIGHT));

        assertEquals(50, resized.getWidth());
        assertEquals(50, resized.getHeight());
    }

    @Test
    public void testFitHeightOverSize() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(5000, 5000, ResizeMethod.FIT_HEIGHT));

        assertEquals(1920, resized.getWidth());
        assertEquals(1080, resized.getHeight());
    }

    @Test
    public void testFill() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(50, 50, ResizeMethod.FILL));

        assertEquals(50, resized.getWidth());
        assertEquals(50, resized.getHeight());
    }

    @Test
    public void testFillOverSizePillarBox() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(5000, 5000, ResizeMethod.FILL));

        assertEquals(1080, resized.getWidth());
        assertEquals(1080, resized.getHeight());
    }

    @Test
    public void testFillOverSizeLetterBox() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(5000, 1000, ResizeMethod.FILL));

        assertEquals(1920, resized.getWidth());
        assertEquals(384, resized.getHeight());
    }

    @Test
    public void testRound() {
        BufferedImage resized = Resizer.resize(image, new ResizeParams(50, 50, ResizeMethod.ROUND));

        assertEquals(50, resized.getWidth());
        assertEquals(50, resized.getHeight());

        // The four corners should be transparent.
        assertEquals(0, resized.getRGB(0, 0));
        assertEquals(0, resized.getRGB(49, 0));
        assertEquals(0, resized.getRGB(49, 49));
        assertEquals(0, resized.getRGB(0, 49));

        // The four middle sides should not be transparent.
        assertNotEquals(0, resized.getRGB(0, 24));
        assertNotEquals(0, resized.getRGB(24, 0));
        assertNotEquals(0, resized.getRGB(49, 24));
        assertNotEquals(0, resized.getRGB(24, 49));
    }
}
