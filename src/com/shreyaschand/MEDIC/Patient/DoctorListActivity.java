package com.shreyaschand.MEDIC.Patient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DoctorListActivity extends Activity implements OnItemClickListener {

	public static String EXTRA_DOCTOR_SOCKET = "doctor_socket";
	private DoctorSocket docSocket;
	private HashMap<String, String> docs;
	private ArrayAdapter<String> doctorsArrayAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.doctor_list);

		setResult(Activity.RESULT_CANCELED);

		Button refreshButton = (Button) findViewById(R.id.button_refresh);
		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				fetchDoctorList();
			}
		});

		doctorsArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.doctor_name);

		ListView doctorListView = (ListView) findViewById(R.id.available_doctors);
		doctorListView.setAdapter(doctorsArrayAdapter);
		doctorListView.setOnItemClickListener(this);

	}

	private void fetchDoctorList() {
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.fetching);
		new SocketManager().execute();
	}

	private class SocketManager extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			if (docSocket == null) {
				try {
					Socket sock = new Socket("chands.dyndns-server.com", 1028);
					PrintWriter writer = new PrintWriter(
							sock.getOutputStream(), true);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(sock.getInputStream()));
					docSocket = new DoctorSocket(sock, writer, reader);
				} catch (IOException e) {e.printStackTrace();}
			}
			docSocket.writer.println("$list");
			doctorsArrayAdapter.clear();
			try {
				String[] doctors = docSocket.reader.readLine().split("\\$");
				for(String doc: doctors){
					if(!doc.equals("")){
						String text = queryServer(doc);
						docs.put(text, doc);
						doctorsArrayAdapter.add(text);
					}
				}
			} catch (Exception e) {e.printStackTrace();}
			if(doctorsArrayAdapter.isEmpty()){
				doctorsArrayAdapter.add(getResources().getText(R.string.none_available).toString());
			}
			return null;
		}
		
		protected void onPostExecute(Void result){
			setTitle("");
		}
		
		protected String queryServer(String uname){
			String response = "";
			try {
				String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode("shreyas", "UTF-8") + "&" 
							+ URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode("Brasilia", "UTF-8");
				URLConnection conn = new URL("http://chands.dyndns-server.com:4080/user/" + uname).openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();
				wr.close();
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
				String[] responses = br.readLine().split("\\$");
				response = responses[0] + "\n" + responses[1]; 
				br.close();
			} catch (IOException e) {e.printStackTrace();}
			return response;	
		}

	}

	public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
		String info = ((TextView) v).getText().toString();
		
		Intent intent = new Intent();
		intent.putExtra(EXTRA_DOCTOR_SOCKET, docs.get(info));

		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
