package com.firebot.dhruv.tensorflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GalleryActivity extends AppCompatActivity {
	String absolutePathOfImage;
	private ArrayList<String> listOfAllImages;
	private RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		recyclerView =  findViewById(R.id.gallery);

		listOfAllImages = new ArrayList<>();
		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		recyclerView.setHasFixedSize(true);

		// use a linear layout manager
		GridLayoutManager layoutManager = new GridLayoutManager(this,3);
		recyclerView.setLayoutManager(layoutManager);

		// specify an adapter (see also next example)
		ImageAdapter mAdapter = new ImageAdapter(listOfAllImages,this);
		recyclerView.setAdapter(mAdapter);
		ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_margin);
		recyclerView.addItemDecoration(itemDecoration);


		Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


		String[] projection = {MediaStore.MediaColumns.DATA};
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

		while (cursor.moveToNext()) {
			absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
			listOfAllImages.add(absolutePathOfImage);
		}
		mAdapter.notifyDataSetChanged();

	}




}
