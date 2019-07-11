package com.firebot.dhruv.depthblur;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.firebot.dhruv.depthblur.asynctasks.BitmapTask;
import com.firebot.dhruv.depthblur.asynctasks.SegmentTask;
import com.firebot.dhruv.depthblur.ml.ImageUtils;
import com.firebot.dhruv.depthblur.utils.BlurBuilder;
import com.firebot.dhruv.depthblur.utils.Utils;
import com.github.shchurov.horizontalwheelview.HorizontalWheelView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
	@BindView(R.id.floatingActionButton2) FloatingActionButton share;

	private int inputSize = 513;

	int w, h;
	private Bitmap resized;
	private Bitmap mask;
	private Bitmap blurred;
	private Bitmap source;

	private Uri sourceUri;

//	LABEL_NAMES =
//			'0:background', '1:aeroplane', '2:bicycle', '3:bird', '4:boat', '5:bottle', '6:bus',
//			'7:car', '8:cat', '9:chair', '10:cow', '11:diningtable', '12:dog', '13:horse', '14:motorbike',
//			'15:person', '16:pottedplant', '17:sheep', '18:sofa', '19:train', '20:tv'

	@OnClick(R.id.exit)
	public void _exit(ImageView imageView) {
		finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_down);
	}

	@OnClick(R.id.floatingActionButton2)
	public void _share(FloatingActionButton imageView) {
		progressBar.setVisibility(View.VISIBLE);

		new BitmapTask(w, h, overlay -> {
			File sdc = new File(Environment.getExternalStorageDirectory() + "/DepthBlur");
			if (!sdc.exists()) {
				sdc.mkdirs();
			}

			File resultImage = new File(sdc.getAbsolutePath() + File.separator + Utils.getName(MainActivity.this, sourceUri));
			try (FileOutputStream out = new FileOutputStream(resultImage)) {
				overlay.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
				// PNG is a lossless format, the compression factor (100) is ignored
			} catch (IOException e) {
				e.printStackTrace();
			}

			Uri resulturi = FileProvider.getUriForFile(
					MainActivity.this,
					getApplicationContext()
							.getPackageName() + ".provider", resultImage);

			progressBar.setVisibility(View.INVISIBLE);
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, resulturi);
			shareIntent.setType("image/*");
			startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.open_with)));

			Toast.makeText(getApplicationContext(), "Your image has been saved!", Toast.LENGTH_LONG).show();

		}).execute(source, blurred);



	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		progressBar.setVisibility(View.VISIBLE);
		share.hide();

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if (type.startsWith("image/")) {
				sourceUri =  intent.getParcelableExtra(Intent.EXTRA_STREAM);
			}
		}
		else {
			String sourceFilePath = intent.getStringExtra("path");
			sourceUri = FileProvider.getUriForFile(
					MainActivity.this,
					getApplicationContext()
							.getPackageName() + ".provider", new File(sourceFilePath));
		}

		Glide.with(this).asBitmap().load(sourceUri).into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
				w = resource.getWidth();
				h = resource.getHeight();

				source = resource;
				float resizeRatio = (float) inputSize / Math.max(w, h);
				int rw = Math.round(w * resizeRatio);
				int rh = Math.round(h * resizeRatio);

				Timber.d("resize bitmap: ratio = %f, [%d x %d] -> [%d x %d]",
						resizeRatio, w, h, rw, rh);

				resized = ImageUtils.tfResizeBilinear(resource, rw, rh);

				original.setImageBitmap(source);

//				mask = DeeplabModel.getInstance().segment(resized);

				new SegmentTask(result -> {

					mask = result;
					blurred = result;
					destImage.setImageBitmap(mask);
					share.show();
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

				if (mask != null) {
					blurred = BlurBuilder.blur(MainActivity.this, mask, (int) (1 + 20 * horizontalWheelView.getCompleteTurnFraction()));
					destImage.setImageBitmap(blurred);
				}
			}
		});
	}
}
