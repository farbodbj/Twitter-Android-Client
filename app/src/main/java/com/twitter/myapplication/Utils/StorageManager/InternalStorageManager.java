package com.twitter.myapplication.Utils.StorageManager;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InternalStorageManager {
    private InternalStorageManager(){}

    public static void writeObjectToFile(Context context, Object obj, String fileName) {
        try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)){

             objectOutputStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T readObjectFromFile(Context context, String fileName) {
        T object = null;
        try (FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            object = (T) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
}
