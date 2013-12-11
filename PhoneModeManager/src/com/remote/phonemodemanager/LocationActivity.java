package com.remote.phonemodemanager;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class LocationActivity extends FragmentActivity implements OnMapClickListener, OnMarkerClickListener {

	private GoogleMap mMap;
	Map<String, Event> events = new ConcurrentHashMap<String, Event>();
	int totalEvents = 0;
	String lastEvent = "";
	static final int MIN_RANGE = 20;
	Intent serviceIntent;
	private LatLng myPosition;
	MarkerOptions myMarker;
	Date date;
	private DatabaseHelper dbHelper;
	
	DialogInterface.OnClickListener confirmListener;
	AlertDialog.Builder boxConfirm;
	DialogInterface.OnClickListener updateListener;
	AlertDialog.Builder boxUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		//Initialize date
		date = new Date();
		
		//Create dialog listeners
		confirmListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
					//Remove event
					events.remove(lastEvent);
					updateMap();
					
					//Update database
					saveToDatabase();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            break;
		        }
		        
				//Clear last event
				lastEvent = "";
		    }
		};
		
		updateListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            break;
		        }
		    }
		};
		
		//Create dialog boxes
		boxConfirm = new AlertDialog.Builder(this);
		boxConfirm.setMessage("Are you sure?");
		boxConfirm.setPositiveButton("Yes", confirmListener);
		boxConfirm.setNegativeButton("No", confirmListener);
		
		boxUpdate = new AlertDialog.Builder(this);
		boxUpdate.setMessage("An update has occured!");
		boxUpdate.setPositiveButton("Ok", updateListener);
		
		//Initialize database
		dbHelper = new DatabaseHelper(this);
		
		setUpMapIfNeeded();
		
		//Initialize service intent
		serviceIntent = new Intent(this, LocationService.class);
		serviceIntent.putExtra("CurrentPositionLng", myPosition.longitude);
		serviceIntent.putExtra("CurrentPositionLat", myPosition.latitude);
		
		//Store default system values
		AudioManager soundSettings = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int silentMode = 0;
		switch(soundSettings.getRingerMode()) {
			case (AudioManager.RINGER_MODE_VIBRATE): {
				silentMode = 1;
				break;
			}
			case (AudioManager.RINGER_MODE_SILENT): {
				silentMode = 2;
				break;
			}
			case (AudioManager.RINGER_MODE_NORMAL): {
				silentMode = 3;
				break;
			}
		}
		serviceIntent.putExtra("DefaultSilence", silentMode);
		serviceIntent.putExtra("DefaultInternet", 0);
		serviceIntent.putExtra("DefaultRingtone", "");
		
		//Log.i("LocationAct", "The activity was created!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case (R.id.serviceStart): {
				startService(serviceIntent);
				break;
			}
			case (R.id.serviceStop): {
				stopService(serviceIntent);
				break;
			}
		}
		
		return true;
	}
	
	private void configureMap() {
		Log.i("Map Setup", "Starting to configure map...");
		
		//Enable listeners
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        //mMap.setLocationSource(this);
        
        //Enable positioning
        mMap.setMyLocationEnabled(true);
        
        //Set maptype
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        
        Log.i("Map Setup", "Data setup done!");
        
        //Gather my position (last known)
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
        	@Override
        	public void onLocationChanged(Location location) {
        		//My location changed
        		myPosition = new LatLng(location.getLatitude(), location.getLatitude());
        		date = new Date();
        		
        		//Update marker
        		myMarker.snippet(new Timestamp(date.getTime()).toString());
        		myMarker.position(myPosition);
        		
        		//Update map
        		//updateMap();
        		
                //Move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                
                //Show dialog
                boxUpdate.show();
        		
        		Log.i("Map Location", "New cache for location retrived!");
        	}

        	@Override
        	public void onProviderDisabled(String provider) {
        		//Required, but not in use
        	}

        	@Override
        	public void onProviderEnabled(String provider) {
        		//Required, but not in use
        	}
        	
        	@Override
        	public void onStatusChanged(String provider, int status, Bundle extras) {
        		//Required, but not in use
        	}
        });
        
        //Criteria for the state of the phone
        Criteria criteria = new Criteria();
        
        //Best provider, concidering the criteria
        String provider = locationManager.getBestProvider(criteria, false);
        
        //My currently last known position
        Location location = locationManager.getLastKnownLocation(provider);
        
        double lat = 0;
        double lng = 0;
        
        if (location == null) {
        	Log.i("Map Setup", "Location: null");
        }
        else {
            lat =  location.getLatitude();
            lng = location.getLongitude();
        }
        
        //Position
        myPosition = new LatLng(lat, lng);
        
        //Move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        
        Log.i("Map Setup", "Animation setup done!");

		//Add marker for refrence
		myMarker = new MarkerOptions();
		myMarker.title("You are here!");
		myMarker.snippet(new Timestamp(date.getTime()).toString());
		myMarker.position(myPosition);
		myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_pin_red));
        mMap.addMarker(myMarker);
        
        Log.i("Map Setup", "Map setup done!");
        
        //Load saved events
        loadFromDatabase();
	}
	
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_Location))
            .getMap();
            
            //If map was retrieved
            if (mMap != null) {
            	configureMap();
            }
        }
    }

    private void updateMap() {
    	//Clear all markers
    	mMap.clear();
    	
    	//Add my marker
    	mMap.addMarker(myMarker);

    	//For all stored markers
        for (Event curEvent : events.values()) {
        	MarkerOptions marker = curEvent.marker;
        	mMap.addMarker(marker);
        }
    }
    
    private void createNewEvent(LatLng position) {
    	//Creates a new event, unless the last entered position was passed.
    	
    	//Get last event
    	String eventId = "" + lastEvent;
    	Event lEvent = events.get(lastEvent);
    	
    	//Data to use
    	String nTitle;
    	int nIcon;
    	int nRange;
    	int nSilenceOption;
    	int nInternetOption;
    	String nRingtone;
    	Event newEvent;
    	
    	//Check if new point
    	if (lEvent == null || position != lEvent.marker.getPosition()) {
    		//Initialize new data
    		nTitle = "New Event (" + ++totalEvents + ")";
    		nIcon = R.drawable.img_pin_red;
    		nRange = MIN_RANGE;
    		nSilenceOption = 0;
    		nInternetOption = 0;
    		nRingtone = getResources().getString(R.string.notSet);
    		eventId = "" + totalEvents;
    		newEvent = new Event();
    	}
    	else {
    		//Use current data
        	nTitle = lEvent.marker.getTitle();
        	nIcon = lEvent.iconResource;
        	nRange = lEvent.range;
        	nSilenceOption = lEvent.silenceOption;
        	nInternetOption = lEvent.internetOption;
        	nRingtone = lEvent.ringtoneResource;
        	newEvent = lEvent;
    	}
    	
    	//Set marker options
    	MarkerOptions newMark = new MarkerOptions();
    	newMark.position(position);
        newMark.title(nTitle);
        newMark.icon(BitmapDescriptorFactory.fromResource(nIcon));
        
        //Set event
        newEvent.marker = newMark;
        newEvent.iconResource = nIcon;
        newEvent.range = nRange;
        newEvent.silenceOption = nSilenceOption;
        newEvent.internetOption = nInternetOption;
        newEvent.ringtoneResource = nRingtone;
        
        //Add the new marker
        events.put(eventId, newEvent);
        lastEvent = eventId;
        
        //Update map
        updateMap();
        
        //Print data
        double x = position.longitude;
        double y = position.latitude;
        Log.i("MAP_POINT", "LatLon (" + (int)x + ", " + (int)y + ")");
    }

	@Override
	public void onMapClick(LatLng position) {
		createNewEvent(position);
		
		//Pass event data
		Event editEvent = events.get(lastEvent);
		Intent data = new Intent(this, NewEventActivity.class);
		
		data.putExtra("EventTitle", editEvent.marker.getTitle());
		data.putExtra("EventIcon", editEvent.iconResource);
		data.putExtra("EventRange", editEvent.range);
		data.putExtra("EventSilenceOption", editEvent.silenceOption);
		data.putExtra("EventInternetOption", editEvent.internetOption);
		data.putExtra("EventRingtone", editEvent.ringtoneResource);
		
		startActivityForResult(data, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Check if result of the new event settings
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				//Update the data
				Event editEvent = events.get(lastEvent);
				
				//Log.i("RESULT_EVENT", "Delete: " + data.getBooleanExtra("EventDelete", false));
				
				//Check if need to delete
				if (data.getBooleanExtra("EventDelete", false)) {
					//Remove it and clear last pointer
					boxConfirm.show();
				}
				else {
					//Gather new data and update
					editEvent.marker.title(data.getStringExtra("EventTitle"));
					editEvent.iconResource = data.getIntExtra("EventIcon", R.drawable.img_pin_red);
					editEvent.marker.icon(BitmapDescriptorFactory.fromResource(editEvent.iconResource));
					editEvent.range = data.getIntExtra("EventRange", MIN_RANGE);
					editEvent.silenceOption = data.getIntExtra("EventSilenceOption", 0);
					editEvent.internetOption = data.getIntExtra("EventInternetOption", 0);
					editEvent.ringtoneResource = data.getStringExtra("EventRingtone");
					
					//Print the data
				    /*
					Log.i("RETURN_EVENT", "Title: " + editEvent.marker.getTitle());
				    Log.i("RETURN_EVENT", "Icon: " + editEvent.iconResource);
				    Log.i("RETURN_EVENT", "Range: " + editEvent.range);
				    Log.i("RETURN_EVENT", "Silence: " + editEvent.silenceOption);
				    Log.i("RETURN_EVENT", "Internet: " + editEvent.internetOption);
				    Log.i("RETURN_EVENT", "Ringtone: " + editEvent.ringtoneResource);
				    */
				    
				    //Update event
				    createNewEvent(editEvent.marker.getPosition());
				    //boxUpdate.show();
				    
					//Clear last event
					lastEvent = "";
					
					//Update database
					saveToDatabase();
				}
			}
		}
	}
	
    @Override
    public boolean onMarkerClick(Marker selectedMarker) {
    	selectedMarker.showInfoWindow();
    	Event editEvent = events.get(lastEvent);
    	LatLng pos1 = selectedMarker.getPosition();
    	
    	//Check if marker is clicked twise
    	boolean doubleClick = false;
    	if (editEvent != null) {
	        if (pos1.equals(editEvent.marker.getPosition())) {
	        	doubleClick = true;
	        	Log.i("MarkerClick", "Want to edit!");
	        	
	    		//Pass event data
	    		Intent data = new Intent(this, NewEventActivity.class);
	    		
	    		data.putExtra("EventTitle", editEvent.marker.getTitle());
	    		data.putExtra("EventIcon", editEvent.iconResource);
	    		data.putExtra("EventRange", editEvent.range);
	    		data.putExtra("EventSilenceOption", editEvent.silenceOption);
	    		data.putExtra("EventInternetOption", editEvent.internetOption);
	    		data.putExtra("EventRingtone", editEvent.ringtoneResource);
	    		
	    		startActivityForResult(data, 0);
	        }
    	}
        if (!doubleClick) {
        	Log.i("MarkerClick", "Marker clicked!");

        	//Check for this marker in the event list
        	for (Map.Entry<String, Event> entry : events.entrySet()) {
        		String key = entry.getKey();
        		Event curEvent = entry.getValue();
        		LatLng pos2 = curEvent.marker.getPosition();
        		
        		//If position matches
        		if (pos1.equals(pos2)) {
        			//Set this to the last clicked
        			lastEvent = "" + key;
        			Log.i("MarkerClick", "LastEvent: " + lastEvent);
        		}
        	}
        }
        
        return true;
    }

	public void saveToDatabase() {
		//Clear current data
		Point.clear(dbHelper);
		
		//Save our last known position
		Point ourPoint = new Point("LastKnownPosition", myPosition.longitude, myPosition.latitude,0, 0, "", 0);
		ourPoint.save(dbHelper);
		
		//For all stored events
		for (Event curEvent : events.values()) {
			//Gather data
			String title = curEvent.marker.getTitle();
			LatLng position = curEvent.marker.getPosition();
			double x = position.latitude;
			double y = position.longitude;
			int radius = curEvent.range;
			int silent = curEvent.silenceOption;
			String ringtone = ""; //curEvent.ringtoneResource;
			int internet = curEvent.internetOption;
			
			//Crate the new point
			Point curPoint = new Point(title, x, y, radius, silent, ringtone, internet);
			
			//Store new point
			curPoint.save(dbHelper);
			
			Log.i("SaveToDatabase", "Saved (name: " + title + ", x: " + (int)x + ", y: " + (int)y + ", r: " + radius + ", a: [" + silent + ", " + internet + ", " + ringtone + "])...");
		}
		
		Log.i("SaveToDatabase", "Saved to database!");
	}

	public void loadFromDatabase() {
		Log.i("Map", "Loading existing events...");
		
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
			String ringtone = ""; //points[i].getName();
			
			//Create new event
			Event newEvent = new Event();
			MarkerOptions marker = new MarkerOptions()
			.title(title)
			.position(new LatLng(x, y))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_pin_red));
			newEvent.marker = marker;
			newEvent.range = radius;
			newEvent.silenceOption = silent;
			newEvent.internetOption = internet;
			newEvent.ringtoneResource = ringtone;
			
			//If not first element (LastKnownLocation)
			if (i > 0) {
				//Store new event
				events.put("" + i, newEvent);
				Log.i("Map", "Loaded (name: " + title + ", x: " + (int)x + ", y: " + (int)y + ", r: " + radius + ", a: [" + silent + ", " + internet + ", " + ringtone + "])...");
			}
			else {
				Log.i("Map", "Skipped: " + title + ", x: " + (int)x + ", y: " + (int)y);
			}
		}
		
		//Update map
		updateMap();
	}
}
