package com.nimr.datalayerconnection;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivityPhone extends AppCompatActivity
		implements DataClient.OnDataChangedListener,
				           MessageClient.OnMessageReceivedListener,
				           CapabilityClient.OnCapabilityChangedListener {
	
	private int mColorCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_phone);
		
		Wearable.getDataClient(this).addListener(this ).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				Log.i("com.nimr", "Successful");
			}
		});
		
		
	}
	
	public void syncDataItem(View view){
		int r = (int) (255 * Math.random());
		int g = (int) (255 * Math.random());
		int b = (int) (255 * Math.random());
		
		final PutDataMapRequest putRequest = PutDataMapRequest.create("/PHONE2WEAR");
		final DataMap map = putRequest.getDataMap();
		map.putInt("color", Color.rgb(r,g,b));
		
		map.putString("colorChanges", "Amount of changes: " + mColorCount++);
		
		Log.v("Com.nimr SyncDataItem ", "Handheld sent new random color to watch");
		Log.v("Com.nimr SyncDataIteme", "color:" + r + ", " + g + ", " + b);
		Log.v("Com.nimr SyncDataItem", "iteration:" + mColorCount);
		Wearable.getDataClient(this).putDataItem(putRequest.asPutDataRequest())
				.addOnSuccessListener(new OnSuccessListener<DataItem>() {
					@Override
					public void onSuccess(DataItem dataItem) {
						Log.i("com.nimr", "Successully Sending Data");
					}
				});
		
		
	}
	private void updateTextField(String text) {
		Log.v("Com.nimr.phone", "Arrived text:" + text);
		((TextView)findViewById(R.id.textViewTouch)).setText(text);
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	protected void onResume() {
		super.onResume();
		Wearable.getDataClient(this).addListener(this );
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Wearable.getDataClient(this).removeListener(this);
		
	}
	
	@Override
	public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
	
	}
	
	@Override
	public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
		
		final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
		for(DataEvent event : events) {
			final Uri uri = event.getDataItem().getUri();
			final String path = uri!=null ? uri.getPath() : null;
			if("/WEAR2PHONE".equals(path)) {
				final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
				// read your values from map:
				float X = map.getFloat("touchX");
				float Y = map.getFloat("touchY");
				String reply = "Touched X=" + X + ", Y=" + Y;
				Log.v("com.nimr.phone", reply);
				updateTextField(reply);
			}
		}
		
	}
	
	@Override
	public void onMessageReceived(@NonNull MessageEvent messageEvent) {
	
	}
}