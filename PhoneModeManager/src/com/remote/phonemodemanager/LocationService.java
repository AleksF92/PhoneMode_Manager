package com.remote.phonemodemanager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {
	
	private final static String PROCESS = "Service";
	private onUpdate updater;
	private boolean isRunning;
	private Event defaultSettings;
	private Map<String, Event> events = new ConcurrentHashMap<String, Event>();
	private LatLng currentPosition;
	private DatabaseHelper dbHelper;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		//Initialize service
		updater = new onUpdate();
		isRunning = false;
		
		//Initialize database
		dbHelper = new DatabaseHelper(this);
		
		Log.i(PROCESS, "Service created!");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Start service
		if (!isRunning) {
			updater.start();
			isRunning = true;
		}
		
		//Initialize default settings
		defaultSettings = new Event();
		defaultSettings.silenceOption = intent.getIntExtra("DefaultSilence", 0);
		defaultSettings.internetOption = intent.getIntExtra("DefaultInternet", 0);
		defaultSettings.ringtoneResource = intent.getStringExtra("DefaultRingtone");
		
		//Load events from database
		loadFromDatabase();
		
		Log.i(PROCESS, "Service started with command!");
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//Stop service
		if (isRunning) {
			isRunning = false;
		}
		Log.i(PROCESS, "Service destroyed!");
	}

	public void activateEvent(Event event) {
		//Do what the event is supposed to...
		
		//Silence mode:
		AudioManager soundSettings = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		switch (event.silenceOption) {
			case 1: {
				//Vibrate
				soundSettings.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				break;
			}
			case 2: {
				//Silent
				soundSettings.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				break;
			}
			case 3: {
				//Sound on
				soundSettings.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				break;
			}
		}
		
		//Internet mode:
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		switch (event.internetOption) {
			case 1: {
				//WiFi
				wifi.setWifiEnabled(true);
				setMobileData(false);
				break;
			}
			case 2: {
				//3G
				wifi.setWifiEnabled(false);
				setMobileData(true);
				break;
			}
			case 3: {
				//Wifi + 3G
				wifi.setWifiEnabled(true);
				setMobileData(true);
				break;
			}
		}
		
		//Ringtone:
		if (!event.ringtoneResource.equals("NOT SET")) {
			//Set ringtone
			Uri ringtoneUri = Uri.parse(event.ringtoneResource);
			RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, ringtoneUri);
		}
		
		if (event == defaultSettings) {
			Log.i(PROCESS, "Default settings restored!");
		}
		else {
			Log.i("Activate", event.marker.getTitle() + "(A: " + event.silenceOption + ", I: " + event.internetOption + ", R: " + event.ringtoneResource + ")");
		}
	}
	
	public void setMobileData(boolean state) {
        ConnectivityManager mobileNet = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Method mobileDataEnabled = null;
        
        //Initialize mobile data change
        try {
        	mobileDataEnabled = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        
        mobileDataEnabled.setAccessible(true);
        
        try {
        	mobileDataEnabled.invoke(mobileNet, state);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
	}

	public void checkDistances() {
		boolean resetToDefault = true;
		
		//Initialize results
		float resultDistance[] = new float[3];
		
		//For all the events stored
		for (Event curEvent : events.values()) {
			//Create event location
	    	LatLng position = curEvent.marker.getPosition();

        	//Calcualte distance
        	Location.distanceBetween(currentPosition.latitude, currentPosition.longitude, position.latitude, position.longitude, resultDistance);
        	int distInMeters = (int) resultDistance[0];
        	
        	//Check if within range
        	if (distInMeters <= curEvent.range) {
        		//Event should be active
        		activateEvent(curEvent);
        		resetToDefault = false;
        	}
        	
        	Log.i(PROCESS, curEvent.marker.getTitle() + " (Distance: " + distInMeters + " meters)");
        }
		
		//Restore default settings (if no events in range)
		if (resetToDefault) {
			activateEvent(defaultSettings);
		}
		
		//Log.i(PROCESS, "Done checking elements!");
	}
	
	class onUpdate extends Thread {
		static final int WAIT = 5 * 1000;
		static final int SYNC = 5 * 1000;
		int runtime = 0;
		
		@Override
		public void run() {
			while (isRunning) {
				//Wait session
				try {
					sleep(WAIT);
					runtime += WAIT;
					
					//Check if you should sync
					if (runtime >= SYNC) {
						//Load and update state
						runtime = 0;
						loadFromDatabase();
					}
				} catch (InterruptedException e) {
	
				}
				
				//Check if there are events within range
				checkDistances();
			}
		}
	}
	
	
	public void loadFromDatabase() {
		//Log.i(PROCESS, "Loading from database...");
		
		//Clear currently known events
		events.clear();
		
		//Get events from database
		Point[] points = Point.getAll(dbHelper);
		
		//For all stored events
		for (int i = 0; i < points.length; i++) {
			//Gather data
			String title = points[i].getName();
			double x = points[i].getX();
			double y = points[i].getY();
			int radius = points[i].getRadius();
			int silent = points[i].getMode();
			int internet = points[i].getConnection();
			String ringtone = points[i].getRingtone();
			
			//Create new event
			Event newEvent = new Event();
			MarkerOptions marker = new MarkerOptions()
			.title(title)
			.position(new LatLng(x, y));
			newEvent.marker = marker;
			newEvent.range = radius;
			newEvent.silenceOption = silent;
			newEvent.internetOption = internet;
			newEvent.ringtoneResource = ringtone;
			
			//If first element (LastKnownLocation)
			if (i == 0) {
				currentPosition = new LatLng(x, y);
				//Log.i("Map", "Loaded: " + title + ", x: " + (int)x + ", y: " + (int)y);
			}
			else {
				//Store new event
				events.put("" + i, newEvent);
				//Log.i(PROCESS, "Loaded (name: " + title + ", x: " + (int)x + ", y: " + (int)y + ", r: " + radius + ", a: [" + silent + ", " + internet + ", " + ringtone + "])...");
			}
		}
		
		//Log.i(PROCESS, "Loaded from database!");
		
		//Check if there are events within range
		//checkDistances();
	}

}
