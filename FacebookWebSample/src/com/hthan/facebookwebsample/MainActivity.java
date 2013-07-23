package com.hthan.facebookwebsample;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.hthan.facebookWeb.DialogError;
import com.hthan.facebookWeb.DialogListener;
import com.hthan.facebookWeb.FacebookAction;
import com.hthan.facebookWeb.FacebookError;
import com.hthan.facebookWeb.FacebookShareListener;
import com.hthan.facebookWeb.FacebookWebView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final FacebookWebView fbWebView = (FacebookWebView)findViewById(R.id.facebookWebView1);
		findViewById(R.id.login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fbWebView.start(new String[] { "email", "publish_stream", "read_stream" }, dialogListener);
			}
		});
		
		findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					FacebookAction.logout(MainActivity.this);
					AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
					msgBox.setCancelable(false);
					msgBox.setMessage("Logout Success").setPositiveButton("Check", null).show();
					fbWebView.loadUrl("about:blank");
				} catch (MalformedURLException e) {
					AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
					msgBox.setCancelable(false);
					msgBox.setMessage("Logout Error ,"+e).setPositiveButton("Check", null).show();
				} catch (IOException e) {
					AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
					msgBox.setCancelable(false);
					msgBox.setMessage("Logout Error ,"+e).setPositiveButton("Check", null).show();					e.printStackTrace();
				}
			}
		});
		
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

			FacebookAction.postToWall("http://www.huffingtonpost.com/theblog/archive/Bolton%20Smiley%20JPG.jpg", "caption", "description", "http://coevo.com.tw", "name", new FacebookShareListener() {
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
