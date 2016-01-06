package com.vevo.nibbler.model;

import java.util.Map;

/**
 * Hold everything we need to know exactly how to resize an image.
 */
public class ResizeParams {
    public int width;
    public int height;
    public ResizeMethod method;

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

    public ResizeParams(Map<String, String[]> req) {
        width = tryParse(req.get("width"));
        height = tryParse(req.get("height"));
        method = ResizeMethod.fromString(firstParam(req.get("resize")));
    }

    private static String firstParam(String[] params) {
        if (params == null || params.length == 0) return "";

        return params[0];
    }

    private static int tryParse(String[] text) {
        try {
            return Integer.parseInt(firstParam(text));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}