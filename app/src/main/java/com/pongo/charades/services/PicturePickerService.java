package com.pongo.charades.services;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.pongo.charades.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsaki on 4/23/2016.
 */
public class PicturePickerService {
    private static final int MODE_CAMERA = 1;
    private static final int MODE_GALLERY = 2;

    private Activity mActivity;
    private int mMode;
    private Uri mSelectedImage;

    public PicturePickerService(Activity activity) {
        mActivity = activity;
    }

    public Intent GetIntent() {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //takePhotoIntent.putExtra("return-data", true);
        //takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));
        intentList = addIntentsToList(intentList, pickIntent);
        intentList = addIntentsToList(intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    mActivity.getString(R.string.select_picture));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intentList.toArray(new Parcelable[]{}));
        }
        return chooserIntent;
    }

    private List<Intent> addIntentsToList(List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = mActivity.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    public Bitmap getResult(Intent data, int requestCode) {
        Bundle extras = data.getExtras();
        Bitmap bitmap = null;

        // Camera
        if (extras != null) {
            bitmap = (Bitmap) extras.get("data");
            if (bitmap != null) {
                mMode = MODE_CAMERA;
                return bitmap;
            }
        }

        // Gallery
        mMode = MODE_GALLERY;
        mSelectedImage = data.getData();
        if (!checkModePermissions(requestCode)) {
            return null;
        }
        return getResultAfterPermission();
    }

    public Bitmap getResultAfterPermission() {
        switch (mMode) {
            case MODE_GALLERY:
                try {
                    return decodeSelectedUri();
                } catch (FileNotFoundException e) {
                    return null;
                }
        }
        return null;
    }

    private Bitmap decodeSelectedUri() throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                mActivity.getContentResolver().openInputStream(mSelectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 200;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                mActivity.getContentResolver().openInputStream(mSelectedImage), null, o2);

    }

    private boolean checkModePermissions(int requestCode) {
        switch (mMode) {
            case MODE_GALLERY:
                return checkPermission(requestCode, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return true;
    }

    private boolean checkPermission(int requestCode, String permission) {
        if (ContextCompat.checkSelfPermission(mActivity, permission)
                == PackageManager.PERMISSION_GRANTED)
            return true;

        ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
        return false;
    }
}
