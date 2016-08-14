package br.com.kiks.charades.transforms;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Matrix4f;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicColorMatrix;

import com.squareup.picasso.Transformation;

/**
 * Created by rsaki on 4/10/2016.
 */
public class ContrastTransform implements Transformation {
    private final RenderScript mRs;
    private final float mBrightness;
    private final float mContrast;

    public ContrastTransform(Context context, float brightness, float contrast) {
        super();
        mRs = RenderScript.create(context);
        mBrightness = -brightness;
        mContrast = contrast;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        // Create another bitmap that will hold the results of the filter.
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap);

        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(mRs, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(mRs, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicColorMatrix script = ScriptIntrinsicColorMatrix.create(mRs);
        script.setColorMatrix(new Matrix4f(new float[]{
                mContrast, 0, 0, mBrightness,
                0, mContrast, 0, mBrightness,
                0, 0, mContrast, mBrightness,
                0, 0, 0, 1
        }));

        // Start the Script
        script.forEach(input, output);

        // Copy the output to the blurred bitmap
        output.copyTo(outputBitmap);

        return outputBitmap;
    }

    @Override
    public String key() {
        return "blur";
    }
}
