package com.firebot.dhruv.tensorflow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.joey);

		ImageView sourceImage = findViewById(R.id.source);

		destImage = findViewById(R.id.segment);

		sourceImage.setImageBitmap(b);

		imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * numBytesPerChannel);
		imgData.order(ByteOrder.nativeOrder());
		labelProbArray = new float[1][inputSize][inputSize][21];

		intValues = new int[inputSize * inputSize];
		segmentMap = new int[inputSize][inputSize];
		try {
			tflite = new Interpreter(loadModelFile(this));


			convertBitmapToByteBuffer(Bitmap.createScaledBitmap(b, inputSize, inputSize, false));
//			convertBitmapToByteBuffer(b);

		} catch (IOException e) {
			e.printStackTrace();
		}


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
		for (int i = 0; i < inputSize; ++i) {
			for (int j = 0; j < inputSize; ++j) {
				int pixelValue = intValues[pixel++];
//				if (isModelQuantized) {
				// Quantized model
				imgData.put((byte) ((pixelValue >> 16) & 0xFF));
				imgData.put((byte) ((pixelValue >> 8) & 0xFF));
				imgData.put((byte) (pixelValue & 0xFF));
//				}

				//else { // Float model
//					imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//					imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//					imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//				}
			}
		}

		tflite.run(imgData, labelProbArray);
		for (int k = 0; k < 21; k++)
			Log.d("Data", String.valueOf(labelProbArray[0][128][128][k]));


		for (int i = 0; i < inputSize; i++)
			for (int j = 0; j < inputSize; j++) {
				segmentMap[i][j] = getIndexOfLargest(labelProbArray[0][i][j]);
//				Log.d("Data", String.valueOf(segmentMap[i][j]));
//				for (int k=0; k<21;k++)
//				if(labelProbArray[0][i][j][0]<6)
//					Log.d("data", String.valueOf(labelProbArray[0][i][j][0]));
			}

		drawBitmap();
		duplicateCount(segmentMap);

		Log.d("Data", "mapped");

	}

	public void drawBitmap() {
		Bitmap imageBitmap = Bitmap.createBitmap(inputSize,
				inputSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imageBitmap);

		Paint p = new Paint();
		p.setColor(Color.BLUE);

		for (int i = 0; i < inputSize; i++)
			for (int j = 0; j < inputSize; j++) {
				if (segmentMap[i][j] != 0)
					canvas.drawPoint(i, j, p);
			}


		destImage.setImageBitmap(imageBitmap);

	}

}
