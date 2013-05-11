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

public class StopListAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public StopListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
			vi = inflater.inflate(R.layout.stops_list, parent, false);

		TextView number = (TextView) vi.findViewById(R.id.number);
		TextView title = (TextView) vi.findViewById(R.id.stoptitle);
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.stop_image);
		HashMap<String, String> linedata = new HashMap<String, String>();
		linedata = data.get(position);
		int numberString = position +1;
		number.setText(numberString+"");
		title.setText(linedata.get("title"));
		imageLoader.DisplayImage(linedata.get("thumbnail"), thumb_image);
		return vi;
	}

}
