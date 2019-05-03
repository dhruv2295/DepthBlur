package com.firebot.dhruv.tensorflow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.firebot.dhruv.tensorflow.Utils.duplicateCount;
import static com.firebot.dhruv.tensorflow.Utils.getIndexOfLargest;

public class MainActivity extends AppCompatActivity {
	protected Interpreter tflite;
	private ByteBuffer imgData;
	private int numBytesPerChannel = 4;
	private int inputSize = 257;
	private int[] intValues;

	private static final float IMAGE_MEAN = 128.0f;
	private static final float IMAGE_STD = 128.0f;

	private float[][][][] labelProbArray = null;

	private int[][] segmentMap;
	ImageView destImage;
	int w, h;
	ImageView sourceImage;

	Bitmap scaledDown;
	Bitmap scaleExtend;

//	LABEL_NAMES =
//			'0:background', '1:aeroplane', '2:bicycle', '3:bird', '4:boat', '5:bottle', '6:bus',
//			'7:car', '8:cat', '9:chair', '10:cow', '11:diningtable', '12:dog', '13:horse', '14:motorbike',
//			'15:person', '16:pottedplant', '17:sheep', '18:sofa', '19:train', '20:tv'

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		destImage = findViewById(R.id.segment);
		imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * numBytesPerChannel);
		imgData.order(ByteOrder.nativeOrder());
		labelProbArray = new float[1][inputSize][inputSize][21];

		intValues = new int[inputSize * inputSize];
		segmentMap = new int[inputSize][inputSize];


		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.joey_small);

		File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);// or DIRECTORY_PICTURES
		File media = new File(sdCard.getAbsolutePath() + "/J.jpg");
//		File media = new File(sdCard.getAbsolutePath() + "/self.jpg");
//		File media = new File(sdCard.getAbsolutePath()+"/Before.jpeg");
//		File media = new File(sdCard.getAbsolutePath()+"/sample_image.jpeg");

		sourceImage = findViewById(R.id.source);

		Glide.with(this).asBitmap().load(media).into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
				sourceImage.setImageBitmap(resource);
				w = resource.getWidth();
				h = resource.getHeight();

				try {
					tflite = new Interpreter(loadModelFile(MainActivity.this));

					float resizeRatio = (float) inputSize / Math.max(w, h);
					int rw = Math.round(w * resizeRatio);
					int rh = Math.round(h * resizeRatio);


					scaledDown = Bitmap.createScaledBitmap(resource, rw, rh, true);
					scaleExtend = Utils.extendBitmap(scaledDown, inputSize, inputSize, Color.BLACK);

					sourceImage.setImageBitmap(scaleExtend);
					Log.d("Data", rw + ":" + rh);

					convertBitmapToByteBuffer(scaleExtend);
//			convertBitmapToByteBuffer(b);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});


	}

	/**
	 * Memory-map the model file in Assets.
	 */
	private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
		AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("deeplabv3.tflite");
		FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
		FileChannel fileChannel = inputStream.getChannel();
		long startOffset = fileDescriptor.getStartOffset();
		long declaredLength = fileDescriptor.getDeclaredLength();


		return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
	}

	private void convertBitmapToByteBuffer(Bitmap bitmap) {
		bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		int pixel = 0;
		imgData.rewind();

		for (int i = 0; i < inputSize; ++i) {
			for (int j = 0; j < inputSize; ++j) {
				int pixelValue = intValues[pixel++];
//				if (isModelQuantized) {
				// Quantized model
//				imgData.put((byte) ((pixelValue >> 16) & 0xFF));
//				imgData.put((byte) ((pixelValue >> 8) & 0xFF));
//				imgData.put((byte) (pixelValue & 0xFF));
//				}

				//else { // Float model
				imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
				imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
				imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//				}
			}
		}

		tflite.run(imgData, labelProbArray);


//		Utils.fillZeroes(segmentMap);
		for (int i = 0; i < inputSize; i++)
			for (int j = 0; j < inputSize; j++) {
				segmentMap[i][j] = getIndexOfLargest(labelProbArray[0][i][j]);
			}

		drawBitmap();
		duplicateCount(segmentMap);

		Log.d("Data", "mapped");

	}

	public void drawBitmap() {
		Bitmap mask = Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mask);

		Paint p = new Paint();
		p.setColor(Color.RED);

		for (int i = 0; i < inputSize; i++)
			for (int j = 0; j < inputSize; j++) {
				if (segmentMap[i][j] != 0)
				{
//					p.setColor(scaleExtend.getPixel(j,i));
					canvas.drawPoint(j, i, p);
				}
				else
				{
//					p.setColor(blurred.getPixel(j,i));
//					canvas.drawPoint(j, i, p);

				}
			}

		Bitmap scaleUp = Bitmap.createScaledBitmap(mask, w, h, false);

		ImageView original = findViewById(R.id.original);
		original.setImageBitmap(scaledDown);
		destImage.setImageBitmap(mask);
		destImage.setAlpha(0.5f);
//		destImage.setImageBitmap(scaleUp);


	}

}
