package com.shreyaschand.MEDIC;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MEDICActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        View aboutButton = findViewById(R.id.home_about_button);
        aboutButton.setOnClickListener(this);
    }
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_about_button:
			Intent i = new Intent(this, About.class);
			startActivity(i);
			break;
		}
		
	}
}