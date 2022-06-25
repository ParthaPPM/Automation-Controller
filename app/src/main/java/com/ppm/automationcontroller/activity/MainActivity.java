package com.ppm.automationcontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ppm.automationcontroller.R;

public class MainActivity extends AppCompatActivity
{
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button homeAutomationButton = findViewById(R.id.home_automation_button);
		homeAutomationButton.setOnClickListener(v ->
		{
			Intent i = new Intent(this, HomeAutomationActivity.class);
			startActivity(i);
		});
	}
}