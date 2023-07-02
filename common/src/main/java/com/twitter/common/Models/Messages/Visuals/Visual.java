package com.twitter.common.Models.Messages.Visuals;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashSet;

public abstract class Visual implements Serializable {
    public final static HashSet<String> ALLOWED_VIDEO_FORMAT_EXTENSIONS = new HashSet<>(Arrays.asList("mp4", "gif"));
    public final static HashSet<String> ALLOWED_IMAGE_FORMAT_EXTENSIONS = new HashSet<>(Arrays.asList("png", "jpg", "jpeg"));
    private byte[] fileBytes;
    private String fileFormat;
    private transient Path pathInStorage;

    public Visual() {}


    public Visual(File file) {
        try{
            this.fileBytes = Files.readAllBytes(file.toPath());
            this.fileFormat = file.getName().substring(file.getName().lastIndexOf("."));
        } catch (IOException e) {
            System.out.println("error: " + e.getMessage());
            System.out.println("Error reading file: " + file.getName());
        }
    }

    public Visual(String pathInStorage) {
        this.pathInStorage = Paths.get(pathInStorage);
    }


    public byte[] getFileBytes() {
        return fileBytes.clone();
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setPathInStorage(Path pathInStorage) {
        this.pathInStorage = pathInStorage;
    }

    public void setPathInStorage(String pathInStorage) {
        this.pathInStorage = Paths.get(pathInStorage);
    }

    public Path getPathInStorage() {
        return pathInStorage;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
}
