package com.firebot.dhruv.tensorflow;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryActivity extends AppCompatActivity {
	String absolutePathOfImage;
	@BindView(R.id.gallery) RecyclerView recyclerView;
	@BindView(R.id.textView) TextView textView;

	private ArrayList<String> listOfAllImages;

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
		ImageAdapter mAdapter = new ImageAdapter(listOfAllImages, this);
		recyclerView.setAdapter(mAdapter);
		ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_margin);
		recyclerView.addItemDecoration(itemDecoration);


		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


		String[] projection = {MediaStore.MediaColumns.DATA};
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

		while (cursor.moveToNext()) {
			absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
			listOfAllImages.add(absolutePathOfImage);
		}
		mAdapter.notifyDataSetChanged();


		mAdapter.setClickListener(new ImageAdapter.ItemClickListener() {
			@Override
			public void onItemClick(String path, int position) {
				Intent i = new Intent(GalleryActivity.this, MainActivity.class);
				i.putExtra("path", path);
				startActivity(i);
				overridePendingTransition(R.anim.slide_up,R.anim.nothing);
			}
		});
	}


}
