package com.firebot.dhruv.tensorflow;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity {
	public static final int EXTERNAL_READ = 102;
	public static final int EXTERNAL_WRITE = 103;

	String absolutePathOfImage;
	@BindView(R.id.gallery)
	RecyclerView recyclerView;
	@BindView(R.id.textView)
	TextView textView;

	private ArrayList<String> listOfAllImages;
	private ImageAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		ButterKnife.bind(this);


		listOfAllImages = new ArrayList<>();
		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		recyclerView.setHasFixedSize(true);

		// use a linear layout manager
		GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
		recyclerView.setLayoutManager(layoutManager);

		// specify an adapter (see also next example)
		mAdapter = new ImageAdapter(listOfAllImages, this);
		recyclerView.setAdapter(mAdapter);
		ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_margin);
		recyclerView.addItemDecoration(itemDecoration);


		mAdapter.setClickListener((path, position) -> {
			Intent i = new Intent(GalleryActivity.this, MainActivity.class);
			i.putExtra("path", path);
			startActivity(i);
			overridePendingTransition(R.anim.slide_up,R.anim.nothing);
		});

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			// Permission is not granted
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
					EXTERNAL_READ);
		}
		else
			pickImagesfromStorage();

	}

	private void pickImagesfromStorage() {
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
		String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

		Cursor cursor = getContentResolver().query(uri, projection, null, null, orderBy);

		while (cursor.moveToNext()) {
			absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
			listOfAllImages.add(absolutePathOfImage);
		}
		mAdapter.notifyDataSetChanged();

	}


	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case EXTERNAL_READ: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					pickImagesfromStorage();
					// permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request.
		}
	}
}
