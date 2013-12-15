package com.remote.phonemodemanager;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallHelper {

	/**
	 * Listener to detect incoming calls. 
	 */
	private class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				final DatabaseHelper dbHelper = new DatabaseHelper(ContactListActivity.getContactContext());
				List<Whitelist> list = Arrays.asList(Whitelist.getAll(dbHelper));
				
				for(int i = 0; i < list.size(); i++){
					String savedNr = list.get(i).getNr();
					System.out.println(incomingNumber +" Testing against "+savedNr.subSequence(savedNr.length()-8, savedNr.length()));
					if( incomingNumber.equals(savedNr.subSequence(savedNr.length()-8, savedNr.length()))){
						if(!(list.get(i).getUnNoticedCalls() < list.get(i).getLevel()-1)){
							AudioManager audio_mngr = (AudioManager) ContactListActivity.getContactContext().getSystemService(Context.AUDIO_SERVICE);
							audio_mngr .setRingerMode(AudioManager.RINGER_MODE_NORMAL);
							System.out.println("Called 3 times!!!!");
							//TODO:: Turn on sound
						}else{
							System.out.println(list.get(i).getUnNoticedCalls());
							list.get(i).setUnNoticedCalls(list.get(i).getUnNoticedCalls()+1);
						}
						Whitelist.clear(dbHelper);
						for(int x = 0; x < list.size(); x++){
							list.get(x).save(dbHelper);
						}
						
					}
				}
				
				System.out.println("Incoming: "+incomingNumber);
				//TODO:: Fix bugs with contacts saved with country numbers
				
				break;
			}
		}
	}
		private Context ctx;
	private TelephonyManager tm;
	private CallStateListener callStateListener;
	
	public CallHelper(Context ctx) {
		this.ctx = ctx;
		
		callStateListener = new CallStateListener();
	}
	
	/**
	 * Start calls detection.
	 */
	public void start() {
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/**
	 * Stop calls detection.
	 */
	public void stop() {
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
	}

}