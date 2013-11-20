package com.remote.phonemodemanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SetSilentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_silent);
		
        String items[] = {"Vibrate", "Silent", "Normal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner box = (Spinner) findViewById(R.id.box_SilentSetting);
        box.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_silent, menu);
		return true;
	}

	public void onClick_SilentBack(View view) {
		// Fires whenever the silence button is toggeled.
		this.finish();
	}
	
	public void onClick_SilentContinue(View view) {
		// Fires whenever the silence button is toggeled.
		// this.finish();
	}
}
