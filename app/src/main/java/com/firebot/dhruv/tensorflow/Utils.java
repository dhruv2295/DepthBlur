package com.firebot.dhruv.tensorflow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Utils {

	public static int getIndexOfLargest(float[] array) {
		if (array == null || array.length == 0) return -1; // null or empty

		int largest = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] > array[largest]) largest = i;
		}
		return largest; // position of the first largest found
	}


	public static void duplicateCount(int[][] data) {
		HashMap<Integer, Integer> repetitions = new HashMap<>();

		for (int i = 0; i < 257; ++i) {
			for (int j = 0; j < 257; ++j) {
				int item = data[i][j];

				if (repetitions.containsKey(item))
					repetitions.put(item, repetitions.get(item) + 1);
				else
					repetitions.put(item, 1);
			}
		}

		// Now let's print the repetitions out
		StringBuilder sb = new StringBuilder();

		int overAllCount = 0;

		for (Map.Entry<Integer, Integer> e : repetitions.entrySet()) {
//			if (e.getValue() > 1) {
			overAllCount += 1;

			sb.append("\n");
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue());
			sb.append(" times");
//			}
		}

		if (overAllCount > 0) {
			sb.insert(0, " repeated numbers:");
			sb.insert(0, overAllCount);
			sb.insert(0, "There are ");
		}

		Log.d("Data", sb.toString());

	}


	public static void fillZeroes(int[][] array) {
		if (array == null) {
			return;
		}

		int r;
		for (r = 0; r < array.length; r++) {
			Arrays.fill(array[r], 0);
		}
	}

	public static Bitmap extendBitmap(Bitmap origin, int destW, int destH, int backgroundColor) {
		if (origin != null && destW > 0 && destH > 0) {
			int var4 = origin.getWidth();
			int var5 = origin.getHeight();
			if (destW >= var4 && destH >= var5) {
				Log.d("Data", "origin =, dest =" + new Object[]{var4, var5, destW, destH});
				Bitmap var6 = Bitmap.createBitmap(destW, destH, Bitmap.Config.ARGB_8888);
				Canvas var7 = new Canvas(var6);
				Paint var8 = new Paint(1);
				var7.drawColor(backgroundColor);
				int var9 = (int) Math.round((double) (destW - var4) / 2.0D);
				int var10 = (int) Math.round((double) (destH - var5) / 2.0D);
				Log.d("Data", "xOffset =, yOffset = " + new Object[]{var9, var10});
				var7.drawBitmap(origin, (float) var9, (float) var10, var8);
				return var6;
			} else {
				return origin;
			}
		} else {
			return origin;
		}
	}


	public static Bitmap rotateBitmap(Bitmap bitmap, InputStream path) {

		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_UNDEFINED);

		Log.d("Utils", "Orientation:"+ orientation);
		Matrix matrix = new Matrix();
		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return bitmap;
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				matrix.setScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.setRotate(180);
				break;
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				matrix.setRotate(180);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_TRANSPOSE:
				matrix.setRotate(90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.setRotate(90);
				break;
			case ExifInterface.ORIENTATION_TRANSVERSE:
				matrix.setRotate(-90);
				matrix.postScale(-1, 1);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.setRotate(-90);
				break;
			default:
				return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return bmRotated;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, new Matrix(), null);
		return bmOverlay;
	}

}
