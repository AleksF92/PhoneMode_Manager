package com.remote.phonemodemanager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class CallDetectService extends Service {
    private CallHelper callHelper;
 
    public CallDetectService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callHelper = new CallHelper(this);
  
        int res = super.onStartCommand(intent, flags, startId);
        callHelper.start();
        
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
            //filter.addAction(Intent.ACTION_SCREEN_OFF);
            //filter.addAction(Intent.ACTION_SCREEN_ON);
            BroadcastReceiver mReceiver = new ScreenReciever();
            registerReceiver(mReceiver, filter);
       } catch (Exception e) {

       }
        
        return res;
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
  
        callHelper.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not supporting binding
        return null;
   }
}