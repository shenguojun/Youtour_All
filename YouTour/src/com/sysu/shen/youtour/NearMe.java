package com.sysu.shen.youtour;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amapv2.cn.apis.util.AMapUtil;
import com.sysu.shen.util.GlobalConst;
import com.sysu.shen.util.JSONFunctions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class NearMe extends FragmentActivity implements LocationSource,
		AMapLocationListener, OnMarkerClickListener {

	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private Double currentgeoLat;
	private Double currentgeoLng;
	private String cityCode = "";
	private String desc = "";
	public final static int NO_NETWORK = 0;
	private String URLString = "";
	private String URLStringBegin = "beg=";
	private String URLStringEnd = "end=";
	private JSONArray jarray = null;
	private Timer mTimer = new Timer();
	private Boolean needReshLocation = true;
	private ProgressDialog mProgressDialog;

	private ArrayList<LatLng> markersList = new ArrayList<LatLng>();
	private UiSettings mUiSettings;

	PopupWindow mPopupWindow;

	// private static final BitmapDescriptor lineIcon = BitmapDescriptorFactory
	// .fromResource(R.drawable.line);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearme_map);
		init();
		// 定时过5分钟后再从新定位
		mTimer.schedule(mTimerTask, 0, 5 * 60 * 1000);

	}

	/*******************************************************
	 * 通过定时器和Handler来改变是否需要更新位置
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			needReshLocation = true;
		}
	};

	public Bitmap getBitMap(String text) {
		Bitmap bitmap = BitmapDescriptorFactory.fromResource(R.drawable.line)
				.getBitmap();
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		Canvas canvas = new Canvas(bitmap);
		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(20f);
		textPaint.setColor(Color.BLACK);
		canvas.drawText(text, 20, 30, textPaint);// 设置bitmap上面的文字位置
		return bitmap;
	}

	public boolean enableMyLocation() {
		boolean result = false;
		if (mAMapLocationManager
				.isProviderEnabled(LocationProviderProxy.AMapNetwork)) {
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, this);
			result = true;
		}
		return result;
	}

	private void init() {
		if (aMap == null) {
			aMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (AMapUtil.checkReady(this, aMap)) {
				setUpMap();
			}
		}

	}

	private void setUpMap() {
		mUiSettings = aMap.getUiSettings();
		mUiSettings.setAllGesturesEnabled(true);
		mUiSettings.setCompassEnabled(true);
		mUiSettings.setMyLocationButtonEnabled(true);
		mUiSettings.setScaleControlsEnabled(true);
		mUiSettings.setZoomControlsEnabled(true);
		mAMapLocationManager = LocationManagerProxy.getInstance(NearMe.this);
		aMap.setLocationSource(this);
		aMap.setMyLocationEnabled(true);
		aMap.setOnMarkerClickListener(this);

	}

	// 处理线程中抛出的massage
	private Handler mhandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NO_NETWORK:
				Toast.makeText(NearMe.this, "连接网络才能看到哦！", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private class GetJSONAsynTack extends AsyncTask<String, Void, Void> {

		public Activity activity;

		public GetJSONAsynTack(Activity activity2) {
			activity = activity2;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(NearMe.this);
			mProgressDialog.setTitle("正在查询附近线路"); // 设置标题
			mProgressDialog.setMessage("附近线路马上为你呈现，请耐心等待..."); // 设置body信息
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... strings) {
			String URLString = strings[0];
			// 判断是否联网
			final ConnectivityManager conMgr = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {
				jarray = JSONFunctions.getJsonFromNetwork(activity, URLString);

			} else {
				Message m1 = new Message();
				m1.what = NO_NETWORK;
				mhandle.sendMessage(m1);
				jarray = JSONFunctions.getJSONFromFile(activity, URLString);
			}

			JSONObject line = null;
			if (jarray != null) {
				Log.v("log_tag", "jarray.length():" + jarray.length());
				try {
					for (int i = 0; i < jarray.length(); i++) {
						line = jarray.getJSONObject(i);
						double longitude = line.getJSONArray("locate")
								.getDouble(0);
						double latitude = line.getJSONArray("locate")
								.getDouble(1);
						LatLng marker = new LatLng(longitude, latitude);
						markersList.add(marker);
						aMap.addMarker(new MarkerOptions()
								.position(marker)
								.title(line.getString("lineName"))
								.snippet(i + "")
								.icon(BitmapDescriptorFactory
										.fromBitmap(getBitMap(""))));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			super.onPostExecute(result);
			loadMarker();
			mProgressDialog.dismiss();
		}
	}

	public void loadMarker() {
		// 设置所有maker显示在View中
		Builder builder = new Builder();
		for (int i = 0; i < markersList.size(); i++) {
			builder.include(markersList.get(i));
		}
		try {
			LatLngBounds bounds = builder.build();
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 点击返回
	 * 
	 * @param v
	 */
	public void backClicked(View v) {
		mTimerTask.cancel();
		this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		deactivate();
		mTimerTask.cancel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		enableMyLocation();
	}

	@Override
	protected void onDestroy() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (mListener != null) {
			mListener.onLocationChanged(location);
		}
		if (location != null && needReshLocation) {
			currentgeoLat = location.getLatitude();
			currentgeoLng = location.getLongitude();
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			// Message m = new Message();
			// m.what = LOCACHANGE;
			// mhandle.sendMessage(m);
			URLString = GlobalConst.URL_HAEDER_LOC + "x="
					+ currentgeoLng.toString() + "&y="
					+ currentgeoLat.toString() + "&" + URLStringBegin + "0"
					+ "&" + URLStringEnd + "25";
			Log.i("locaturl", URLString);
			URLString = GlobalConst.URL_HAEDER_ALL + URLStringBegin + "0" + "&"
					+ URLStringEnd + "25";
			Log.i("locaturl", URLString);
			String str = ("定位成功:(" + currentgeoLng + "," + currentgeoLat + ")"
					+ "\n城市编码:" + cityCode + "\n位置描述:" + desc);
			Log.i("locatinfo", str);
			Log.i("nearmeurl", URLString);
			// 异步更新列表
			new GetJSONAsynTack(NearMe.this).execute(URLString);

			needReshLocation = false;
		}

	}

	@Override
	public void activate(OnLocationChangedListener arg0) {
		mListener = arg0;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
		}
		// 网络定位
		mAMapLocationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 10, 5000, this);
	}

	@Override
	public void deactivate() {
		mAMapLocationManager.removeUpdates(this);

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		Log.i("marker",
				"title:" + arg0.getTitle() + " snippet:" + arg0.getSnippet());
		Intent it = new Intent(NearMe.this, MapPopup.class);
		try {
			it.putExtra("lineString",
					jarray.getJSONObject(Integer.parseInt(arg0.getSnippet()))
							.toString());
			it.putExtra("type", "line");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		startActivity(it);

		return false;
	}

	@Override
	public void onBackPressed() {
		mTimerTask.cancel();
		super.onBackPressed();
	}

}
