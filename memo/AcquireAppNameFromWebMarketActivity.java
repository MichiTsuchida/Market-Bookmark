package com.michitsuchida.marketfavoritter.main;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Web版のマーケットにアクセスして、強制的にアプリ名を取得するActivity
 * <title>タグの内容を無理矢理使うｗ
 */
public class AcquireAppNameFromWebMarketActivity extends Activity {

	// LOG TAG
	static final String LOG_TAG = "MarketBookmark";

	// アプリ名を格納する
	private String appName = "";
	// <title>タグの内容を格納する
	private String title = "";
	// Intentで受け取ったパッケージ名を格納する
	private String pkg = "";


	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.appname_market);

		// Intentの情報を取得する
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			pkg = extras.getCharSequence(
				com.michitsuchida.marketfavoritter.main.ReceiveMarketIntentActivity.EXTRA_PKG_NAME).toString();
			// パッケージ名を使用して、HTTPリクエストを投げて通信する
			doRequest(pkg);
		}
		// Extraがnullの場合は何もしないで空文字を返す
		Intent intent = new Intent();
		intent.putExtra(
				com.michitsuchida.marketfavoritter.main.ReceiveMarketIntentActivity.EXTRA_APP_NAME,
				appName);
		setResult(RESULT_OK, intent);

		// Activityを終了する
		finish();
	}

	/**
	 * HTTPリクエストを実行する
	 */
	private void doRequest(String pkg) {
		final String urlStr = "https://market.android.com/details?id=" + pkg;
		Log.d(LOG_TAG, "URL for app is: " + urlStr);

		// HttpでGETするよ!!
		InputStream in = null;
		HttpsURLConnection https = null;
		try {
			// URLにHTTP接続
			URL url = new URL(urlStr);
			https = (HttpsURLConnection) url.openConnection();
			https.setRequestMethod("GET");
			https.connect();
			// データを取得
			in = https.getInputStream();

			// HTMLソースを読み出す
			String src = new String();
			byte[] line = new byte[4 * 1024];
			int size;
			while (true) {
				size = in.read(line);
				if (size <= 0) {
					break;
				}
				src += new String(line);

				// HTMLのソースから<title>タグだけをぶっこ抜く正規表現
				Pattern pattern = Pattern.compile(".*<title>(.+)\\s-\\s.+</title>.*");
				Matcher matcher = pattern.matcher(src);
				if (matcher.find()) {
					title = matcher.group(1);
					Log.d(LOG_TAG, "App name: " + title);
					appName = title;
					break;
				}
			}
		} catch (Exception e) {
			// 例外発生時はスタックトレースを出力
			e.printStackTrace();
		} finally {
			// close処理はfinally内でtry-catchする
			try {
				if (https != null) {
					https.disconnect();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
