package com.hthan.facebookWeb;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FacebookWebView extends WebView {

	private DialogListener dialogListener;
	private ProgressDialog loadingDialog;
	private Context mContext;
	private static String mAccessToken = null;
	private static long mAccessExpires = 0;
	public FacebookWebView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		CookieSyncManager.createInstance(context);
		init(context);
		mContext = context;
	}

	private void init(Context context) {
		WebSettings settings = getSettings();
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

		loadingDialog = new ProgressDialog(context);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setMessage("Loading..");
	}

	public void start(String[] permissions, DialogListener dialogListener) {
		this.dialogListener = dialogListener;
		new FBTask(permissions).execute();
	}

	public static String getmAccessToken() {
		return mAccessToken;
	}

	public static void setmAccessToken(String mAccessToken) {
		FacebookWebView.mAccessToken = mAccessToken;
	}

	public static long getmAccessExpires() {
		return mAccessExpires;
	}

	public static void setAccessExpiresIn(String expiresIn) {
		if (expiresIn != null) {
			long expires = expiresIn.equals("0") ? 0 : System.currentTimeMillis() + Long.parseLong(expiresIn) * 1000L;
			setmAccessExpires(expires);
		}
	}
	
	public static void setmAccessExpires(long mAccessExpires) {
		FacebookWebView.mAccessExpires = mAccessExpires;
	}
	
	public static boolean isSessionValid() {
		return (getmAccessToken() != null) && ((getmAccessExpires() == 0) || (System.currentTimeMillis() < getmAccessExpires()));
	}

	private class FBTask extends AsyncTask<Void, Void, Void> {
		private String loginUrl;

		public FBTask(String[] permissions) {
			// TODO Auto-generated constructor stub
			Bundle params = new Bundle();
			if (permissions.length > 0) {
				params.putString("scope", TextUtils.join(",", permissions));
			}
			String endpoint = FBConstants.DIALOG_BASE_URL + FBConstants.LOGIN_ACTION;
			params.putString("display", "touch");
			params.putString("redirect_uri", FBConstants.REDIRECT_URI);
			if (isSessionValid()) {
				params.putString(FBConstants.TOKEN, getmAccessToken());
			}
			params.putString("type", "user_agent");
			params.putString("client_id", FBConstants.APPID);
			loginUrl = endpoint + "?" + Util.encodeUrl(params);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			FacebookWebView.this.setWebViewClient(new FbWebViewClient());
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			FacebookWebView.this.loadUrl(loginUrl);
			return null;
		}

	}

	private class FbWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("Facebook-WebView", "Redirect URL: " + url);
			if (url.startsWith(FBConstants.REDIRECT_URI)) {
				Bundle values = Util.parseUrl(url);

				String error = values.getString("error");
				if (error == null) {
					error = values.getString("error_type");
				}

				if (error == null) {
					CookieSyncManager.getInstance().sync();
					setmAccessToken(values.getString(FBConstants.TOKEN));
					setAccessExpiresIn(values.getString(FBConstants.EXPIRES));
					if (isSessionValid()) {
						Log.d("setting", "Login Success! access_token=" + getmAccessToken() + " expires=" + getmAccessExpires());
						dialogListener.onComplete(values);
					} else {
						dialogListener.onFacebookError(new FacebookError("Failed to receive access token."));
					}
				} else if (error.equals("access_denied") || error.equals("OAuthAccessDeniedException")) {
					dialogListener.onCancel();
				} else {
					dialogListener.onFacebookError(new FacebookError(error));
				}

				FacebookWebView.this.loadUrl("about:blank");
				return true;
			} else if (url.startsWith(FBConstants.CANCEL_URI)) {
				dialogListener.onCancel();
				FacebookWebView.this.loadUrl("about:blank");
				return true;
			} else if (url.contains(FBConstants.DISPLAY_STRING)) {
				return false;
			}
			// launch non-dialog URLs in a full browser
			getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			dialogListener.onError(new DialogError(description, errorCode, failingUrl));
			FacebookWebView.this.loadUrl("about:blank");
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d("Facebook-WebView", "Webview loading URL: " + url);
			super.onPageStarted(view, url, favicon);
			loadingDialog.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			loadingDialog.dismiss();
			/*
			 * Once webview is fully loaded, set the mContent background to be
			 * transparent and make visible the 'x' image.
			 */
		}
	}
	
	public static String request(Bundle parameters) throws MalformedURLException, IOException {
		if (!parameters.containsKey("method")) {
			throw new IllegalArgumentException("API method must be specified. " + "(parameters must contain key \"method\" and value). See" + " http://developers.facebook.com/docs/reference/rest/");
		}
		return request(null, parameters, "GET");
	}
	public static String request(String graphPath, Bundle params, String httpMethod) throws FileNotFoundException, MalformedURLException, IOException {
		params.putString("format", "json");
		if (isSessionValid()) {
			params.putString(FBConstants.TOKEN, getmAccessToken());
		}
		String url = (graphPath != null) ? FBConstants.GRAPH_BASE_URL + graphPath : FBConstants.RESTSERVER_URL;
		return Util.openUrl(url, httpMethod, params);
	}
}
