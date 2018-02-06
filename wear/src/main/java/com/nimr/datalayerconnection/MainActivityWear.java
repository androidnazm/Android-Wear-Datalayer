package com.nimr.datalayerconnection;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wear.widget.BoxInsetLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class MainActivityWear extends WearableActivity
		implements DataClient.OnDataChangedListener {
	
	
	private TextView mTextView;
	//private  String DEFAULT_COLOR="2007";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_wear);
		
		mTextView = findViewById(R.id.text);
		
		final BoxInsetLayout boxInsetLayout = findViewById(R.id.box_layout);
		boxInsetLayout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String s = "X=" + event.getX();
				s += ", Y=" + event.getY();
				Log.v("com.nimr wear", s);
				final PutDataMapRequest putRequest = PutDataMapRequest.create("/WEAR2PHONE");
				final DataMap map = putRequest.getDataMap();
				map.putFloat("touchX", event.getX());
				map.putFloat("touchY", event.getY());
				Wearable.getDataClient(MainActivityWear.this).putDataItem( putRequest.asPutDataRequest());
				return false;
			}
			
		});
		
		
		
		
		// Enables Always-on
		setAmbientEnabled();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		
		Wearable.getDataClient(this).addListener(this).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				Log.i("com.nimr DataConnection", "Success");
			}
		});
	}
	
	
	private void setBackgroundColor(int color) {
		
		final BoxInsetLayout boxInsetLayout = findViewById(R.id.box_layout);
		boxInsetLayout.setBackgroundColor(color);
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Wearable.getDataClient(this).removeListener(this);
		
	}
	
	
	@Override
	public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
		//super.onDataChanged(dataEventBuffer);
		
		final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
		for(DataEvent event : events) {
			final Uri uri = event.getDataItem().getUri();
			final String path = uri!=null ? uri.getPath() : null;
			if("/PHONE2WEAR".equals(path)) {
				final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
				// read your values from map:
				int color = map.getInt("color");
				Log.v("com.nimr Wear", "Color received: " + color);
				setBackgroundColor(color);
			}
		}
		
	}
	
}