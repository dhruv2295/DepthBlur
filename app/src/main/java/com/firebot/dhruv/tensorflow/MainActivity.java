package com.firebot.dhruv.tensorflow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebot.dhruv.tensorflow.ml.DeeplabInterface;
import com.firebot.dhruv.tensorflow.ml.DeeplabModel;
import com.firebot.dhruv.tensorflow.ml.ImageUtils;
import com.github.shchurov.horizontalwheelview.HorizontalWheelView;
import java.io.File;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
	private int inputSize = 513;

	ImageView destImage;
	int w, h;
	ImageView original;
	Bitmap scaledDown;
	Bitmap resized;
	Bitmap mask;

//	LABEL_NAMES =
//			'0:background', '1:aeroplane', '2:bicycle', '3:bird', '4:boat', '5:bottle', '6:bus',
//			'7:car', '8:cat', '9:chair', '10:cow', '11:diningtable', '12:dog', '13:horse', '14:motorbike',
//			'15:person', '16:pottedplant', '17:sheep', '18:sofa', '19:train', '20:tv'

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		destImage = findViewById(R.id.segment);

		original = findViewById(R.id.original);
		original.setImageBitmap(scaledDown);

		File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);// or DIRECTORY_PICTURES
		File media = new File(sdCard.getAbsolutePath() + "/J.jpg");
//		File media = new File(sdCard.getAbsolutePath() + "/self.jpg");
//		File media = new File(sdCard.getAbsolutePath()+"/Before.jpeg");
//		File media = new File(sdCard.getAbsolutePath()+"/sample_image.jpeg");

		final DeeplabInterface deeplabInterface = DeeplabModel.getInstance();
		DeeplabModel.getInstance().initialize(this);

		Glide.with(this).asBitmap().load(media).into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
				w = resource.getWidth();
				h = resource.getHeight();

				float resizeRatio = (float) inputSize / Math.max(w, h);
				int rw = Math.round(w * resizeRatio);
				int rh = Math.round(h * resizeRatio);

				Timber.d("resize bitmap: ratio = %f, [%d x %d] -> [%d x %d]",
						resizeRatio, w, h, rw, rh);

				resized = ImageUtils.tfResizeBilinear(resource, rw, rh);

//				original.setImageBitmap(BlurBuilder.blur(MainActivity.this,resized));
				original.setImageBitmap(resized);

				mask = deeplabInterface.segment(resized);
				Timber.d(resized.getHeight()+";"+resized.getWidth());
				Timber.d(mask.getHeight()+";"+mask.getWidth());
				destImage.setImageBitmap(mask);

			}
		});



		final HorizontalWheelView horizontalWheelView =  findViewById(R.id.horizontalWheelView);
		horizontalWheelView.setEndLock(true);
		horizontalWheelView.setSnapToMarks(true);
		horizontalWheelView.setListener(new HorizontalWheelView.Listener() {
			@Override
			public void onRotationChanged(double radians) {
				destImage.setImageBitmap(BlurBuilder.blur(MainActivity.this,mask, (int)(1+20* horizontalWheelView.getCompleteTurnFraction())));
//				original.setImageBitmap(BlurBuilder.blur(MainActivity.this,resized, (int)(20* horizontalWheelView.getCompleteTurnFraction())));
//				original.setImageBitmap(BlurBuilder.blur(MainActivity.this,resized, 20));
			}
		});
	}
}
