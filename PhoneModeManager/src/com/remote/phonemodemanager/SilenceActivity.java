package com.remote.phonemodemanager;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class SilenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_silence);
		
		loadSilenceMode();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.silence, menu);
		return true;
	}

	public void onToggle_Silence(View view) {
		// Fires whenever the silence button is toggeled.
		saveSilenceMode();
	}
	
	public void onClick_TestBack(View view) {
		// Fires whenever the silence button is toggeled.
		this.finish();
	}
	
	public void loadSilenceMode() {
		// Checks what the silence mode is and sets the button
		ToggleButton silenceMode = (ToggleButton) findViewById(R.id.btn_TestToggle);
		AudioManager soundSettings = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		switch (soundSettings.getRingerMode()) {
		    case AudioManager.RINGER_MODE_SILENT:
		        Log.i("SilenceMode","Silent mode");
		        silenceMode.setChecked(true);
		        break;
		    case AudioManager.RINGER_MODE_VIBRATE:
		        Log.i("SilenceMode","Vibrate mode");
		        silenceMode.setChecked(true);
		        break;
		    case AudioManager.RINGER_MODE_NORMAL:
		        Log.i("SilenceMode","Normal mode");
		        silenceMode.setChecked(false);
		        break;
		}
	}
	
	public void saveSilenceMode() {
		// Checks what the silence mode is and sets the button
		ToggleButton silenceMode = (ToggleButton) findViewById(R.id.btn_TestToggle);
		AudioManager soundSettings = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		if (silenceMode.isChecked()) {
			soundSettings.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
		else {
			soundSettings.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}
}
