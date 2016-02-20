package jp.gr.java_conf.ya.weather; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private final static int MP = LinearLayout.LayoutParams.MATCH_PARENT;
	private EditText editText;
	private String text = null;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//レイアウトの生成
		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);

		//エディットテキストの生成
		editText = new EditText(this);
		editText.setText("", EditText.BufferType.NORMAL);
		editText.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
		layout.addView(editText);

		//		Thread thread1 = new Thread(new Runnable() {
		//			public void run() {
		//
		//				// http://weather.livedoor.com/forecast/rss/primary_area.xml
		//				String URL =
		//						"http://weather.livedoor.com/forecast/rss/primary_area.xml";
		//				try {
		//					text = new String(http2data(URL));
		//				} catch (Exception e) {
		//				}
		//				handler.post(new Runnable() {
		//					public void run() {
		//						if (text != null) {
		//							editText.setText(text, TextView.BufferType.EDITABLE);
		//						} else {
		//							Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
		//						}
		//					}
		//				});
		//			}
		//		});
		//		thread1.start();

		Thread thread2 = new Thread(new Runnable() {
			public void run() {

				// http://weather.livedoor.com/weather_hacks/webservice
				String URL =
						"http://weather.livedoor.com/forecast/webservice/json/v1?city=" + "120010";
				try {
					text = new String(http2data(URL));
				} catch (Exception e) {
				}

				//				handler.post(new Runnable() {
				//					public void run() {
				//						if (text != null) {
				//							editText.setText(text, TextView.BufferType.EDITABLE);
				//						} else {
				//							Toast.makeText(MainActivity.this, "null", Toast.LENGTH_SHORT).show();
				//						}
				//					}
				//				});

				JSONObject rootObject = null;
				try {
					rootObject = new JSONObject(text);
				} catch (JSONException e) {
				}

				String rslt = "";

				try {
					String city = rootObject.getJSONObject("location").getString("city");

					Log.d("JSON", "city: " + city);

					rslt += " city: " + city;
				} catch (JSONException e) {
				}

				JSONArray foreArray = null;
				try {
					foreArray = rootObject.getJSONArray("forecasts");
				} catch (JSONException e) {
				}

				try {
					for (int i = 0; i < foreArray.length(); i++) {
						JSONObject foreObject = foreArray.getJSONObject(i);

						Log.d("JSON", "dateLabel: " + foreObject.getString("dateLabel")
								+ " telop: " + foreObject.getString("telop"));

						rslt += " dateLabel: " + foreObject.getString("dateLabel")
								+ " telop: " + foreObject.getString("telop");

						try {
							Log.d("JSON", "min: "
									+ foreObject.getJSONObject("temperature").getJSONObject("min").getString("celsius"));

							rslt += " min: "
									+ foreObject.getJSONObject("temperature").getJSONObject("min").getString("celsius");
						} catch (JSONException e1) {
						}

						try {
							Log.d("JSON", "max: "
									+ foreObject.getJSONObject("temperature").getJSONObject("max").getString("celsius"));

							rslt += " max: "
									+ foreObject.getJSONObject("temperature").getJSONObject("max").getString("celsius");
						} catch (JSONException e1) {
						}
					}
				} catch (JSONException e) {
				}

				final String rslt2 = rslt;
				handler.post(new Runnable() {
					public void run() {
						if (rslt2 != null) {
							editText.setText(rslt2, TextView.BufferType.EDITABLE);
						}
					}
				});
			}
		});
		thread2.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//HTTP通信
	public static byte[] http2data(String path) throws Exception {
		byte[] w = new byte[1024];
		HttpURLConnection c = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			//HTTP接続のオープン
			URL url = new URL(path);
			c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.connect();
			in = c.getInputStream();

			//バイト配列の読み込み
			out = new ByteArrayOutputStream();
			while (true) {
				int size = in.read(w);
				if (size <= 0)
					break;
				out.write(w, 0, size);
			}
			out.close();

			//HTTP接続のクローズ
			in.close();
			c.disconnect();
			return out.toByteArray();
		} catch (Exception e) {
			try {
				if (c != null)
					c.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e2) {
			}
			throw e;
		}
	}

}
