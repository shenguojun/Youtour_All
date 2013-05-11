package com.sysu.shen.youtour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sysu.shen.util.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MapPopup extends Activity {
	private ImageLoader imageLoader;
	private ImageView thumb;
	private TextView address;
	private TextView title;
	// private RatingBar rateBar;
	private RelativeLayout mapPopupLayout;
	private String lineString;
	private String stopsString;
	private JSONObject lineJson;
	private JSONArray stopsJArray;
	private int position;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_popup_view);
		Bundle extras = getIntent().getExtras();
		type = extras.getString("type");
		if (type.equals("line")) {
			try {
				lineString = extras.getString("lineString");
				lineJson = new JSONObject(lineString);
			} catch (JSONException e) {
				Log.e("mappopup", "praseLinejsonerror:" + e.toString());
			}
		} else {
			try {
				stopsString = extras.getString("stopsJarray");
				stopsJArray = new JSONArray(stopsString);
				position = Integer.parseInt(extras.getString("position"));
			} catch (JSONException e) {
				Log.e("mappopup", "praseStopsjsonerror:" + e.toString());
			}
		}
		initView();
		initValue();

	}

	private void initView() {
		mapPopupLayout = (RelativeLayout) findViewById(R.id.map_popup_line);
		imageLoader = new ImageLoader(this.getApplicationContext());
		thumb = (ImageView) findViewById(R.id.line_image);
		title = (TextView) findViewById(R.id.title);
		address = (TextView) findViewById(R.id.adress);
		// rateBar = (RatingBar) findViewById(R.id.rating_bar);

	}

	private void initValue() {

		if (type.equals("line")) {
			try {
				imageLoader.DisplayImage(lineJson.getString("coverThumbnail"),
						thumb);
				address.setText(lineJson.getString("mapAddress"));
				title.setText(lineJson.getString("lineName"));
				// Float score =
				// Float.parseFloat(lineJson.getString("totalScore"))
				// / Float.parseFloat(lineJson.getString("totalPeople"));
				// rateBar.setRating(score / 2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				imageLoader.DisplayImage(stopsJArray.getJSONObject(position)
						.getString("stopThumbnail"), thumb);
				address.setVisibility(View.INVISIBLE);
				title.setText(stopsJArray.getJSONObject(position).getString(
						"stopName"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mapPopupLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				detailClicked();
			}
		});

	}

	protected void detailClicked() {
		// 判断是否wifi环境
		ConnectivityManager connManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileCon = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileCon.isConnected()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle("设置WIFI更精彩");
			builder.setMessage("小游检测到您正使用移动网络，站点包括很多图片和音频哦，还是设置一下WIFI吧！");
			builder.setPositiveButton("马上设置",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {
							final Intent intent = new Intent(
									Intent.ACTION_MAIN, null);
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
							final ComponentName cn = new ComponentName(
									"com.android.settings",
									"com.android.settings.wifi.WifiSettings");
							intent.setComponent(cn);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					});
			builder.setNegativeButton("继续浏览",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							if (type.equals("line")) {
								Intent it = new Intent(MapPopup.this,
										StopsList.class);
								try {
									it.putExtra("stopJArray", lineJson
											.getJSONArray("stops").toString());
									it.putExtra("lineName", title.getText());
								} catch (JSONException e) {
									e.printStackTrace();
								}
								startActivity(it);
							} else {
								Intent it = new Intent(MapPopup.this,
										StopMain.class);
								it.putExtra("stopsJarray",
										stopsJArray.toString());
								it.putExtra("position", (position + 1) + "");
								startActivity(it);
							}
						}
					});
			builder.create();
			builder.show();
		} else {
			if (type.equals("line")) {
				Intent it = new Intent(MapPopup.this, StopsList.class);
				try {
					it.putExtra("stopJArray", lineJson.getJSONArray("stops")
							.toString());
					it.putExtra("lineName", title.getText());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(it);
			} else {
				Intent it = new Intent(MapPopup.this, StopMain.class);
				it.putExtra("stopsJarray", stopsJArray.toString());
				it.putExtra("position", (position + 1) + "");
				startActivity(it);
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.finish();
		return true;
	}

}
