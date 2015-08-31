package com.pkulak.nibbler.model;

import javax.servlet.http.HttpServletRequest;

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

    public ResizeParams(HttpServletRequest req) {
        width = tryParse(req.getParameter("width"));
        height = tryParse(req.getParameter("height"));
        method = ResizeMethod.fromString(req.getParameter("resize"));
    }

    public static int tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}