package com.ppm.automationcontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ppm.automationcontroller.R;
import com.ppm.automationcontroller.data.DbHelper;
import com.ppm.automationcontroller.home.automation.Room;
import com.ppm.automationcontroller.home.automation.RoomAdapter;

public class HomeAutomationActivity extends AppCompatActivity
{
	private SwipeRefreshLayout refreshLayout;
	private ListView roomsListView;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_automation);

		refreshLayout = findViewById(R.id.refresh_layout);
		roomsListView = findViewById(R.id.room_list);
		View emptyView = findViewById(R.id.empty_room_view);
		FloatingActionButton addButton = findViewById(R.id.add_button);

		refreshLayout.setOnRefreshListener(() -> updateRoomList(true));
		updateRoomList(false);
		roomsListView.setOnItemClickListener((parent, view, position, id) ->
		{
			Room room = (Room) parent.getItemAtPosition(position);
			Bundle bundle = new Bundle();
			bundle.putInt("room_id", room.getId());
			bundle.putString("room_name", room.getName());
			bundle.putString("room_type", room.getType());
			Intent i = new Intent(HomeAutomationActivity.this, HomeAutomationRoomActivity.class);
			i.putExtras(bundle);
			startActivity(i);
		});
		roomsListView.setEmptyView(emptyView);
		addButton.setOnClickListener(v ->
		{
			Intent i = new Intent(HomeAutomationActivity.this, EditRoomActivity.class);
			startActivity(i);
		});
	}

	protected void onResume()
	{
		super.onResume();
		updateRoomList(false);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.home_automation_activity_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		int itemID = item.getItemId();
		if (itemID == R.id.refresh)
		{
			updateRoomList(true);
			return true;
		}
		else
		{
			return super.onOptionsItemSelected(item);
		}
	}

	private void updateRoomList(boolean showToast)
	{
		refreshLayout.setRefreshing(true);
		Thread t = new Thread(() ->
		{
			DbHelper dbHelper = new DbHelper(this);
			RoomAdapter adapter = new RoomAdapter(this, dbHelper.getRooms());
			dbHelper.close();
			runOnUiThread(() ->
			{
				roomsListView.setAdapter(adapter);
				refreshLayout.setRefreshing(false);
				if (showToast)
				{
					Toast.makeText(this, R.string.update_complete, Toast.LENGTH_SHORT).show();
				}
			});
		});
		t.start();
	}
}