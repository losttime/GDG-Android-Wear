package com.dconstructing.android.gdgdemo.services;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by dcox on 8/12/14.
 */
public class DemoWearableListenerService extends WearableListenerService {

	private static final String SOUND_ALERT_PATH = "/alert/start";

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		super.onMessageReceived(messageEvent);

		if (messageEvent.getPath().equals(SOUND_ALERT_PATH)) {
			// Sound the alert
		}
	}

}
