package br.com.kiks.charades.services;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rsaki on 4/24/2016.
 */
public class PictureCompressorService {
    private static final int REQUIRED_SIZE = 400;

    private Activity mActivity;

    public PictureCompressorService(Activity activity) {
        mActivity = activity;
    }

    private Bitmap getCompressedBitmap(Uri imageUri) throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                mActivity.getContentResolver().openInputStream(imageUri), null, o);

        // Find the correct scale value. It should be the power of 2.
        int scale = getScale(o.outWidth, o.outHeight);

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                mActivity.getContentResolver().openInputStream(imageUri), null, o2);
    }

    private Bitmap getCompressedBitmap(Bitmap inBitmap) throws FileNotFoundException {
        int scale = getScale(inBitmap.getWidth(), inBitmap.getHeight());

        return Bitmap.createScaledBitmap(
                inBitmap,
                inBitmap.getWidth() / scale,
                inBitmap.getHeight() / scale,
                true);
    }

    private int getScale(int width, int height) {
        int scale = 1;
        while (true) {
            if (width / 2 < REQUIRED_SIZE || height / 2 < REQUIRED_SIZE) {
                break;
            }
            width /= 2;
            height /= 2;
            scale *= 2;
        }
        return scale;
    }

    public Uri compress(Uri imageUri) {
        try {
            Bitmap outBitmap = getCompressedBitmap(imageUri);
            return saveToFile(outBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri compress(Bitmap inBitmap) {
        try {
            Bitmap outBitmap = getCompressedBitmap(inBitmap);
            return saveToFile(outBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri saveToFile(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, bos);
        byte[] bitmapData = bos.toByteArray();

        try {
            File file = createImageFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Category_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File charadesDir = new File(storageDir + File.separator + "Charades");
        charadesDir.mkdir();

        return File.createTempFile(imageFileName, ".jpg", charadesDir);
    }
}
