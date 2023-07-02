package com.twitter.myapplication.Utils;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

final public class AndroidUtils {
    private AndroidUtils(){}

    public static void gotoFragment(FragmentManager fragmentManager, Fragment newFragment, int fragmentContainerId, Bundle bundle) {
        if(bundle != null)
            newFragment.setArguments(bundle);
        fragmentManager
                .beginTransaction()
                .replace(fragmentContainerId, newFragment)
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();
    }

    public static void gotoActivity(Context context, Class<? extends Activity> destinationActivity, Bundle bundle) {
        Intent intent = new Intent(context, destinationActivity);
        if(bundle != null)
            intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    public static void runWithDelay(Runnable runnable, long timeMilis) {
        (new Handler()).postDelayed(runnable, timeMilis);
    }

    public static void showLongToastMessage(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        setAndShowToast(toast, duration);
    }

    public static void showShortMessage(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        setAndShowToast(toast, duration);
    }

    private static void setAndShowToast(Toast toast, int duration) {
        if(duration != -1)
            toast.setDuration(duration);
        toast.show();
    }

    public static byte[] getBytesFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();

        try(
            ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            FileInputStream fis = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO Exception");
        }

        return null;
    }


    public static ActivityResultLauncher<PickVisualMediaRequest> multipleImageSelector(ActivityResultCaller resultCaller, int maxSelectionCount, MultipleImageChooserCallback callback) {
        return resultCaller.registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(maxSelectionCount),
            uris -> {
                if (!uris.isEmpty()) {
                    callback.onMultipleImageChosen(uris);
                    Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            }
        );
    }


    public static ActivityResultLauncher<PickVisualMediaRequest> singleImageSelector(ActivityResultCaller resultCaller, SingleImageChooser callback) {
        return resultCaller.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    callback.onSingleImageChosen(uri);
                    Log.d("PhotoPicker", "Number of items selected: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            }
        );
    }

}
