package com.sysu.shen.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.sysu.shen.youtour.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class Myadapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public Myadapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View converview, ViewGroup parent) {
		View vi = converview;
		if (converview == null)
			vi = inflater.inflate(R.layout.list_row, parent, false);

		TextView address = (TextView) vi.findViewById(R.id.adress);
		TextView title = (TextView) vi.findViewById(R.id.title);
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);
		RatingBar rate_bar = (RatingBar) vi.findViewById(R.id.rating_bar);
		HashMap<String, String> linedata = new HashMap<String, String>();
		linedata = data.get(position);
		address.setText(linedata.get("address"));
		title.setText(linedata.get("title"));
		rate_bar.setRating(Float.parseFloat(linedata.get("score")));
		imageLoader.DisplayImage(linedata.get("thumbnail"), thumb_image);
		return vi;
	}

}
