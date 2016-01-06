package com.vevo.nibbler;

import com.vevo.nibbler.model.ResizeMethod;
import com.vevo.nibbler.model.ResizeParams;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This is where the magic happens!
 */
public class Resizer {
    public static BufferedImage resize(BufferedImage image, ResizeParams params) {
        switch (params.method) {
            case FIT:
                // Never scale up.
                if (params.width > image.getWidth() && params.height > image.getHeight()) {
                    float requestedRatio = (float) params.width / (float) params.height;
                    float currentRatio = (float) image.getWidth() / (float) image.getHeight();

                    if (requestedRatio < currentRatio) {
                        return resize(image, new ResizeParams(
                                image.getWidth(),
                                (int) (image.getWidth() / requestedRatio),
                                ResizeMethod.FIT
                        ));
                    }

                    return resize(image, new ResizeParams(
                            (int) (image.getHeight() * requestedRatio),
                            image.getHeight(),
                            ResizeMethod.FIT
                    ));
                }

                // Do the scale.
                Scalr.Mode mode = Scalr.Mode.FIT_TO_WIDTH;

                if (((float) image.getWidth()) / ((float) image.getHeight()) < ((float) params.width) / ((float) params.height)) {
                    mode = Scalr.Mode.FIT_TO_HEIGHT;
                }

                image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, mode, params.width, params.height);

                // And, if we need to, put it on a transparent background.
                if (image.getHeight() != params.height || image.getWidth() != params.width) {
                    BufferedImage bg = new BufferedImage(params.width, params.height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = bg.createGraphics();
                    int x = 0;
                    int y = 0;

                    if (image.getHeight() < params.height) {
                        y = (params.height - image.getHeight()) / 2;
                    } else {
                        x = (params.width - image.getWidth()) / 2;
                    }

                    graphics.drawImage(image, x, y, null);
                    graphics.dispose();

                    return bg;
                }

                return image;
            case FIT_WIDTH:
                if (image.getWidth() > params.width) {
                    image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, params.width, params.height);
                }

                if (image.getHeight() > params.height) {
                    return resize(image, new ResizeParams(params.width, params.height));
                }

                return image;
            case FIT_HEIGHT:
                if (image.getHeight() > params.height) {
                    image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, params.width, params.height);
                }

                if (image.getWidth() > params.width) {
                    return resize(image, new ResizeParams(params.width, params.height));
                }

                return image;
            case ROUND:
                int size = Math.min(params.width, params.height);
                image = resize(image, new ResizeParams(size, size));

                int x = 0;
                int y = 0;

                if (image.getHeight() < params.height) {
                    y = (params.height - image.getHeight()) / 2;
                } else {
                    x = (params.width - image.getWidth()) / 2;
                }

                BufferedImage bg = new BufferedImage(params.width, params.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = bg.createGraphics();

                // Clear the image so all pixels have zero alpha
                graphics.setComposite(AlphaComposite.Clear);
                graphics.fillRect(0, 0, params.width, params.height);

                graphics.setComposite(AlphaComposite.Src);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setColor(Color.WHITE);
                graphics.fillOval(x, y, size, size);

                graphics.setComposite(AlphaComposite.SrcAtop);
                graphics.drawImage(image, x, y, null);
                graphics.dispose();

                return bg;
            case FILL:
                // Crop to the right aspect ratio.
                float requestedRatio = (float) params.width / (float) params.height;
                float currentRatio = (float) image.getWidth() / (float) image.getHeight();
                int newWidth = image.getWidth();
                int newHeight = image.getHeight();

                if (requestedRatio < currentRatio) {
                    newWidth = (int) ((float) image.getWidth() * (requestedRatio / currentRatio));
                    image = image.getSubimage((image.getWidth() - newWidth) / 2, 0, newWidth, newHeight);
                } else {
                    newHeight = (int) ((float) image.getHeight() * (currentRatio / requestedRatio));
                    image = image.getSubimage(0, (image.getHeight() - newHeight) / 2, newWidth, newHeight);
                }

                // Do the scale, but only if we are scaling down.
                if (newWidth > params.width) {
                    return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, params.width, params.height);
                } else {
                    return image;
                }
            default:
                throw new UnsupportedOperationException("unknown resize method");
        }
    }
}
