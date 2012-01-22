package com.shreyaschand.MEDIC;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Trial extends Activity implements OnClickListener {

	private final String TAG = "Trial";
	
	private BluetoothAdapter btAdapter;

	private BluetoothDevice device = null;
	
	private static final int DEVICE_CONNECT = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trial);

		View backButtonView = findViewById(R.id.trial_back_button);
		backButtonView.setOnClickListener(this);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		Button connect_button = (Button) findViewById(R.id.trial_connect_button);
		connect_button.setEnabled(false);
		connect_button.setOnClickListener(this);
		
		if (btAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	public void onStart() {
		super.onStart();
		if (!btAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			setupBt();
		}

	}

	private void setupBt() {
		findViewById(R.id.trial_connect_button).setEnabled(true);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				setupBt();
			} else {
				Toast.makeText(this, "Bluetooth not enabled.",
						Toast.LENGTH_LONG).show();
				finish();
			}
			break;
		case DEVICE_CONNECT:
			device = btAdapter.getRemoteDevice(data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS));
			Toast.makeText(this, device.getName(),
					Toast.LENGTH_LONG).show();
			break;
		}
	}

	public void onClick(View v) {
		Log.d(TAG, "clicked");
		switch(v.getId()){
		case R.id.trial_back_button:
			finish();
			break;
		case R.id.trial_connect_button:
			startActivityForResult(new Intent(this, DeviceListActivity.class), DEVICE_CONNECT);
		}
	}
}
