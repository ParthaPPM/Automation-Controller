package com.ppm.automationcontroller.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.ppm.automationcontroller.R;
import com.ppm.automationcontroller.data.DbHelper;

public class EditDeviceActivity extends AppCompatActivity
{
	private int roomId;
	private String roomName;
	private String roomType;
	private int deviceId;
	private String deviceName;
	private String deviceType;
	private String ipAddress;
	private boolean isNewDevice;

	private EditText nameEditText;
	private MaterialAutoCompleteTextView typeEditText;
	private EditText ipAddressEditText;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_device);
		Bundle bundle = getIntent().getExtras();
		roomId = bundle.getInt("room_id");
		roomName = bundle.getString("room_name");
		roomType = bundle.getString("room_type");
		deviceId = bundle.getInt("device_id");
		deviceName = bundle.getString("device_name");
		deviceType = bundle.getString("device_type");
		ipAddress = bundle.getString("ip_address");

		if (deviceId == 0)
		{
			isNewDevice = true;
			setTitle(R.string.add_device_activity_name);
		}
		else
		{
			isNewDevice = false;
		}

		nameEditText = findViewById(R.id.device_name_edit_text);
		typeEditText = findViewById(R.id.device_type_selector);
		ipAddressEditText = findViewById(R.id.ip_address_edit_text);

		nameEditText.setText(deviceName);
		ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.device_names_list, android.R.layout.simple_spinner_dropdown_item);
		typeEditText.setAdapter(typeAdapter);
		ipAddressEditText.setText(ipAddress);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.editor_acitivity_menu, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		if (isNewDevice)
		{
			menu.findItem(R.id.delete).setVisible(false);
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		if (itemId == R.id.save)
		{
			deviceName = nameEditText.getText().toString().trim();
			deviceType = typeEditText.getText().toString().trim();
			ipAddress = ipAddressEditText.getText().toString().trim();
			addOrEditDevice();
			return true;
		}
		else if (itemId == R.id.delete)
		{
			deleteDevice();
			return true;
		}
		else
		{
			return super.onOptionsItemSelected(item);
		}
	}

	private void addOrEditDevice()
	{
		if (deviceName.equals(""))
		{
			Toast.makeText(this, R.string.device_name_empty_error_message, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Thread t = new Thread(() ->
			{
				DbHelper dbHelper = new DbHelper(this);
				if (isNewDevice)
				{
					dbHelper.addDevice(roomId, roomName, roomType, deviceName, deviceType, ipAddress);
				}
				else
				{
					dbHelper.editDevice(deviceId, deviceName, deviceType, ipAddress);
				}
				dbHelper.close();
			});
			t.start();
			finish();
		}
	}

	private void deleteDevice()
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(R.string.delete_device_alert_title);
		alertBuilder.setMessage(R.string.delete_device_alert_message);
		alertBuilder.setPositiveButton(R.string.delete, (dialogInterface, i) ->
		{
			Thread t = new Thread(() ->
			{
				DbHelper dbHelper = new DbHelper(this);
				dbHelper.deleteRoom(deviceId);
				dbHelper.close();
				runOnUiThread(() -> Toast.makeText(this, R.string.device_delete_complete, Toast.LENGTH_SHORT).show());
			});
			t.start();
			finish();
		});
		alertBuilder.setNegativeButton(R.string.cancel, ((dialogInterface, i) -> dialogInterface.dismiss()));
		alertBuilder.create().show();
	}
}