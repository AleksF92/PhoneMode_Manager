package com.remote.phonemodemanager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class LocationActivity extends FragmentActivity implements OnMapClickListener {

	private GoogleMap mMap;
	
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

	public void onClick_Back(View view) {
		// Fires whenever the silence button is toggeled.
		this.finish();
	}
	
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_Location))
                    .getMap();
            mMap.setOnMapClickListener(this);
            mMap.setMyLocationEnabled(true);
            
            //Set camera to current location
            //To do... !
        }
    }

    private void setUpMap(LatLng position) {
        mMap.addMarker(new MarkerOptions()
        	.position(position)
        	.title("Marker")
        	.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_pin))
        );
    }

	@Override
	public void onMapClick(LatLng position) {
		// TODO Auto-generated method stub
		setUpMap(position);
		
		//Create the new event
		Intent intent = new Intent(this, NewEventActivity.class);
		startActivity(intent);
	}
}
