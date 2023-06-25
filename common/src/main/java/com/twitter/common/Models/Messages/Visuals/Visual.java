package com.twitter.common.Models.Messages.Visuals;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.*;

public abstract class Visual implements Serializable {
    private byte[] fileBytes;
    private String fileFormat;
    private transient Path pathInStorage;

    public Visual() {}


    public Visual(File file) throws IOException {
        this.fileBytes = Files.readAllBytes(file.toPath());
        this.fileFormat = file.getName().substring(file.getName().lastIndexOf("."));
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
