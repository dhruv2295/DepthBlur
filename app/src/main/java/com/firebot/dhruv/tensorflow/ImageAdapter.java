package com.firebot.dhruv.tensorflow;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
	private ArrayList<String> mDataset;
	private Context context;
	private ItemClickListener mClickListener;

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		// each data item is just a string in this case
		public ImageView imageView;

		public MyViewHolder(ImageView v) {
			super(v);
			imageView = v;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mClickListener != null) mClickListener.onItemClick(mDataset.get(getAdapterPosition()), getAdapterPosition());

		}
	}

	// Provide a suitable constructor (depends on the kind of dataset)
	public ImageAdapter(ArrayList<String> myDataset, Context context) {
		mDataset = myDataset;
		this.context = context;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public ImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
														int viewType) {
		// create a new view
		ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.image_view, parent, false);

		return new MyViewHolder(v);
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		RequestOptions options = new RequestOptions();
		options.centerCrop();


		Glide.with(context).load(mDataset.get(position)).apply(options).into(holder.imageView);

	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	public void setClickListener(ItemClickListener itemClickListener) {
		this.mClickListener = itemClickListener;
	}
	public interface ItemClickListener {
		void onItemClick(String path, int position);
	}
}
