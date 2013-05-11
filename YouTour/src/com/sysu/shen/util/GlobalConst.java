package com.sysu.shen.util;

public class GlobalConst {
	public static final String SDCARD_JSONCACHE_DIR = "/youtour/jsoncache";
	public static final String SDCARD_IMAGECACHE_DIR = "/youtour";
	public static final String[] TOPIC_ITEMS = new String[] { "全部主题", "生活",
			"自然", "人文", "历史" };
	public static final String[] SORT_METHORD = new String[] { "默认排序", "按旅程时间",
			"按旅程距离", "价格由低到高", "价格由高到低", "最新发布" };
	public static final String EXPLORE_PLACE = "roughPlace";
	public static final String EXPLORE_TOPIC = "throughTopic";
	public static final String HOST = "http://103.31.20.60:3000";
	// public static final String HOST = "http://172.18.219.82:3000/";
	// public static String EXPLORE_URL = HOST + "/androidtest";
	public static final String URL_HEADER_ADDTOP = HOST
			+ "/browsebyaddresstopic?";
	public static final String URL_HAEDER_ADD = HOST + "/browsebyaddress?";
	public static final String URL_HAEDER_TOP = HOST + "/browsebytopic?";
	public static final String URL_HAEDER_ALL = HOST + "/browseallbytime?";
	public static final String URL_HAEDER_LOC = HOST + "/browsebylocation?";
	public static final String URL_APP_UPDATE = "https://dl.dropboxusercontent.com/u/109241536/update2.json";

}