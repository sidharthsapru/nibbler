package com.vevo.nibbler;

import com.vevo.nibbler.model.ResizeMethod;
import com.vevo.nibbler.model.ResizeParams;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

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

        // If we ask for larger than the source, we should get the largest possible, in the requested aspect ratio.
        resized = Resizer.resize(image, new ResizeParams(5000, 5000, ResizeMethod.FIT));

        assertEquals(1920, resized.getWidth());
        assertEquals(1920, resized.getHeight());

        // Bars right and left.
        resized = Resizer.resize(image, new ResizeParams(5000, 2500, ResizeMethod.FIT));

        assertEquals(2160, resized.getWidth());
        assertEquals(1080, resized.getHeight());

        // Bars top and bottom.
        resized = Resizer.resize(image, new ResizeParams(2500, 5000, ResizeMethod.FIT));

        assertEquals(1920, resized.getWidth());
        assertEquals(3840, resized.getHeight());

    }
}
