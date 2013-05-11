package com.sysu.shen.youtour;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sysu.shen.util.GlobalConst;
import com.sysu.shen.util.JSONFunctions;
import com.sysu.shen.util.Myadapter;
import com.sysu.shen.youtour.MyListView.OnRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ExploreMain extends Activity {
	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	private ViewMiddle viewMiddle;
	private ViewRight viewRight;
	private Boolean isFromPlace = false;
	private Boolean isFromTopic = false;
	private String URLString = "";
	private String URLStringAddress = "address=";
	private String URLStringTopic = "topic=";
	private String URLStringBegin = "beg=";
	private String URLStringEnd = "end=";
	private String address = "国内全部";
	private String topic = "全部主题";
	private MyListView list;
	private Myadapter adapter;
	private JSONArray jarray;
	private final int NO_NETWORK = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.explore_main);
		Bundle extras = getIntent().getExtras();
		isFromPlace = extras.getBoolean(GlobalConst.EXPLORE_PLACE);
		isFromTopic = extras.getBoolean(GlobalConst.EXPLORE_TOPIC);
		initView();
		initVaule();
		initListener();
		//初始加载
		URLString = GlobalConst.URL_HAEDER_ALL + URLStringBegin + "0" + "&"
				+ URLStringEnd + "25";
		// 异步更新列表
		new GetJSONAsynTack(this).execute(URLString);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				showPopup();
			}
		}, 100);

	}

	// 处理线程中抛出的massage
	private Handler mhandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NO_NETWORK:
				Toast.makeText(ExploreMain.this, "连接网络才能看到更多哦！",
						Toast.LENGTH_LONG).show();
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
		protected Void doInBackground(String... strings) {
			String URLString = strings[0];

			// 判断是否联网
			final ConnectivityManager conMgr = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {
				jarray = JSONFunctions.getJsonFromNetwork(activity, URLString);
			} else {
				Message m = new Message();
				m.what = NO_NETWORK;
				mhandle.sendMessage(m);
				jarray = JSONFunctions.getJSONFromFile(activity, URLString);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			JSONObject line = null;
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			if (jarray != null) {
				Log.v("log_tag", "jarray.length():" + jarray.length());
				try {
					for (int i = 0; i < jarray.length(); i++) {
						line = jarray.getJSONObject(i);
						HashMap<String, String> lineMap = JSONFunctions
								.praseJSONToMap(line);
						mylist.add(lineMap);
					}
				} catch (Exception e) {
					Log.v("log_tag", "analyseJsonexception:" + e.toString());
				}
				adapter = new Myadapter(activity, mylist);
				list = (MyListView) findViewById(R.id.list);
				list.setAdapter(adapter);
				list.setonRefreshListener(new OnRefreshListener() {

					public void onRefresh() {
						new GetJSONAsynTackList(ExploreMain.this)
								.execute(URLString);
					}
				});

				// Click event for single list row
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent it = new Intent(ExploreMain.this, LineMain.class);
						try {
							it.putExtra("lineString",
									jarray.getJSONObject(position - 1)
											.toString());
						} catch (JSONException e) {
							Log.v("exploremain",
									"get onelinejsonobject exception:"
											+ e.toString());
						}
						startActivity(it);

					}
				});

			}
			super.onPostExecute(result);
		}
	}

	private class GetJSONAsynTackList extends AsyncTask<String, Void, Void> {

		public Activity activity;

		public GetJSONAsynTackList(Activity activity2) {
			activity = activity2;
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
				Message m = new Message();
				m.what = NO_NETWORK;
				mhandle.sendMessage(m);
				jarray = JSONFunctions.getJSONFromFile(activity, URLString);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			JSONObject line = null;
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			if (jarray != null) {
				Log.v("log_tag", "jarray.length():" + jarray.length());
				try {
					for (int i = 0; i < jarray.length(); i++) {
						line = jarray.getJSONObject(i);
						HashMap<String, String> lineMap = JSONFunctions
								.praseJSONToMap(line);
						mylist.add(lineMap);
					}
				} catch (Exception e) {
					Log.v("log_tag", "analyseJsonexception:" + e.toString());
				}
				adapter = new Myadapter(activity, mylist);
				list.setAdapter(adapter);

			}
			list.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	protected void showPopup() {
		if (isFromPlace)
			expandTabView.virtualClickButton(0);
		if (isFromTopic)
			expandTabView.virtualClickButton(1);

	}

	private void initView() {

		expandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(this);
		viewMiddle = new ViewMiddle(this);
		viewRight = new ViewRight(this);

	}

	private void initVaule() {

		mViewArray.add(viewLeft);
		mViewArray.add(viewMiddle);
		mViewArray.add(viewRight);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("区域");
		mTextArray.add("主题");
		mTextArray.add("排序");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);
		expandTabView.setTitle(viewMiddle.getShowText(), 1);
		expandTabView.setTitle(viewRight.getShowText(), 2);

	}

	private void initListener() {

		viewMiddle.setOnSelectListener(new ViewMiddle.OnSelectListener() {

			@Override
			public void getValue(String distance, String showText) {
				// 填充请求URL
				topic = showText;
				onRefresh(viewMiddle, showText);
			}
		});

		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {

			@Override
			public void getValue(String showText) {
				// 填充请求URL
				address = showText;
				onRefresh(viewLeft, showText);
			}
		});

		viewRight.setOnSelectListener(new ViewRight.OnSelectListener() {

			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewRight, showText);
			}
		});

	}

	private void onRefresh(View view, String showText) {

		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		try {
			if (address.equals("国内全部") && !topic.equals("全部主题"))
				URLString = GlobalConst.URL_HAEDER_TOP + URLStringTopic
						+ URLEncoder.encode(topic, "UTF-8") + "&"
						+ URLStringBegin + "0" + "&" + URLStringEnd + "25";
			
			else if (topic.equals("全部主题") && !address.equals("国内全部"))
				URLString = GlobalConst.URL_HAEDER_ADD + URLStringAddress
						+ URLEncoder.encode(address, "UTF-8") + "&"
						+ URLStringBegin + "0" + "&" + URLStringEnd + "25";
			
			else if (topic.equals("全部主题") && address.equals("国内全部"))
				URLString = GlobalConst.URL_HAEDER_ALL + URLStringBegin + "0"
						+ "&" + URLStringEnd + "25";
			
			else if (!topic.equals("全部主题") && !address.equals("国内全部"))
				URLString = GlobalConst.URL_HEADER_ADDTOP + URLStringAddress
						+ URLEncoder.encode(address, "UTF-8") + "&"
						+ URLStringTopic + URLEncoder.encode(topic, "UTF-8")
						+ "&" + URLStringBegin + "0" + "&" + URLStringEnd
						+ "25";
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 更新列表
		new GetJSONAsynTack(this).execute(URLString);
		Toast.makeText(ExploreMain.this,
				"address:" + address + " topic:" + topic, Toast.LENGTH_SHORT)
				.show();
		Log.i("requestURL: ", URLString);

	}

	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void onBackPressed() {

		if (!expandTabView.onPressBack()) {
			finish();
		}

	}

	/**
	 * 点击邻近
	 * 
	 * @param v
	 */
	public void nearmeClicked(View v) {
		Toast.makeText(this, "nearmebutton clicked", Toast.LENGTH_SHORT).show();
		// Intent it = new Intent(ExploreMain.this,
		// com.sysu.shen.util.GoogleMap.class);
		// startActivity(it);
	}

	/**
	 * 点击返回
	 * 
	 * @param v
	 */
	public void backClicked(View v) {
		expandTabView.onPressBack();
		this.finish();
	}
}
