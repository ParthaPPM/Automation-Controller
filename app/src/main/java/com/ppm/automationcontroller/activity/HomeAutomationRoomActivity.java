package com.ppm.automationcontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ppm.automationcontroller.R;
import com.ppm.automationcontroller.data.DbHelper;
import com.ppm.automationcontroller.home.automation.Device;
import com.ppm.automationcontroller.home.automation.DeviceAdapter;

public class HomeAutomationRoomActivity extends AppCompatActivity
{
	private int roomId;
	private String roomName;
	private String roomType;

	private SwipeRefreshLayout refreshLayout;
	private ListView deviceListView;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_automation_room);
		Bundle bundle = getIntent().getExtras();
		roomId = bundle.getInt("room_id");
		roomName = bundle.getString("room_name");
		roomType = bundle.getString("room_type");
		setTitle(roomName);

		refreshLayout = findViewById(R.id.refresh_layout);
		deviceListView = findViewById(R.id.devices_list);
		View emptyView = findViewById(R.id.empty_device_view);
		FloatingActionButton addButton = findViewById(R.id.add_button);

		refreshLayout.setOnRefreshListener(() -> updateDeviceList(true));
		updateDeviceList(false);
		deviceListView.setOnItemClickListener((parent, view, position, id) ->
		{
			Device device = (Device) parent.getItemAtPosition(position);
			Bundle deviceDetailsBundle = new Bundle();
			deviceDetailsBundle.putInt("room_id", roomId);
			deviceDetailsBundle.putString("room_name", roomName);
			deviceDetailsBundle.putString("room_type", roomType);
			deviceDetailsBundle.putInt("device_id", device.getId());
			deviceDetailsBundle.putString("device_name", device.getName());
			deviceDetailsBundle.putString("device_type", device.getType());
			deviceDetailsBundle.putString("ip_address", device.getIpAddress());
			Intent i = new Intent(HomeAutomationRoomActivity.this, EditDeviceActivity.class);
			i.putExtras(deviceDetailsBundle);
			startActivity(i);
		});
		deviceListView.setEmptyView(emptyView);
		addButton.setOnClickListener(v ->
		{
			Bundle deviceDetailsBundle = new Bundle();
			deviceDetailsBundle.putInt("room_id", roomId);
			deviceDetailsBundle.putString("room_name", roomName);
			deviceDetailsBundle.putString("room_type", roomType);
			deviceDetailsBundle.putInt("device_id", 0);
			deviceDetailsBundle.putString("device_name", "");
			deviceDetailsBundle.putString("device_type", "");
			deviceDetailsBundle.putString("ip_address", "");
			Intent i = new Intent(HomeAutomationRoomActivity.this, EditDeviceActivity.class);
			i.putExtras(deviceDetailsBundle);
			startActivity(i);
		});
	}

	protected void onResume()
	{
		super.onResume();
		updateDeviceList(false);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.home_automation_room_activity_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemId = item.getItemId();
		if (itemId == R.id.refresh)
		{
			updateDeviceList(true);
			return true;
		}
		else if (itemId == R.id.edit)
		{
			Bundle roomDetailsBundle = new Bundle();
			roomDetailsBundle.putInt("room_id", roomId);
			roomDetailsBundle.putString("room_name", roomName);
			roomDetailsBundle.putString("room_type", roomType);
			Intent i = new Intent(HomeAutomationRoomActivity.this, EditRoomActivity.class);
			i.putExtras(roomDetailsBundle);
			startActivity(i);
			return true;
		}
		else if (itemId == R.id.delete)
		{
			deleteRoom();
			return true;
		}
		else
		{
			return super.onOptionsItemSelected(item);
		}
	}

	private void updateDeviceList(boolean showToast)
	{
		refreshLayout.setRefreshing(true);
		Thread t = new Thread(() ->
		{
			DbHelper dbHelper = new DbHelper(this);
			DeviceAdapter adapter = new DeviceAdapter(this, dbHelper.getDevices(roomId));
			dbHelper.close();
			runOnUiThread(() ->
			{
				deviceListView.setAdapter(adapter);
				refreshLayout.setRefreshing(false);
				if (showToast)
				{
					Toast.makeText(this, R.string.update_complete, Toast.LENGTH_SHORT).show();
				}
			});
		});
		t.start();
	}

	private void deleteRoom()
	{
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		alertBuilder.setTitle(R.string.delete_room_alert_title);
		alertBuilder.setMessage(R.string.delete_room_alert_message);
		alertBuilder.setPositiveButton(R.string.delete, (dialogInterface, i) ->
		{
			Thread t = new Thread(() ->
			{
				DbHelper dbHelper = new DbHelper(this);
				dbHelper.deleteRoom(roomId);
				dbHelper.close();
				runOnUiThread(() -> Toast.makeText(this, R.string.room_delete_complete, Toast.LENGTH_SHORT).show());
			});
			t.start();
			finish();
		});
		alertBuilder.setNegativeButton(R.string.cancel, ((dialogInterface, i) -> dialogInterface.dismiss()));
		alertBuilder.create().show();
	}
}