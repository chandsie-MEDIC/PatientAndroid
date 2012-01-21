package com.shreyaschand.MEDIC;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Trial extends Activity implements OnClickListener {

	BluetoothAdapter btAdapter;

	private static final int REQUEST_ENABLE_BT = 2;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trial);

		View backButtonView = findViewById(R.id.trial_back_button);
		backButtonView.setOnClickListener(this);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

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
		}
	}

	public void onClick(View v) {
		finish();
	}
}
