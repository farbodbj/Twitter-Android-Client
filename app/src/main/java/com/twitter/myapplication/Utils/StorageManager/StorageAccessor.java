package com.twitter.myapplication.Utils.StorageManager;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.twitter.common.Models.Messages.Visuals.Visual;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class StorageAccessor {
    private final static String OBJECT_FILE_EXTENSION = ".bin";

    public static Uri saveToCache(Context context, String filename, String fileFormat, Visual visual) {
        File file = new File(context.getCacheDir(), (filename + "." + fileFormat));
        try {
            writeFile(file, visual.getFileBytes());
        } catch (IOException e) {
            Log.e(StorageAccessor.class.getName(), "writing to cache failed:\n" + Arrays.toString(e.getStackTrace()));
        }
        return Uri.fromFile(file);
    }

    public static File saveToCache(Context context, String filename, String fileFormat, byte[] fileBytes) {
        File tempFile = new File(context.getCacheDir(), (filename + "." + fileFormat));
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileBytes);
        } catch (IOException e) {
            Log.e(StorageAccessor.class.getName(), "writing bytes to cache failed:\n" + Arrays.toString(e.getStackTrace()));
        }
        return tempFile;
    }

    public static void saveObjectToCache(Context context, String filename, Object obj) {
        File objectFile = new File(context.getCacheDir(), filename + OBJECT_FILE_EXTENSION);
        try(FileOutputStream fos = new FileOutputStream(objectFile); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);

        } catch (IOException e) {
            Log.e(StorageAccessor.class.getName(), "writing bytes to cache failed:\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public static Object readObjectFromCache(Context context, String filename) {
        File objectFile = new File(context.getCacheDir(), filename + OBJECT_FILE_EXTENSION);
        try(FileInputStream fos = new FileInputStream(objectFile); ObjectInputStream oos = new ObjectInputStream(fos)) {
            return oos.readObject();

        } catch (IOException e) {
            Log.e(StorageAccessor.class.getName(), "writing object to cache failed:\n" + Arrays.toString(e.getStackTrace()));

        } catch (ClassNotFoundException e) {
            Log.e(StorageAccessor.class.getName(), "attempt to read object from empty file:\n" + Arrays.toString(e.getStackTrace()));
        }

        return null;
    }


    public static Uri saveToFiles(Context context,  String filename, Visual visual) {
        File file = new File(context.getFilesDir(), (filename + "." + visual.getFileFormat()));
        if(!file.exists()){
            try {
                writeFile(file, visual.getFileBytes());
            } catch (IOException e) {
                Log.e(StorageAccessor.class.getName(), "writing to files failed:\n" + Arrays.toString(e.getStackTrace()));
            }
        }
        return Uri.fromFile(file);
    }


    private static void writeFile(File file, byte[] bytes) throws IOException{
        try(FileOutputStream fis = new FileOutputStream(file)) {
            if (!file.exists())
                file.createNewFile();

            fis.write(bytes);
        }
    }


}
