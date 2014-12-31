package org.zarroboogs.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lib.org.zarroboogs.utils.net.BroserContent;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

import android.util.Log;

public class HttpGetHelper {

	public boolean isLogIn(BroserContent broserContent, String url, String cookie) {
		String content = "'is_login':\"1\"";
		CloseableHttpClient httpClient = broserContent.getHttpClient();
		// http://widget.weibo.com/dialog/PublishWeb.php?button=public
		Header[] loginHeaders = {
				new BasicHeader("Host", "widget.weibo.com"),
				new BasicHeader("Cache-Control", "max-age=0"),
				new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
				new BasicHeader("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.122 Safari/537.36"),
				new BasicHeader("Referer", "http://widget.weibo.com/dialog/PublishWeb.php?button=public"),
				new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"), new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"), };

		HttpGet httpGet = HttpGetFactory.createHttpGet(url, loginHeaders);
		httpGet.addHeader("Cookie", cookie);

		CloseableHttpResponse logInResponse = null;
		String allResponse = "";
		boolean isSuccess = false;
		try {
			logInResponse = httpClient.execute(httpGet);
			HttpEntity mEntity = logInResponse.getEntity();
			allResponse = mEntity.getContent().toString();
			if (mEntity != null) {
				InputStream in;
				try {
					in = mEntity.getContent();
					String str = "";
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					while ((str = br.readLine()) != null) {
						allResponse += str + "\n";
					}

				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error IllegalStateException----");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error IOException");
				}
			}

			Log.d("RES_CODE_LogIN: ", "" + logInResponse.getStatusLine().getStatusCode() + "result: " + allResponse);
			if (logInResponse.getStatusLine().getStatusCode() == 200) {
				if (allResponse.contains(content)) {
					isSuccess = true;
				} else {
					isSuccess = false;
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		} finally {
			try {
				if (logInResponse != null) {
					logInResponse.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return isSuccess;
	}

	public String getWeiboWeiba(BroserContent broserContent) {
	    CloseableHttpClient httpClient = broserContent.getHttpClient();
	    
		// http://appsrc.sinaapp.com/
		String url = "http://appsrc.sinaapp.com/";
		
		Header[] loginHeaders = {
				new BasicHeader("Cache-Control", "max-age=0"),
				new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
				new BasicHeader("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.122 Safari/537.36"),
				new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"), new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"), };

		HttpGet httpGet = HttpGetFactory.createHttpGet(url, loginHeaders);

		CloseableHttpResponse logInResponse = null;
		String allResponse = "";
		boolean isSuccess = false;
		try {
			logInResponse = httpClient.execute(httpGet);
			HttpEntity mEntity = logInResponse.getEntity();
			if (mEntity != null) {
				InputStream in;
				try {
					in = mEntity.getContent();
					String str = "";
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
					while ((str = br.readLine()) != null) {
						allResponse += str;
					}

				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error IllegalStateException----");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error IOException");
				}
			}

			Log.d("RES_CODE: ", "" + logInResponse.getStatusLine().getStatusCode() + "result: " + allResponse);
			if (logInResponse.getStatusLine().getStatusCode() == 200) {
				isSuccess = true;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isSuccess = false;
		} finally {
			try {
				if (logInResponse != null) {
					logInResponse.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		String result = null;
		try {
			result = allResponse.split("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")[1];
		} catch (Exception e) {
			// TODO: handle exception
		}

		Log.d("RESULT", "" + result);
		return result;
	}
}
