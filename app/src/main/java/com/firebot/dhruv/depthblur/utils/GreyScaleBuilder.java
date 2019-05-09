package com.firebot.dhruv.depthblur.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicColorMatrix;

public class GreyScaleBuilder {


	public static Bitmap convert(Context context, Bitmap image) {
		int width = Math.round(image.getWidth());
		int height = Math.round(image.getHeight());

		Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
		Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

		RenderScript rs = RenderScript.create(context);


		Allocation input = Allocation.createFromBitmap(rs, inputBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
		Allocation output = Allocation.createTyped(rs, input.getType());

		// Inverts and grayscales the image
		final ScriptIntrinsicColorMatrix inverter = ScriptIntrinsicColorMatrix.create(rs);
		inverter.setGreyscale();
		inverter.forEach(input, output);
		output.copyTo(outputBitmap);

		inputBitmap.recycle();
		rs.destroy();
		inverter.destroy();
		input.destroy();
		output.destroy();

		return outputBitmap;
	}
}
