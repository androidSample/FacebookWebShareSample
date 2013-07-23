package com.hthan.facebookwebsample;

import com.hthan.facebookWeb.DialogError;
import com.hthan.facebookWeb.DialogListener;
import com.hthan.facebookWeb.FacebookAction;
import com.hthan.facebookWeb.FacebookError;
import com.hthan.facebookWeb.FacebookShareListener;
import com.hthan.facebookWeb.FacebookWebView;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookWebView fbWebView = new FacebookWebView(this);
		setContentView(fbWebView);
		fbWebView.start(new String[] { "email", "publish_stream", "read_stream" }, dialogListener);
	}

	private DialogListener dialogListener = new DialogListener() {

		@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub

			FacebookAction.postToWall("http://www.huffingtonpost.com/theblog/archive/Bolton%20Smiley%20JPG.jpg", "caption", "description", "Url", "name", new FacebookShareListener() {
				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
							msgBox.setCancelable(false);
							msgBox.setMessage("Success").setPositiveButton("Check", null).show();
						}
					});
				}
				@Override
				public void onError(String msg) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
							msgBox.setCancelable(false);
							msgBox.setMessage("Error").setPositiveButton("Check", null).show();

						}
					});
				}
			});
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
