package com.ppm.automationcontroller.data;

import android.provider.BaseColumns;

public class DatabaseContract
{
	private DatabaseContract()
	{}

	public static class HomeController implements BaseColumns
	{
		public static final String TABLE_NAME = "home_controller_room";
		public static String _ID = BaseColumns._ID;
		public static String ROOM_ID_COLUMN_NAME = "room_id";
		public static String ROOM_NAME_COLUMN_NAME = "room_name";
		public static String ROOM_TYPE_COLUMN_NAME = "room_type";
		public static String DEVICE_ID_COLUMN_NAME = "device_id";
		public static String DEVICE_NAME_COLUMN_NAME = "device_name";
		public static String DEVICE_TYPE_COLUMN_NAME = "device_type";
		public static String IP_ADDRESS_COLUMN_NAME = "ip_address";
	}
}