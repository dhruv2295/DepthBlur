package com.firebot.dhruv.depthblur;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.firebot.dhruv.depthblur.ml.DeeplabModel;



public class SegmentTask extends AsyncTask<Bitmap, String, Bitmap> {
	private final Listener mListener;

	public SegmentTask(Listener listener) {
		mListener = listener;

	}

	@Override
	protected Bitmap doInBackground(Bitmap... resized) {
		return DeeplabModel.getInstance().segment(resized[0]);
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