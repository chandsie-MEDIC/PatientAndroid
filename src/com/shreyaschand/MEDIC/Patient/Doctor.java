package com.shreyaschand.MEDIC.Patient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Doctor extends Activity implements OnClickListener {

	private BluetoothAdapter btAdapter;
	public BluetoothSocket btSocket;
	private DoctorSocket docSocket;
	private String btDeviceMac;
	private boolean firstTimer = true;
	
	private TextView output = null;
	private ScrollView scroller = null;

	private static final int DEVICE_SELECT = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int DOCTOR_SELECT = 3;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctor);

		View backButtonView = findViewById(R.id.doctor_back_button);
		backButtonView.setOnClickListener(this);

		output = ((TextView) findViewById(R.id.doctor_output));
		scroller = (ScrollView) findViewById(R.id.doctor_scroller);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		Button connect_button = (Button) findViewById(R.id.doctor_button);
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
			findViewById(R.id.doctor_button).setEnabled(true);
		}

	}

	public void onResume() {
		super.onResume();
		if(!firstTimer)
		{
			if (!btAdapter.isEnabled()) {

				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}
			if(btSocket == null) {
				new ConnectBT().execute(new String[] { btDeviceMac });
				return;
			}
			try {
				btSocket.getInputStream();
				btSocket.getOutputStream();
			} catch (IOException e) {
				new ConnectBT().execute(new String[] { btDeviceMac });
				return;
			}
		}
	}
	
//	public void onPause() {
//		super.onPause();
//		try {
//			btSocket.close();
//			output.append("Connection terminated.");
//		} catch (IOException e) {
//			output.append("Error closing socket.\nAssuming already closed.");
//		} catch (NullPointerException e) {} //Never got to create a socket.
//		btSocket = null;
//	}

	public void onDestroy() {
		super.onDestroy();
		try {
			btSocket.close();
			output.append("Connection terminated.");
		} catch (IOException e) {
			output.append("Error closing socket.\nAssuming already closed.");
		} catch (NullPointerException e) {} //Never got to create a socket.
		btSocket = null;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				findViewById(R.id.doctor_button).setEnabled(true);
			} else {
				Toast.makeText(this, "Bluetooth not enabled.",
						Toast.LENGTH_LONG).show();
				finish();
			}
			break;
		case DEVICE_SELECT:
			if (resultCode == Activity.RESULT_OK) {
				btDeviceMac = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				new ConnectBT().execute(new String[] { btDeviceMac });
			}
			break;
		case DOCTOR_SELECT:
			if (resultCode == Activity.RESULT_OK) {
				docSocket = DoctorListActivity.docSocket;//(DoctorSocket) data.getExtras().get(DoctorListActivity.EXTRA_DOCTOR_SOCKET);
				new Communicator().execute();
			}
			break;
		}
		
	}

	private class ConnectBT extends AsyncTask<String, Void, Boolean> {

		protected Boolean doInBackground(String... mac) {
			firstTimer = false;
			try {
				publishProgress();
				BluetoothDevice device = btAdapter.getRemoteDevice(mac[0]);
				Method m = device.getClass().getMethod("createRfcommSocket",
						new Class[] { int.class });
				btSocket = (BluetoothSocket) m.invoke(device, 1);
				btSocket.connect();
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		protected void onProgressUpdate(Void... updates) {
			output.append("Connecting...");
			scroller.fullScroll(ScrollView.FOCUS_DOWN);
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				output.setText("connected.");
				((TextView) findViewById(R.id.doctor_button)).setText(R.string.find_doctor);			
			} else {
				output.setText("error connecting.");
			}
		}

	}
		
	private class Communicator extends
			AsyncTask<Void, String, Void> {
		protected Void doInBackground(Void... params) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
				long startTime = System.currentTimeMillis();
				long time = System.currentTimeMillis();
				String input = in.readLine();
				while (input != null && (time - startTime) < 5000) {
					time = System.currentTimeMillis();
					publishProgress(new String[] { time - startTime + ": " + input});
					docSocket.writer.println(input);
					docSocket.writer.flush();
					input = in.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(String... update) {
			output.append("\n" + update[0]);
			scroller.fullScroll(ScrollView.FOCUS_DOWN);
		}
	}

	public void onClick(View v) {
		String action = ((TextView) v).getText().toString();
		if (action == getResources().getString(R.string.connect)) {
			startActivityForResult(new Intent(this, DeviceListActivity.class),DEVICE_SELECT);
		} else if (action == getResources().getString(R.string.find_doctor)) {
			startActivityForResult(new Intent(this, DoctorListActivity.class),DOCTOR_SELECT);
		}
	}
}
