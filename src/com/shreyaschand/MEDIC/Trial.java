package com.shreyaschand.MEDIC;

import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Trial extends Activity implements OnClickListener {
	
	private BluetoothAdapter btAdapter;

	public BluetoothSocket socket;

	private static final int DEVICE_SELECT = 1;
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
			findViewById(R.id.trial_connect_button).setEnabled(true);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				findViewById(R.id.trial_connect_button).setEnabled(true);
			} else {
				Toast.makeText(this, "Bluetooth not enabled.",
						Toast.LENGTH_LONG).show();
				finish();
			}
			break;
		case DEVICE_SELECT:
			new connectBT().execute(new String[] {data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)});
			break;
		}
	}
	
	private class connectBT extends AsyncTask<String, Void, Boolean> {

		protected Boolean doInBackground(String... mac) {
			try {
				BluetoothDevice device = btAdapter.getRemoteDevice(mac[0]);
				Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				socket = (BluetoothSocket) m.invoke(device, 1);
				socket.connect();
			return true;
			} catch (Exception e) {
				return false;
			}
		}
		protected void onPostExecute(Boolean result) {
			if(result) {
				((TextView)findViewById(R.id.test_output)).setText("connected.");
				findViewById(R.id.trial_connect_button).setEnabled(false);
			}else{
				((TextView)findViewById(R.id.test_output)).setText("error connecting.");
			}
		}
		
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.trial_back_button:
			finish();
			break;
		case R.id.trial_connect_button:
			startActivityForResult(new Intent(this, DeviceListActivity.class), DEVICE_SELECT);
		}
	}
}