package com.sysu.shen.youtour;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MytourFragment extends Fragment {

	TabHost tabHost;
	TabWidget tabWidget;
	LinearLayout bottom_layout;
	int CURRENT_TAB = 0;
	MytourTabDownload downloadFragment;
	MytourTabCloud cloudFragment;
	MytourTabFavorite favoriteFragment;
	android.support.v4.app.FragmentTransaction ft;
	LinearLayout tabIndicator1, tabIndicator2, tabIndicator3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//获取fragment视图
		View v = inflater.inflate(R.layout.main_tab_mytour, container, false);
		//填充tab内容
		findTabView(v);
		tabHost.setup();
		//设置tab监听器
		TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				//获取子fragmentManager
				android.support.v4.app.FragmentManager fm = getChildFragmentManager();
				downloadFragment = (MytourTabDownload) fm
						.findFragmentByTag("download");
				cloudFragment = (MytourTabCloud) fm.findFragmentByTag("cloud");
				favoriteFragment = (MytourTabFavorite) fm
						.findFragmentByTag("favorite");
				ft = fm.beginTransaction();

				if (downloadFragment != null)
					ft.detach(downloadFragment);

				if (cloudFragment != null)
					ft.detach(cloudFragment);

				if (favoriteFragment != null)
					ft.detach(favoriteFragment);

				if (tabId.equalsIgnoreCase("download")) {
					isTabDownload();
					CURRENT_TAB = 1;

				} else if (tabId.equalsIgnoreCase("cloud")) {
					isTabCloud();
					CURRENT_TAB = 2;

				} else if (tabId.equalsIgnoreCase("favorite")) {
					isTabFavorite();
					CURRENT_TAB = 3;
				} else {
					switch (CURRENT_TAB) {
					case 1:
						isTabDownload();
						break;
					case 2:
						isTabCloud();
						break;
					case 3:
						isTabFavorite();
						break;
					default:
						isTabDownload();
						break;
					}

				}
				ft.commit();
			}

			private void isTabDownload() {
				if (downloadFragment == null) {
					ft.add(R.id.realtabcontent, new MytourTabDownload(),
							"download");
				} else {
					ft.attach(downloadFragment);
				}

			}

			private void isTabCloud() {
				if (cloudFragment == null) {
					ft.add(R.id.realtabcontent, new MytourTabCloud(), "cloud");
				} else {
					ft.attach(cloudFragment);
				}

			}

			private void isTabFavorite() {
				if (favoriteFragment == null) {
					ft.add(R.id.realtabcontent, new MytourTabFavorite(),
							"favorite");
				} else {
					ft.attach(favoriteFragment);
				}

			}

		};
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(tabChangeListener);
		//初始化tab
		initTab(v);
		tabHost.setCurrentTab(0);
		return v;

	}

	private void initTab(View v) {
		TabHost.TabSpec tSpecHome = tabHost.newTabSpec("download");
		tSpecHome.setIndicator(tabIndicator1);
		tSpecHome.setContent(new DummyTabContent(v.getContext()));
		tabHost.addTab(tSpecHome);

		TabHost.TabSpec tSpecWall = tabHost.newTabSpec("cloud");
		tSpecWall.setIndicator(tabIndicator2);
		tSpecWall.setContent(new DummyTabContent(v.getContext()));
		tabHost.addTab(tSpecWall);

		TabHost.TabSpec tSpecCamera = tabHost.newTabSpec("favorite");
		tSpecCamera.setIndicator(tabIndicator3);
		tSpecCamera.setContent(new DummyTabContent(v.getContext()));
		tabHost.addTab(tSpecCamera);

	}

	private void findTabView(View v) {
		tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) v.findViewById(android.R.id.tabs);
		LinearLayout layout = (LinearLayout) tabHost.getChildAt(0);
		TabWidget tw = (TabWidget) layout.getChildAt(1);

		tabIndicator1 = (LinearLayout) LayoutInflater.from(v.getContext())
				.inflate(R.layout.tourtab_indicator, tw, false);
		TextView tvTab1 = (TextView) tabIndicator1.getChildAt(1);
		ImageView ivTab1 = (ImageView) tabIndicator1.getChildAt(0);
		ivTab1.setBackgroundResource(R.drawable.list2);
		tvTab1.setText(R.string.download);

		tabIndicator2 = (LinearLayout) LayoutInflater.from(v.getContext())
				.inflate(R.layout.tourtab_indicator, tw, false);
		TextView tvTab2 = (TextView) tabIndicator2.getChildAt(1);
		ImageView ivTab2 = (ImageView) tabIndicator2.getChildAt(0);
		ivTab2.setBackgroundResource(R.drawable.cloud);
		tvTab2.setText(R.string.create);

		tabIndicator3 = (LinearLayout) LayoutInflater.from(v.getContext())
				.inflate(R.layout.tourtab_indicator, tw, false);
		TextView tvTab3 = (TextView) tabIndicator3.getChildAt(1);
		ImageView ivTab3 = (ImageView) tabIndicator3.getChildAt(0);
		ivTab3.setBackgroundResource(R.drawable.heart);
		tvTab3.setText(R.string.favorite);

	}

}