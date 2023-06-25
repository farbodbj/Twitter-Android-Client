package com.twitter.common.Models.Messages.Visuals;

import java.io.File;
import java.io.IOException;

public class Video extends Visual {
    public Video() {super();}
    public Video(File file) throws IOException {
        super(file);
    }
}
