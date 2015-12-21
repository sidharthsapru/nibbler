package com.vevo.nibbler.model;

/**
 * The two file types we support, plus some info about their mime types.
 */
public enum FileType {
    JPEG ("jpg", "image/jpeg"),
    PNG  ("png", "image/png");

    private String extension;
    private String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static FileType fromString(String path) {
        if (path.toLowerCase().endsWith(".png")) {
            return PNG;
        }
        return JPEG;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }
}