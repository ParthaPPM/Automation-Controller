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

public class EditRoomActivity extends AppCompatActivity
{
	private int roomId;
	private String roomName;
	private String roomType;
	private boolean isNewRoom;

	private EditText nameEditText;
	private MaterialAutoCompleteTextView typeEditText;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_room);
		Bundle bundle = getIntent().getExtras();
		if (bundle == null)
		{
			isNewRoom = true;
			setTitle(R.string.add_room_activity_name);
			roomId = 0;
			roomName = "";
			roomType = "";
		}
		else
		{
			isNewRoom = false;
			roomId = bundle.getInt("room_id");
			roomName = bundle.getString("room_name");
			roomType = bundle.getString("room_type");
		}

		nameEditText = findViewById(R.id.room_name_edit_text);
		typeEditText = findViewById(R.id.room_type_selector);

		nameEditText.setText(roomName);
		ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.room_names_list, android.R.layout.simple_spinner_dropdown_item);
		typeEditText.setAdapter(typeAdapter);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.editor_acitivity_menu, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		if (isNewRoom)
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
			roomName = nameEditText.getText().toString().trim();
			roomType = typeEditText.getText().toString().trim();
			addOrEditRoom();
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

	private void addOrEditRoom()
	{
		if (roomName.equals(""))
		{
			Toast.makeText(this, R.string.room_name_empty_error_message, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Thread t = new Thread(() ->
			{
				DbHelper dbHelper = new DbHelper(this);
				if (isNewRoom)
				{
					dbHelper.addRoom(roomName, roomType);
				}
				else
				{
					dbHelper.editRoom(roomId, roomName, roomType);
				}
				dbHelper.close();
			});
			t.start();
			finish();
		}
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