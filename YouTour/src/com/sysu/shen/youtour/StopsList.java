package com.sysu.shen.youtour;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sysu.shen.util.StopListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StopsList extends Activity {
	private TextView lineName;
	private ListView list;
	private StopListAdapter myadapter;
	private JSONArray stopsJArray;
	private String stopString;
	private View header;
	private View footer;
	private String lineNameString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stopslist_main);
		Bundle extras = getIntent().getExtras();
		stopString = extras.getString("stopJArray");
		lineNameString = extras.getString("lineName");
		try {
			stopsJArray = new JSONArray(stopString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		initView();
		initValue();
	}

	private void initView() {
		lineName = (TextView) findViewById(R.id.line_name);
		header = getLayoutInflater().inflate(R.layout.stopheader, null);
		footer = getLayoutInflater().inflate(R.layout.stopfooter, null);
		list = (ListView) findViewById(R.id.stop_list);

	}

	private void initValue() {
		lineName.setText(lineNameString);
		JSONObject line = null;
		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		try {
			for (int i = 0; i < stopsJArray.length(); i++) {
				line = stopsJArray.getJSONObject(i);
				HashMap<String, String> lineMap = new HashMap<String, String>();
				try {
					lineMap.put("title", line.getString("stopName"));
					lineMap.put("thumbnail", line.getString("stopThumbnail"));

				} catch (Exception e) {
					e.printStackTrace();
				}
				mylist.add(lineMap);
			}
		} catch (Exception e) {
			Log.v("log_tag", "analyseJsonexception:" + e.toString());
		}
		myadapter = new StopListAdapter(this, mylist);

		list.addHeaderView(header, null, false);
		list.addFooterView(footer, null, false);
		list.setAdapter(myadapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it = new Intent(StopsList.this, StopMain.class);
				it.putExtra("stopsJarray", stopsJArray.toString());
				it.putExtra("position", position + "");
				startActivity(it);

			}
		});

	}

	/**
	 * 点击返回
	 * 
	 * @param v
	 */
	public void backClicked(View v) {
		this.finish();
	}

	/**
	 * 点击地图
	 * 
	 * @param v
	 */
	public void mapbuttonClicked(View v) {
		Intent it = new Intent(StopsList.this, StopMap.class);
		it.putExtra("stopsJarray", stopsJArray.toString());
		startActivity(it);
	}

}
