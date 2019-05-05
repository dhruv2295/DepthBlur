package com.firebot.dhruv.depthblur;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.firebot.dhruv.depthblur.ml.ImageUtils;


public class BitmapTask extends AsyncTask<Bitmap, String, Bitmap> {
	private final Listener mListener;
	int w;
	int h;



	public BitmapTask(int w, int h, Listener listener) {
		mListener = listener;
		this.w = w;
		this.h = h;
	}

	@Override
	protected Bitmap doInBackground(Bitmap... data) {
		return Utils.overlay(data[0], ImageUtils.tfResizeBilinear(data[1], w, h));
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		mListener.onCompleted(result);
	}

	public interface Listener {
		void onCompleted(Bitmap result);
	}
}