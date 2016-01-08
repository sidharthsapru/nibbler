package com.vevo.nibbler.model;

/**
 * The two file types we support, plus some info about their mime types.
 */
public enum FileType {
    JPEG ("jpg", "image/jpeg"),
    BPG  ("bpg", "image/bpg"),
    PNG  ("png", "image/png");

    private String extension;
    private String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static FileType fromPath(String path) {
        if (path.toLowerCase().endsWith(".png")) {
            return PNG;
        }
        if (path.toLowerCase().endsWith(".bpg")) {
            return BPG;
        }
        return JPEG;
    }

    public static FileType fromString(String s) {
        if (s.equalsIgnoreCase("png")) {
            return PNG;
        }
        if (s.equalsIgnoreCase("bpg")) {
            return BPG;
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