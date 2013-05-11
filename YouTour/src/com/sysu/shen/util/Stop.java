package com.sysu.shen.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Stop {
	// 站点顺序
	private int num;
	// 站点名称
	private String stopName;
	// 站点描述
	private String stopDes;
	// 站点坐标 经度，纬度
	private double locate[] = new double[2];
	//站点图片
	private String stopThumbnail;
	//站点音频
	private String stopAudio;
	//站点顺序
	private String imageOrder;
	//站点图片
	private ArrayList<String> stopImages = new ArrayList<String>();

	//站点构造函数
	public Stop(JSONObject stop) {
		try {
			num = stop.getInt("num");
			stopName = stop.getString("stopName");
			stopDes = stop.getString("stopDes");
			JSONArray locatArray = stop.getJSONArray("locate");
			locate[0] = locatArray.getDouble(0);
			locate[0] = locatArray.getDouble(1);
			stopThumbnail = stop.getString("stopThumbnail");
			stopAudio = stop.getString("stopAudio");
			imageOrder = stop.getString("imageOrder");
			JSONArray imagesArray = stop.getJSONArray("stops");
			for (int i = 0; i < imagesArray.length(); i++) {
				stopImages.add(imagesArray.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
