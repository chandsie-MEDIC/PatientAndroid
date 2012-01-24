package com.shreyaschand.MEDIC;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class About extends Activity implements OnClickListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		View backButtonView = findViewById(R.id.about_back_button);
		backButtonView.setOnClickListener(this);

	}

	public void onClick(View v) {

		finish();

	}
}
