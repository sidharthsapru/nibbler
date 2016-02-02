package com.vevo.nibbler.model;

import java.util.Map;

/**
 * Hold everything we need to know exactly how to resize an image.
 */
public class ResizeParams {
    public int width;
    public int height;
    public ResizeMethod method;
    public float horizontalBias = 0.5f;
    public float verticalBias = 0.5f;

    public ResizeParams(int width, int height) {
        this.width = width;
        this.height = height;
        this.method = ResizeMethod.FILL;
    }

    public ResizeParams(int width, int height, ResizeMethod method) {
        this.width = width;
        this.height = height;
        this.method = method;
    }

    public ResizeParams(int width, int height, ResizeMethod method, float biasX, float biasY) {
        this.width = width;
        this.height = height;
        this.method = method;
        horizontalBias = biasX;
        verticalBias = biasY;
    }

    public ResizeParams(Map<String, String[]> req) {
        width = tryParse(req.get("width"), 0);
        height = tryParse(req.get("height"), 0);
        method = ResizeMethod.fromString(firstParam(req.get("resize")));

        if (req.containsKey("bias_x")) {
            horizontalBias = ((float) tryParse(req.get("bias_x"), 50)) / 100f;

            if (horizontalBias < 0) horizontalBias = 0;
            if (horizontalBias > 1) horizontalBias = 1;
        }

        if (req.containsKey("bias_y")) {
            verticalBias = ((float) tryParse(req.get("bias_y"), 50)) / 100f;

            if (verticalBias < 0) verticalBias = 0;
            if (verticalBias > 1) verticalBias = 1;
        }
    }

    public ResizeParams changeMethod(ResizeMethod method) {
        return new ResizeParams(width, height, method, horizontalBias, verticalBias);
    }

    private static String firstParam(String[] params) {
        if (params == null || params.length == 0) return "";

        return params[0];
    }

    private static int tryParse(String[] text, int def) {
        try {
            return Integer.parseInt(firstParam(text));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}