package com.dconstructing.android.gdgdemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by dcox on 8/12/14.
 */
public class MessageService extends Service implements GoogleApiClient.ConnectionCallbacks {

	private static final int MESSAGE_GOOGLE_PLAY_SERVICES = 1;

	private GoogleApiClient googleApiClient;
	private volatile ServiceHandler serviceHandler;

	// TODO: Send a message to the handset

	@Override
	public void onCreate() {
		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addApi(Wearable.API)
				.build();

		HandlerThread thread = new HandlerThread("MessageService");
		thread.start();

		serviceHandler = new ServiceHandler(thread.getLooper());

		Message GPSMessage = serviceHandler.obtainMessage();
		GPSMessage.what = MESSAGE_GOOGLE_PLAY_SERVICES;
		serviceHandler.sendMessage(GPSMessage);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO: Send a message to the handset

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {
		// no-op
	}

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case MESSAGE_GOOGLE_PLAY_SERVICES:
					googleApiClient.connect();
					break;
				default:
					// no-op
			}
		}
	}
}
