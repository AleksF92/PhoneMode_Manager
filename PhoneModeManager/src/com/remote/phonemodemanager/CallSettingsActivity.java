package com.remote.phonemodemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class CallSettingsActivity extends Activity {

	private boolean detectEnabled;
	private TextView textViewDetectState;
	private Button buttonToggleDetect;
	private Button buttonExit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_settings);
		
		textViewDetectState = (TextView) findViewById(R.id.textViewDetectState);
        buttonToggleDetect = (Button) findViewById(R.id.buttonDetectToggle);
        buttonToggleDetect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDetectEnabled(!detectEnabled);
			}
		});
        
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setDetectEnabled(false);
				CallSettingsActivity.this.finish();
			}
		});
	}
	
	private void setDetectEnabled(boolean enable) {
    	System.out.println("Service Clicked");
    	detectEnabled = enable;
        Intent intent = new Intent(this, CallDetectService.class);
    	if (enable) {
    		 // start detect service 
            startService(intent);
            
            buttonToggleDetect.setText("Turn off");
    		textViewDetectState.setText("Detecting");
    	}
    	else {
    		// stop detect service
    		stopService(intent);
    		
    		buttonToggleDetect.setText("Turn on");
    		textViewDetectState.setText("Not detecting");
    	}
    }

}
