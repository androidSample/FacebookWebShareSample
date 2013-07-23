package com.hthan.facebookWeb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


import android.content.Context;
import android.os.Bundle;

public class FacebookAction {
	public static String logout(Context context) throws MalformedURLException, IOException {
		Util.clearCookies(context);
		Bundle b = new Bundle();
		b.putString("method", "auth.expireSession");
		String response = FacebookWebView.request(b);
		FacebookWebView.setmAccessToken(null);
		FacebookWebView.setmAccessExpires(0);
		return response;
	}

	public static boolean isSessionValid() {
		return (FacebookWebView.getmAccessToken() != null) && ((FacebookWebView.getmAccessExpires() == 0) || (System.currentTimeMillis() < FacebookWebView.getmAccessExpires()));
	}

	public static void postToWall(String imgURL, String caption, String description, String url,String name,final FacebookShareListener listener) {
		Bundle params = new Bundle();
		params.putString("picture", imgURL);
		params.putString("link", url);
		params.putString("name", name);
		params.putString("caption", caption);
		params.putString("description", description);
		request("me/feed", params, "POST", listener);
		
	}

	private static void request(final String graphPath, final Bundle parameters, final String httpMethod, final FacebookShareListener listener) {
		new Thread() {
			@Override
			public void run() {
				try {
					String resp = FacebookWebView.request(graphPath, parameters, httpMethod);
					listener.onSuccess(resp);
				} catch (FileNotFoundException e) {
					listener.onError(e.getMessage());
				} catch (MalformedURLException e) {
					listener.onError(e.getMessage());
				} catch (IOException e) {
					listener.onError(e.getMessage());
				}
			}
		}.start();
	}
}
