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
    /**
     * Resize the given image based on the parameters.
     *
     * @param image The image to resize.
     * @param params The set of resize options.
     * @return The resized image.
     */
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

                image = Scalr.resize(image, Scalr.Method.AUTOMATIC, mode, params.width, params.height);

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
                    image = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, params.width, params.height);
                }

                if (image.getHeight() > params.height) {
                    return resize(image, params.changeMethod(ResizeMethod.FILL));
                }

                return image;
            case FIT_HEIGHT:
                if (image.getHeight() > params.height) {
                    image = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, params.width, params.height);
                }

                if (image.getWidth() > params.width) {
                    return resize(image, params.changeMethod(ResizeMethod.FILL));
                }

                return image;
            case ROUND:
                int size = Math.min(params.width, params.height);
                image = resize(image, new ResizeParams(size, size, ResizeMethod.FILL, params.horizontalBias, params.verticalBias));

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
                    int newX = (int) ((image.getWidth() - newWidth) * params.horizontalBias);

                    image = image.getSubimage(newX, 0, newWidth, newHeight);
                } else {
                    newHeight = (int) ((float) image.getHeight() * (currentRatio / requestedRatio));
                    int newY = (int) ((image.getHeight() - newHeight) * params.verticalBias);

                    image = image.getSubimage(0, newY, newWidth, newHeight);
                }

                // Do the scale, but only if we are scaling down.
                if (newWidth > params.width) {
                    return Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT, params.width, params.height);
                } else {
                    return image;
                }
            default:
                throw new UnsupportedOperationException("unknown resize method");
        }
    }
}
