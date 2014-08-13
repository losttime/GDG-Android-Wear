package com.dconstructing.android.gdgdemo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.dconstructing.android.gdgdemo.R;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by dcox on 8/11/14.
 */
public class DemoWearableListenerService extends WearableListenerService {

	@Override
	public void onPeerDisconnected(Node peer) {
		super.onPeerDisconnected(peer);

		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Warning")
				.setContentText("You are disconnected")
				.setPriority(Notification.PRIORITY_HIGH)
				.setDefaults(Notification.DEFAULT_ALL)
				.build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
	}

	@Override
	public void onPeerConnected(Node peer) {
		super.onPeerConnected(peer);

		// TODO: Add an action to this notification

		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("All Clear")
				.setContentText("We're back ;)")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setDefaults(NotificationCompat.DEFAULT_ALL)
				.build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
	}

}
