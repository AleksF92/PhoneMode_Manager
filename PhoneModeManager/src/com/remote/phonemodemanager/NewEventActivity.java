package com.remote.phonemodemanager;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class NewEventActivity extends Activity {

	SeekBar rangeBar;
	TextView rangeTxt;
	Spinner boxSilentMode;
	Spinner boxInternet;
	int iconSelected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("INTENT IN", "EventTitle: " + getIntent().getStringExtra("EventTitle"));
		Log.i("INTENT IN", "EventIcon: " + getIntent().getIntExtra("EventIcon", R.drawable.img_pin_red));
		Log.i("INTENT IN", "EventRange: " + getIntent().getIntExtra("EventRange", 20));
		Log.i("INTENT IN", "EventSilenceOption: " + getIntent().getIntExtra("EventSilenceOption", 0));
		Log.i("INTENT IN", "EventInternetOption: " + getIntent().getIntExtra("EventInternetOption", 0));
		Log.i("INTENT IN", "EventRingtone: " + getIntent().getStringExtra("EventRingtone"));
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_event);
		
		//Set title to intent data
		EditText eventTitle = (EditText) findViewById(R.id.txt_EventTitle);
		eventTitle.setText(getIntent().getStringExtra("EventTitle"));
		
		//Set image to intent data
		ImageView eventIcon = (ImageView) findViewById(R.id.img_Icon);
		iconSelected = getIntent().getIntExtra("EventIcon", R.drawable.img_pin_red);
		eventIcon.setImageResource(iconSelected);
		
		//Set range text to minimum
		int curRange = getIntent().getIntExtra("EventRange", 20);
		rangeTxt = (TextView) findViewById(R.id.txt_EventRange);
		rangeTxt.setText(getResources().getString(R.string.event_range) + " " + curRange);
		
		//Create items for silent dropdown
		String items1[] = {"NOT SET", "Vibrate", "Silent", "Sound on"};
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items1);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		boxSilentMode = (Spinner) findViewById(R.id.box_EventSilenceMode);
		boxSilentMode.setAdapter(adapter1);
		int curSilent = getIntent().getIntExtra("EventSilenceOption", 0);
		boxSilentMode.setSelection(curSilent);
		
		//Create items for internet dropdown
		String items3[] = {"NOT SET", "WiFi", "3G", "Wifi + 3G"};
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items3);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		boxInternet = (Spinner) findViewById(R.id.box_EventInternet);
		boxInternet.setAdapter(adapter3);
		int curInternet = getIntent().getIntExtra("EventInternetOption", 0);
		boxInternet.setSelection(curInternet);
		
		//Set ringtone to data
		TextView eventRingtone = (TextView) findViewById(R.id.box_EventRingtoneSelected);
		eventRingtone.setText(getIntent().getStringExtra("EventRingtone"));
		
		//Create draggable slider
		rangeBar = (SeekBar) findViewById(R.id.bar_EventRange);
		rangeBar.setMax(180);
		rangeBar.setProgress(curRange - 20);
		rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { 
		   @Override 
		   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { 
		    	//Show the progress
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
	
	public void onClick_EventContinue(View view) {
		// Fires whenever the silence button is toggeled.
		EditText eventTitle = (EditText) findViewById(R.id.txt_EventTitle);
		TextView eventRingtone = (TextView) findViewById(R.id.box_EventRingtoneSelected);
		
		Log.i("INTENT OUT", "EventTitle: " + eventTitle.getText().toString());
		Log.i("INTENT OUT", "EventIcon: " + iconSelected);
		Log.i("INTENT OUT", "EventRange: " + (20 + rangeBar.getProgress()));
		Log.i("INTENT OUT", "EventSilenceOption: " + boxSilentMode.getSelectedItemPosition());
		Log.i("INTENT OUT", "EventInternetOption: " + boxInternet.getSelectedItemPosition());
		Log.i("INTENT OUT", "EventRingtone: " + eventRingtone.getText().toString());
		
		getIntent().putExtra("EventTitle", eventTitle.getText().toString());
		getIntent().putExtra("EventIcon", iconSelected);
		getIntent().putExtra("EventRange", (20 + rangeBar.getProgress()));
		getIntent().putExtra("EventSilenceOption", boxSilentMode.getSelectedItemPosition());
		getIntent().putExtra("EventInternetOption", boxInternet.getSelectedItemPosition());
		getIntent().putExtra("EventRingtone", eventRingtone.getText().toString());
		
		getIntent().putExtra("EventDelete", false);
		
		setResult(Activity.RESULT_OK, getIntent());
		finish();
	}
	
	public void onClick_EventDelete(View view) {
		getIntent().putExtra("EventDelete", true);
		
		setResult(Activity.RESULT_OK, getIntent());
		finish();
	}
	
	public void BrowseForRingtone(View view) {
		
	}
	
	public void SelectNewIcon(View view) {
		if (iconSelected == R.drawable.img_pin_red) {
			iconSelected = R.drawable.img_pin_blue;
		}
		else if (iconSelected == R.drawable.img_pin_blue) {
			iconSelected = R.drawable.img_pin_green;
		}
		else if (iconSelected == R.drawable.img_pin_green) {
			iconSelected = R.drawable.img_pin_red;
		}
		
		ImageView icon = (ImageView) findViewById(R.id.img_Icon);
		icon.setImageResource(iconSelected);
		Log.i("SELECTED_ICON", "Resource: " + iconSelected);
	}
}