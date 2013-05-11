package com.sysu.shen.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Line {

	// 线路名称
	private String lineName;
	// 线路简介
	private String lineSummary;
	// 线路封面
	private String coverThumbnail;
	// 地图类型
	private String mapType;
	// 线路地址描述
	private String mapAddress;
	// 线路坐标 经度，纬度
	private double locate[] = new double[2];
	// 线路介绍语言
	private String language;
	// 线路长度
	private String lineLength;
	// 线路主题
	private String topics[] = new String[3];
	// 线路价格
	private String price;
	// 线路作者
	private String author;
	// 作者头像
	private String authorImage;
	// 作者email
	private String authorEmail;
	// 作者简介
	private String authorBio;
	// 作者类型
	private String authorType;
	// 自定义主题
	private ArrayList<String> keyWords = new ArrayList<String>();
	// 线路交通
	private String traffics;
	// 线路注意事项
	private String cautions;
	// 线路介绍连接
	private String lineLinks;
	// 线路介绍视频
	private String lineVedio;
	// 线路总打分人数
	private float totalPeople;
	// 线路总打分
	private float totalScore;
	// 线路包含站点
	private ArrayList<Stop> stops = new ArrayList<Stop>();

	// 构造函数
	public Line(JSONObject line) {
		try {
			lineName = line.getString("lineName");
			lineSummary = line.getString("lineSummary");
			coverThumbnail = line.getString("coverThumbnail");
			mapType = line.getString("mapType");
			mapAddress = line.getString("mapAddress");
			JSONArray locatArray = line.getJSONArray("locate");
			locate[0] = locatArray.getDouble(0);
			locate[0] = locatArray.getDouble(1);
			language = line.getString("language");
			lineLength = line.getString("lineLength");
			JSONArray topicsArray = line.getJSONArray("topics");
			for (int i = 0; i < topicsArray.length(); i++) {
				topics[i] = topicsArray.getString(i);
			}
			price = line.getString(price);
			author = line.getString(author);
			authorImage = line.getString("authorImage");
			authorEmail = line.getString("authorEmail");
			authorBio = line.getString("authorBio");
			authorType = line.getString("authorType");
			keyWords = new ArrayList<String>();
			traffics = line.getString("traffics");
			cautions = line.getString("cautions");
			lineLinks = line.getString("lineLinks");
			lineVedio = line.getString("lineVedio");
			totalPeople = (float) line.getDouble("totalPeople");
			totalScore = (float) line.getDouble("totalScore");
			JSONArray stopsArray = line.getJSONArray("stops");
			for (int i = 0; i < stopsArray.length(); i++) {
				stops.add(new Stop(stopsArray.getJSONObject(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
