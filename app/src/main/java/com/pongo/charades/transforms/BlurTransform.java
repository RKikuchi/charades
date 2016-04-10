package com.pongo.charades.transforms;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

/**
 * Created by rsaki on 4/10/2016.
 */
public class BlurTransform implements Transformation {
    private final RenderScript mRs;
    private final int mRadius;

    public BlurTransform(Context context, int radius) {
        super();
        mRs = RenderScript.create(context);
        mRadius = radius;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        // Create another bitmap that will hold the results of the filter.
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);

        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(mRs, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(mRs, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(mRs, Element.U8_4(mRs));
        script.setInput(input);

        // Set the blur radius
        script.setRadius(mRadius);

        // Start the ScriptIntrinisicBlur
        script.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap);

        return blurredBitmap;
    }

    @Override
    public String key() {
        return "blur";
    }
}
