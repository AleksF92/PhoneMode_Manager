package com.remote.phonemodemanager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick_Map(View view) {
		// Fires whenever the map button is clicked.
		Intent intent = new Intent(this, LocationActivity.class);
		startActivity(intent);
	}
	
	public void onClick_Silence(View view) {
		// Fires whenever the silence button is clicked.
		Intent intent = new Intent(this, SilenceActivity.class);
		startActivity(intent);
	}
}
