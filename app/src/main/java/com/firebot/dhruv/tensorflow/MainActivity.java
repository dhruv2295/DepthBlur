package com.firebot.dhruv.tensorflow;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebot.dhruv.tensorflow.ml.ImageUtils;
import com.github.shchurov.horizontalwheelview.HorizontalWheelView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
	@BindView(R.id.original) ImageView original;
	@BindView(R.id.segment) ImageView destImage;
	@BindView(R.id.exit) ImageView exit;
	@BindView(R.id.horizontalWheelView) HorizontalWheelView horizontalWheelView;
	@BindView(R.id.progressBar) ProgressBar progressBar;
	private int inputSize = 513;
//	private int inputSize = 257;

	int w, h;
	Bitmap scaledDown;
	Bitmap resized;
	Bitmap mask;

//	LABEL_NAMES =
//			'0:background', '1:aeroplane', '2:bicycle', '3:bird', '4:boat', '5:bottle', '6:bus',
//			'7:car', '8:cat', '9:chair', '10:cow', '11:diningtable', '12:dog', '13:horse', '14:motorbike',
//			'15:person', '16:pottedplant', '17:sheep', '18:sofa', '19:train', '20:tv'

	@OnClick(R.id.exit)
	public void _exit(ImageView imageView) {
		finish();
		overridePendingTransition(R.anim.nothing,R.anim.slide_down);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		progressBar.setVisibility(View.VISIBLE);

		original.setImageBitmap(scaledDown);

		Glide.with(this).asBitmap().load(getIntent().getStringExtra("path")).into(new SimpleTarget<Bitmap>() {
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

				original.setImageBitmap(resized);

//				mask = DeeplabModel.getInstance().segment(resized);

				new SegmentTask(result -> {

					mask = result;
					destImage.setImageBitmap(mask);
					Timber.d(resized.getHeight() + ";" + resized.getWidth());
					Timber.d(mask.getHeight() + ";" + mask.getWidth());
					progressBar.setVisibility(View.INVISIBLE);
					horizontalWheelView.setCompleteTurnFraction(0.5f);
				}).execute(resized);

			}
		});


		horizontalWheelView.setEndLock(true);
		horizontalWheelView.setSnapToMarks(true);
		horizontalWheelView.setListener(new HorizontalWheelView.Listener() {
			@Override
			public void onRotationChanged(double radians) {
				if (mask != null)
					destImage.setImageBitmap(BlurBuilder.blur(MainActivity.this, mask, (int) (1 + 20 * horizontalWheelView.getCompleteTurnFraction())));
//				original.setImageBitmap(BlurBuilder.blur(MainActivity.this,resized, (int)(20* horizontalWheelView.getCompleteTurnFraction())));
//				original.setImageBitmap(BlurBuilder.blur(MainActivity.this,resized, 20));
			}
		});
	}
}
