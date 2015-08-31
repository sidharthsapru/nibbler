package com.pkulak.nibbler.model;

/**
 * How would you like your image resized today?
 */
public enum ResizeMethod {
    // Fit the image into the given box. Padding is added to center it.
    FIT,

    // Fit the image into the given box such that the output width is less than the requested width only when the
    // origin width is less than the requested width.
    FIT_WIDTH,

    // Fit the image into the given box such that the output height is less than the requested height only when the
    // origin height is less than the requested height.
    FIT_HEIGHT,

    // Resize and crop the image such that the dimensions match the given dimensions with no padding, unless doing
    // so would require an up-scaling, in which case the image is cropped to match the ratio. Default.
    FILL,

    // Turn the origin image into a circle fitting inside the requested width/height box.
    ROUND;

    public static ResizeMethod fromString(String method) {
        if (method == null) {
            return FILL;
        }

        method = method.toLowerCase();

        if (method.equals("fit")) {
            return ResizeMethod.FIT;
        }

        if (method.equals("fit_width")) {
            return ResizeMethod.FIT_WIDTH;
        }

        if (method.equals("fit_height")) {
            return ResizeMethod.FIT_HEIGHT;
        }

        if (method.equals("round")) {
            return ResizeMethod.ROUND;
        }

        return ResizeMethod.FILL;
    }
}
