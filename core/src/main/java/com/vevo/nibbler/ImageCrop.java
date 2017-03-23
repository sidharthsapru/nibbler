package com.vevo.nibbler;

import java.awt.image.BufferedImage;
import java.awt.Color;
public class ImageCrop {

    private ImageCrop() {}

    private final static double Default_Tolerance = 0.04;

    public static  BufferedImage cropBlackBorder(BufferedImage source) {
        return cropBlackBorder(source, Default_Tolerance);
    }

    /*
       Crop image borders with Black Color;
        Starts from  Left pixel and checks gradience.
        http://stackoverflow.com/questions/10678015/how-to-auto-crop-an-image-white-border-in-java

     */
    public static BufferedImage cropBlackBorder(BufferedImage source, double tolerance) {

        //Start with RGB Black.
        final int baseColor = Color.BLACK.getRGB();


        final int width = source.getWidth();
        final int height = source.getHeight();

        int topY = height, topX = width ;
        int bottomY = -1, bottomX = -1;


        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if (colorWithinTolerance(baseColor, source.getRGB(x, y), tolerance)) {
                    if (x < topX) topX = x;
                    if (y < topY) topY = y;
                    if (x > bottomX) bottomX = x;
                    if (y > bottomY) bottomY = y;
                }
            }
        }

        BufferedImage destination = new BufferedImage( (bottomX-topX + 1),
                (bottomY-topY + 1), source.getType());

        destination.getGraphics().drawImage(source, 0, 0,
                destination.getWidth(), destination.getHeight(),
                topX, topY, bottomX, bottomY, null);

        return destination;
    }

    private static boolean colorWithinTolerance(int a, int b, double tolerance) {
        int aAlpha  = (int)((a & 0xFF000000) >>> 24);   // Alpha level
        int aRed    = (int)((a & 0x00FF0000) >>> 16);   // Red level
        int aGreen  = (int)((a & 0x0000FF00) >>> 8);    // Green level
        int aBlue   = (int)(a & 0x000000FF);            // Blue level

        int bAlpha  = (int)((b & 0xFF000000) >>> 24);   // Alpha level
        int bRed    = (int)((b & 0x00FF0000) >>> 16);   // Red level
        int bGreen  = (int)((b & 0x0000FF00) >>> 8);    // Green level
        int bBlue   = (int)(b & 0x000000FF);            // Blue level

        double distance = Math.sqrt((aAlpha-bAlpha)*(aAlpha-bAlpha) +
                (aRed-bRed)*(aRed-bRed) +
                (aGreen-bGreen)*(aGreen-bGreen) +
                (aBlue-bBlue)*(aBlue-bBlue));

        // 510.0 is the maximum distance between two colors
        // (0,0,0,0 -> 255,255,255,255)
        double percentAway = distance / 510.0d;

        return (percentAway > tolerance) ;
    }
}
