package com.remote.phonemodemanager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class NewEventActivity extends Activity {

	SeekBar rangeBar;
	TextView rangeTxt;
	TextView typeTxt;
	Spinner box;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_event);
		
		rangeTxt = (TextView) findViewById(R.id.txt_EventRange);
		rangeTxt.setText(getResources().getString(R.string.event_range) + " 20");
		
		typeTxt = (TextView) findViewById(R.id.txt_EventType);
		
        String items[] = {"Silent mode", "Ringtones", "Wifi / 3G"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        box = (Spinner) findViewById(R.id.box_EventTypes);
        box.setAdapter(adapter);
		
		rangeBar = (SeekBar) findViewById(R.id.bar_EventRange);
		rangeBar.setMax(180);
		rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { 
		   @Override 
		   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { 
		    	// Log the progress
		    	int min = 20;
		    	int cur = min + progress;
		    	Log.i("DragBar", "Progress is: " + cur);
		    	
		    	rangeTxt.setText(getResources().getString(R.string.event_range) + " " + cur);
		   } 

		   @Override 
		   public void onStartTrackingTouch(SeekBar seekBar) { 
		    // TODO Auto-generated method stub 
		   } 

		   @Override 
		   public void onStopTrackingTouch(SeekBar seekBar) { 
		    // TODO Auto-generated method stub 
		   }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_event, menu);
		return true;
	}

	public void onClick_EventBack(View view) {
		// Fires whenever the silence button is toggeled.
		this.finish();
	}
	
	public void onClick_EventContinue(View view) {
		// Fires whenever the silence button is toggeled.
		//Create the new event
		if (box.getSelectedItem() == "Silent mode") {
			Intent intent = new Intent(this, SetSilentActivity.class);
			startActivity(intent);
		}
	}
}
