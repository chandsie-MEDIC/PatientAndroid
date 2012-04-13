package com.shreyaschand.MEDIC.Patient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class Doctor extends Activity implements OnClickListener {

	TextView tView;
	EditText input;
	Socket sock;
	PrintWriter writer;
			
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doctor);

		findViewById(R.id.doctor_send_button).setOnClickListener(this);

		tView = (TextView) findViewById(R.id.doctor_test_output);
		input = (EditText) findViewById(R.id.doctor_message);
		
		tView.append("Connecting to server now...\n\n");	
		
	}
	
	public void onStart(){
		super.onStart();
		try {	
			sock = new Socket();
			sock.connect(new InetSocketAddress("chands.dyndns-server.com", 1028), 5000);
			writer = new PrintWriter(sock.getOutputStream(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick(View v) {
		String m = input.getText().toString();
		if(m.equals("")){
			return;
		} else {
			writer.println(m);
			tView.append(m + "\n");
			if (m.equals(".exit")) {
				try {
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer.close();		
			}
		}
		input.setText("");
	}
}
