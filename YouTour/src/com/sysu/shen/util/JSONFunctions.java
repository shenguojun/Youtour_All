package com.sysu.shen.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JSONFunctions {

	/**
	 * 从服务端获取json，并保存缓存到本地
	 * 
	 * @param URLString
	 * @return
	 */
	public static JSONArray getJsonFromNetwork(Context context, String URLString) {

		String result = "";
		JSONArray jarray = null;
		// http
		try {
			// 从网络获取json

			URL url = new URL(URLString);
			Log.i("jsonrequesturl", url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());

			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));

			Log.v("jsonfucntion", "get connect to server");
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		try {
			jarray = new JSONArray(result);
		} catch (Exception e) {
			Log.e("jsonfucntion",
					"Error parsing data from network: " + e.toString());
		}

		// 把刚刚得到的json缓存到本地
		try {
			File cacheDir;
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				cacheDir = new File(android.os.Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ GlobalConst.SDCARD_JSONCACHE_DIR);
			else
				cacheDir = context.getCacheDir();
			if (!cacheDir.exists())
				cacheDir.mkdirs();
			File file = new File(cacheDir, String.valueOf(URLString.hashCode()));
			FileOutputStream f = new FileOutputStream(file);
			InputStream is1 = new ByteArrayInputStream(result.getBytes("UTF-8"));
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is1.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}

			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jarray;
	}

	/**
	 * 从文件缓存中读取json
	 * 
	 * @param context
	 * @param URLString
	 * @return
	 */
	public static JSONArray getJSONFromFile(Context context, String URLString) {
		String result = "";
		JSONArray jarray = null;
		try {
			File cacheDir;
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED))
				cacheDir = new File(android.os.Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ GlobalConst.SDCARD_JSONCACHE_DIR);
			else
				cacheDir = context.getCacheDir();
			if (!cacheDir.exists())
				cacheDir.mkdirs();
			File file = new File(cacheDir, String.valueOf(URLString.hashCode()));
			if (!file.exists())
				return null;
			FileInputStream stream = new FileInputStream(file);

			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			/* Instead of using default, pass in a decoder. */
			result = Charset.defaultCharset().decode(bb).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			jarray = new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jarray;
	}

	public static HashMap<String, String> praseJSONToMap(JSONObject line) {
		HashMap<String, String> lineMap = new HashMap<String, String>();
		try {
			lineMap.put("address", line.getString("mapAddress"));
			lineMap.put("title", line.getString("lineName"));
			lineMap.put("thumbnail", line.getString("coverThumbnail"));
			Float score = Float.parseFloat(line.getString("totalScore"))
					/ Float.parseFloat(line.getString("totalPeople"));
			lineMap.put("score", score / 2 + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lineMap;

	}
}