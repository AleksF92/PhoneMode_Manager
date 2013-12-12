package com.remote.phonemodemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReciever extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
		/*
		if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
			System.out.println("Screen On");
        }
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
			System.out.println("Screen Off");
        }
        */
		if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
			System.out.println("Screen is now unlocked");
			//TODO: Reset call counter list
        }
    }
}