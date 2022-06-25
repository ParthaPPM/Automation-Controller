package com.ppm.automationcontroller.home.automation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ppm.automationcontroller.R;

import java.util.ArrayList;

public class RoomAdapter extends ArrayAdapter<Room>
{
	public RoomAdapter(Activity context, ArrayList<Room> rooms)
	{
		super(context, 0, rooms);
	}

	public View getView(int position, View convertView, ViewGroup parentView)
	{
		View listItemView;
		if(convertView == null)
		{
			listItemView = LayoutInflater.from(getContext()).inflate(R.layout.room_list_item, parentView, false);
		}
		else
		{
			listItemView = convertView;
		}

		ImageView roomImageView = listItemView.findViewById(R.id.room_image);
		TextView roomNameTextView = listItemView.findViewById(R.id.room_name_text_view);
		TextView roomTypeTextView = listItemView.findViewById(R.id.room_type_text_view);
		TextView noOfDevicesTextView = listItemView.findViewById(R.id.no_of_devices_text_view);

		Room currentRoom = getItem(position);
		String roomType = currentRoom.getType();
		String roomTypeDrawingRoom = getContext().getString(R.string.drawing_room);
		String roomTypeBedroom = getContext().getString(R.string.bed_room);
		String roomTypeKitchen = getContext().getString(R.string.kitchen);
		String roomTypeBathroom = getContext().getString(R.string.bathroom);

		if(roomType.equals(roomTypeDrawingRoom))
		{
			roomImageView.setImageResource(R.drawable.image_drawing_room);
		}
		else if(roomType.equals(roomTypeBedroom))
		{
			roomImageView.setImageResource(R.drawable.image_bed_room);
		}
		else if (roomType.equals(roomTypeKitchen))
		{
			roomImageView.setImageResource(R.drawable.image_kitchen);
		}
		else if (roomType.equals(roomTypeBathroom))
		{
			roomImageView.setImageResource(R.drawable.image_bathroom);
		}
		else
		{
			roomImageView.setImageResource(R.drawable.image_unknown);
		}
		roomNameTextView.setText(currentRoom.getName());
		roomTypeTextView.setText(currentRoom.getType());
		noOfDevicesTextView.setText(String.valueOf(currentRoom.getNoOfDevices()));

		return listItemView;
	}

	
}