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
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dcox on 8/12/14.
 *
 * In order to send a message to a different device (watch->phone, phone->watch),
 * you'll need to connect to Google Play Services. Connecting to Google Play Services is an
 * asynchronous process. Don't have your message-sending service extend from IntentService, because
 * IntentService shuts itself down as soon as the `onHandleIntent` method is complete. You'll still
 * be waiting for the connection to Google Play Services when your service cancels itself, so your
 * message will never get sent.
 */
public class MessageService extends Service implements GoogleApiClient.ConnectionCallbacks {

	private static final int MESSAGE_GOOGLE_PLAY_SERVICES = 1;
	private static final int MESSAGE_FIND_HOST = 2;
	private static final int MESSAGE_INTENT = 3;

	private static final String SOUND_ALERT_PATH = "/alert/start";

	private LinkedList<Intent> intentQueue = new LinkedList<Intent>();

	private GoogleApiClient googleApiClient;
	private volatile ServiceHandler serviceHandler;
	private Node host;

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

		Message hostmsg = serviceHandler.obtainMessage();
		hostmsg.what = MESSAGE_FIND_HOST;
		serviceHandler.sendMessage(hostmsg);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Message msg = serviceHandler.obtainMessage();
		msg.what = MESSAGE_INTENT;
		msg.arg1 = startId;
		serviceHandler.sendMessage(msg);

		return START_STICKY;
	}

	private void findHostNode() {
		NodeApi.GetConnectedNodesResult nodesResult = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
		List<Node> nodes = nodesResult.getNodes();
		if (nodes.size() > 0) {
			host = nodes.get(0);
			processIntents();
		} else {
			stopSelf();
		}
	}

	private void onHandleIntent(Intent intent) {
		if (googleApiClient.isConnected() && host != null) {
			processIntent(intent);
		} else {
			intentQueue.add(intent);
		}
	}

	private void processIntents() {
		if (googleApiClient.isConnected() && host != null) {
			while(intentQueue.size() > 0) {
				processIntent(intentQueue.poll());
			}
		}
	}

	private void processIntent(Intent intent) {
		MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, host.getId(), SOUND_ALERT_PATH, null).await();

		if (intentQueue.size() == 0) {
			stopSelf();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onConnected(Bundle bundle) {
		processIntents();
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
				case MESSAGE_FIND_HOST:
					findHostNode();
					break;
				case MESSAGE_INTENT:
					onHandleIntent((Intent) msg.obj);
				default:
					// no-op
			}
		}
	}
}
