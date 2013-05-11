package com.sysu.shen.youtour;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.search.core.AMapException;
import com.amap.api.search.core.LatLonPoint;
import com.amap.api.search.route.Route;
import com.amapv2.cn.apis.util.AMapUtil;
import com.amapv2.cn.apis.util.Constants;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class StopMap extends FragmentActivity implements OnMarkerClickListener {

	private static final float lineWidth = 6.9F;
	private static final float zoomLevel = (float) 9;
	private static final int zoomPadding = 1;
	private AMap mMap;
	private UiSettings mUiSettings;

	private String stopslistString;
	private JSONArray stopsJsonArray;
	private JSONObject stopJson;
	private ArrayList<LatLonPoint> LatLonPointList = new ArrayList<LatLonPoint>();

	private List<Route> routeResult;
	private Route route;
	private int mode = Route.DrivingLeastDistance;

	// private ProgressDialog mProgressDialog;s

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.stop_map);
		Bundle extras = getIntent().getExtras();
		stopslistString = extras.getString("stopsJarray");
		// 初始化地图
		init();
		// 分析json得到坐标生成marker
		phraseJsonToMarker();
		// 根据得到的坐标话线
		drawLine();

	}

	public void drawLine() {
		if (LatLonPointList.size() == 1) {
			// 只有一个站点不能绘制线路之间返回marker
			mMap.addMarker((new MarkerOptions()).position(
					SearchPointConvert(LatLonPointList.get(0))).icon(
					BitmapDescriptorFactory.fromBitmap(getBitMap(1 + ""))));
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					SearchPointConvert(LatLonPointList.get(0)),(float) (zoomLevel+4)));
			return;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				routeHandler.sendMessage(Message.obtain(routeHandler,
						Constants.ROUTE_SEARCH_RESULT));
				LatLonPoint preStop;
				LatLonPoint nextStop;
				for (int i = 0; i < LatLonPointList.size() - 1; i++) {
					preStop = LatLonPointList.get(i);
					nextStop = LatLonPointList.get(i + 1);
					final Route.FromAndTo fromAndTo = new Route.FromAndTo(
							preStop, nextStop);
					try {
						routeResult = Route.calculateRoute(StopMap.this,
								fromAndTo, mode);
						if (routeResult != null || routeResult.size() > 0) {
							route = routeResult.get(0);
							if (route != null) {
								LatLng startPoint = SearchPointConvert(route
										.getStartPos());
								LatLng endPoint = SearchPointConvert(route
										.getTargetPos());
								if (i == 0) {
									mMap.addMarker((new MarkerOptions())
											.position(startPoint)
											.icon(BitmapDescriptorFactory
													.fromBitmap(getBitMap((i + 1)
															+ "")))
											.title(i + ""));
								}
								mMap.addMarker((new MarkerOptions())
										.position(endPoint)
										.icon(BitmapDescriptorFactory
												.fromBitmap(getBitMap((i + 2)
														+ "")))
										.title((i + 1) + ""));
								for (int i1 = 0; i1 < route.getStepCount(); i1++) {
									mMap.addPolyline((new PolylineOptions())
											.addAll(convertArrList(route
													.getStep(i1).getShapes()))
											.color(Color
													.argb(180, 54, 114, 227))
											.width(lineWidth));
								}
							}
						}
					} catch (AMapException e) {
						Message msg = new Message();
						msg.what = Constants.ROUTE_SEARCH_ERROR;
						msg.obj = e.getErrorMessage();
						routeHandler.sendMessage(msg);
					}

				}
				routeHandler.sendMessage(Message.obtain(routeHandler,
						Constants.DRAWLINE_FINISH));
			}
		});
		t.start();

	}

	private Handler routeHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.ROUTE_SEARCH_RESULT) {
				// mProgressDialog = new ProgressDialog(StopMap.this);
				// mProgressDialog.setTitle("正在绘制站点路线"); // 设置标题
				// mProgressDialog.setMessage("站点分布马上为您呈现，请耐心等待..."); //
				// 设置body信息
				// mProgressDialog.show();

			} else if (msg.what == Constants.ROUTE_SEARCH_ERROR) {
				showToast((String) msg.obj);
			} else if (msg.what == Constants.DRAWLINE_FINISH) {
				// 将视图放大到所有maker都能看到
				loadMarker();
				// mProgressDialog.dismiss();
			}
		}
	};

	public void showToast(String showString) {
		Toast.makeText(getApplicationContext(), showString, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * 工具方法，将搜索得到的LatLonPoint转成latLng 才能添加到地图上
	 * 
	 * @param latLonPoint
	 * @return
	 */
	private LatLng SearchPointConvert(LatLonPoint latLonPoint) {
		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
	}

	/**
	 * 工具方法， 将一个segment shaps 转化成map的LatLng list 方便添加到地图
	 * 
	 * @param shapes
	 * @return
	 */
	private ArrayList<LatLng> convertArrList(LatLonPoint[] shapes) {
		ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
		for (LatLonPoint point : shapes) {
			LatLng latLngTemp = SearchPointConvert(point);
			lineShapes.add(latLngTemp);
		}
		return lineShapes;
	}

	private void phraseJsonToMarker() {
		try {
			stopsJsonArray = new JSONArray(stopslistString);
			for (int i = 0; i < stopsJsonArray.length(); i++) {
				stopJson = stopsJsonArray.getJSONObject(i);
				double longitude = stopJson.getJSONArray("locate").getDouble(0);
				double latitude = stopJson.getJSONArray("locate").getDouble(1);
				Log.i("locate", longitude + " " + latitude);
				LatLonPoint llp = new LatLonPoint(longitude, latitude);
				LatLonPointList.add(llp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			showToast(e.getMessage());
		}
	}

	public void loadMarker() {
		// 设置所有maker显示在View中
		Builder builder = new Builder();
		for (int i = 0; i < LatLonPointList.size(); i++) {
			builder.include(SearchPointConvert(LatLonPointList.get(i)));
		}
		try {
			LatLngBounds bounds = builder.build();
			mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
					zoomPadding));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		Log.i("marker",
				"title:" + arg0.getTitle() + " snippet:" + arg0.getSnippet());

		Intent it = new Intent(StopMap.this, MapPopup.class);
		it.putExtra("stopsJarray", stopsJsonArray.toString());
		it.putExtra("position", arg0.getTitle());
		it.putExtra("type", "stop");
		startActivity(it);

		return false;
	}

	public void backClicked(View v) {
		this.finish();
	}

	private void init() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (AMapUtil.checkReady(this, mMap)) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mUiSettings = mMap.getUiSettings();
		mUiSettings.setAllGesturesEnabled(true);
		mUiSettings.setCompassEnabled(true);
		mUiSettings.setMyLocationButtonEnabled(false);
		mUiSettings.setScaleControlsEnabled(true);
		mUiSettings.setZoomControlsEnabled(true);
		mMap.setOnMarkerClickListener(this);

	}

	public Bitmap getBitMap(String text) {
		Bitmap bitmap = BitmapDescriptorFactory.fromResource(R.drawable.line)
				.getBitmap();
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight());
		Canvas canvas = new Canvas(bitmap);
		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(20f);
		textPaint.setColor(Color.WHITE);
		canvas.drawText(text, 15, 30, textPaint);// 设置bitmap上面的文字位置
		return bitmap;
	}

}
