package com.remote.phonemodemanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class LocationActivity extends FragmentActivity implements OnMapClickListener, OnMarkerClickListener {

	private GoogleMap mMap;
	Map<String, Event> events = new ConcurrentHashMap<String, Event>();
	int totalEvents = 0;
	String lastEvent = "";
	static final int MIN_RANGE = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		
		setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}
	
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_Location))
                    .getMap();
            mMap.setOnMapClickListener(this);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(this);
            //Set camera to current location
            //To do... !
        }
    }

    private void updateMap() {
    	//Clear all markers
    	mMap.clear();

    	//For all stored markers
        for (Event curEvent : events.values()) {
        	MarkerOptions marker = curEvent.marker;
        	mMap.addMarker(marker);
        }
    }
    
    private void createNewEvent(LatLng position) {
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
    
    public void onMarkerClick() {
    	// TODO
    }

	@Override
	public void onMapClick(LatLng position) {
		// TODO Auto-generated method stub
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
		
		//data.putExtra("EventDelete", false);
		
		startActivityForResult(data, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Check if result of the new event settings
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				//Update the data
				Event editEvent = events.get(lastEvent);
				
				Log.i("RESULT_EVENT", "Delete: " + data.getBooleanExtra("EventDelete", false));
				
				//Check if need to delete
				if (data.getBooleanExtra("EventDelete", false)) {
					//Remove it and clear last pointer
					events.remove(lastEvent);
					lastEvent = "";
					updateMap();
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
				    Log.i("RETURN_EVENT", "Title: " + editEvent.marker.getTitle());
				    Log.i("RETURN_EVENT", "Icon: " + editEvent.iconResource);
				    Log.i("RETURN_EVENT", "Range: " + editEvent.range);
				    Log.i("RETURN_EVENT", "Silence: " + editEvent.silenceOption);
				    Log.i("RETURN_EVENT", "Internet: " + editEvent.internetOption);
				    Log.i("RETURN_EVENT", "Ringtone: " + editEvent.ringtoneResource);
				    
				    //Update event
				    createNewEvent(editEvent.marker.getPosition());
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
}
