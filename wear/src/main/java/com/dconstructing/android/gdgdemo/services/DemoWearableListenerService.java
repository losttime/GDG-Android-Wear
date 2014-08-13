package com.dconstructing.android.gdgdemo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

		// Most documentation uses the compat library to build Notifications.
		// That's needed for notifications built on the mobile device.
		// Wear device are guaranteed to have the Notification APIs from API version 20,
		// so notifications built on the wear device don't need to use the compat libraray.
		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Warning")
				.setContentText("You are disconnected")
				.setPriority(Notification.PRIORITY_HIGH)
				.setDefaults(Notification.DEFAULT_ALL)
				.build();

		// The first parameter passed to the `notify` method of the NotificationManager is an ID
		// for your Notification. If you want one Notification to replace another, make sure they
		// have the same ID. If you want two distinct Notifications at once, make sure they have
		// different IDs.
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
	}

	@Override
	public void onPeerConnected(Node peer) {
		super.onPeerConnected(peer);

		// Most documentation merely states that you can start an Activity or Service on the mobile
		// device by providing an Action with a PendingIntent to the Notification.
		// What they fail to mention is that only applies if the Notification is built on the mobile
		// device and mirrored to the wear device.
		// For Notifications built on the wear device, your PendingIntent will launch an Activity or
		// Service also on the wear device. To make something happen on the mobile device you will
		// then need to send a message to the mobile device and have the message receiver on the
		// mobile device launch the Activity or Service.
		Intent actionIntent = new Intent(this, MessageService.class);
		PendingIntent actionPendingIntent = PendingIntent.getService(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Action action = new Notification.Action.Builder(R.drawable.ic_full_cancel,
				"Alert", actionPendingIntent)
				.build();

		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("All Clear")
				.setContentText("We're back ;)")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setDefaults(NotificationCompat.DEFAULT_ALL)
				.extend(new Notification.WearableExtender().addAction(action))
				.build();

		// This Notification is sent with the same ID as the preceeding Notification. It will
		// replace the previous Notification.
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);
	}

}
