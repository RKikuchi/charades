package br.com.kiks.charades.services;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.kiks.charades.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsaki on 4/23/2016.
 */
public class PicturePickerService {
    private static final int MODE_CAMERA = 1;
    private static final int MODE_GALLERY = 2;

    final private PictureCompressorService mCompressor;
    private Activity mActivity;
    private int mMode;
    private Uri mSelectedImage;

    public PicturePickerService(Activity activity) {
        mCompressor = new PictureCompressorService(activity);
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

    public Uri getResult(Intent data, int requestCode) {
        Bundle extras = data.getExtras();
        Bitmap bitmap = null;

        // Camera
        if (extras != null) {
            bitmap = (Bitmap) extras.get("data");
            if (bitmap != null) {
                mMode = MODE_CAMERA;
                mSelectedImage = mCompressor.compress(bitmap);
                return mSelectedImage;
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

    public Uri getResultAfterPermission() {
        switch (mMode) {
            case MODE_CAMERA:
            case MODE_GALLERY:
                mSelectedImage = mCompressor.compress(mSelectedImage);
                return mSelectedImage;
        }
        return null;
    }

    private boolean checkModePermissions(int requestCode) {
        switch (mMode) {
            case MODE_CAMERA:
                return checkPermission(requestCode,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            case MODE_GALLERY:
                return checkPermission(requestCode,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return true;
    }

    private boolean checkPermission(int requestCode, String... permissions) {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mActivity, permission)
                    != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(permission);
        }

        if (permissionsNeeded.size() == 0)
            return true;

        ActivityCompat.requestPermissions(
                mActivity,
                permissionsNeeded.toArray(new String[]{}),
                requestCode);
        return false;
    }
}
