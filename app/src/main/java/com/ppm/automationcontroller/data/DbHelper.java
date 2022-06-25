package com.ppm.automationcontroller.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ppm.automationcontroller.data.DatabaseContract.HomeController;
import com.ppm.automationcontroller.home.automation.Device;
import com.ppm.automationcontroller.home.automation.Room;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "automation_controller.db";

	public DbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase database)
	{
		String CREATE_HOME_CONTROLLER_TABLE_SQL = "CREATE TABLE " + HomeController.TABLE_NAME + " ("
				+ HomeController._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ HomeController.ROOM_ID_COLUMN_NAME + " INTEGER NOT NULL, "
				+ HomeController.ROOM_NAME_COLUMN_NAME + " TEXT NOT NULL, "
				+ HomeController.ROOM_TYPE_COLUMN_NAME + " TEXT NOT NULL,"
				+ HomeController.DEVICE_ID_COLUMN_NAME + " INTEGER, "
				+ HomeController.DEVICE_NAME_COLUMN_NAME + " TEXT, "
				+ HomeController.DEVICE_TYPE_COLUMN_NAME + " TEXT, "
				+ HomeController.IP_ADDRESS_COLUMN_NAME + " TEXT);";
		database.execSQL(CREATE_HOME_CONTROLLER_TABLE_SQL);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{}

	public ArrayList<Room> getRooms()
	{
		ArrayList<Room> roomsList = new ArrayList<>();
		String[] columns = {HomeController.ROOM_ID_COLUMN_NAME,
				HomeController.ROOM_NAME_COLUMN_NAME,
				HomeController.ROOM_TYPE_COLUMN_NAME,
				"count("+ HomeController.DEVICE_ID_COLUMN_NAME +")"};
		String groupBy = HomeController.ROOM_ID_COLUMN_NAME;
		Cursor cursor = getCursor(columns, null, null, groupBy);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String type = cursor.getString(2);
				int noOfDevices = cursor.getInt(3);
				roomsList.add(new Room(id, name, type, noOfDevices-1));
			}
			cursor.close();
		}
		return roomsList;
	}

	public boolean addRoom(String roomName, String roomType)
	{
		int roomId = getMaxId(HomeController.ROOM_ID_COLUMN_NAME);
		return add(roomId, roomName, roomType, 0, "", "", "");
	}

	public boolean editRoom(int roomId, String roomName, String roomType)
	{
		ContentValues values = new ContentValues();
		values.put(HomeController.ROOM_NAME_COLUMN_NAME, roomName);
		values.put(HomeController.ROOM_TYPE_COLUMN_NAME, roomType);
		return update(values, HomeController.ROOM_ID_COLUMN_NAME, roomId);
	}

	public boolean deleteRoom(int roomId)
	{
		return delete(HomeController.ROOM_ID_COLUMN_NAME, roomId);
	}

	public ArrayList<Device> getDevices(int roomId)
	{
		ArrayList<Device> devicesList = new ArrayList<>();
		String[] columns = {HomeController.DEVICE_ID_COLUMN_NAME,
				HomeController.DEVICE_NAME_COLUMN_NAME,
				HomeController.DEVICE_TYPE_COLUMN_NAME,
				HomeController.IP_ADDRESS_COLUMN_NAME};
		String selection = HomeController.ROOM_ID_COLUMN_NAME + " = ?";
		String[] selectionArgs = {String.valueOf(roomId)};
		Cursor cursor = getCursor(columns, selection, selectionArgs, null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String type = cursor.getString(2);
				String ipAddress = cursor.getString(3);
				if (id != 0)
				{
					devicesList.add(new Device(id, name, type, ipAddress));
				}
			}
			cursor.close();
		}
		return devicesList;
	}

	public boolean addDevice(int roomId, String roomName, String roomType, String deviceName, String deviceType, String ipAddress)
	{
		int deviceId = getMaxId(HomeController.DEVICE_ID_COLUMN_NAME);
		return add(roomId, roomName, roomType, deviceId, deviceName, deviceType, ipAddress);
	}

	public boolean editDevice(int deviceId, String deviceName, String deviceType, String ipAddress)
	{
		ContentValues values = new ContentValues();
		values.put(HomeController.DEVICE_NAME_COLUMN_NAME, deviceName);
		values.put(HomeController.DEVICE_TYPE_COLUMN_NAME, deviceType);
		values.put(HomeController.IP_ADDRESS_COLUMN_NAME, ipAddress);
		return update(values, HomeController.DEVICE_ID_COLUMN_NAME, deviceId);
	}

	public boolean deleteDevice(int deviceId)
	{
		return delete(HomeController.DEVICE_ID_COLUMN_NAME, deviceId);
	}

	private Cursor getCursor(String[] columns, String selection, String[] selectionArgs, String groupBy)
	{
		try
		{
			SQLiteDatabase database = getReadableDatabase();
			return database.query(HomeController.TABLE_NAME, columns, selection, selectionArgs, groupBy, null, null);
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	private int getMaxId(String roomOrDeviceIdColumnName)
	{
		try
		{
			SQLiteDatabase database = getReadableDatabase();

			String[] columns = {"max(" + roomOrDeviceIdColumnName + ")"};
			Cursor cursor = database.query(HomeController.TABLE_NAME, columns, null, null, null, null, null);
			int maxId;
			if (cursor.moveToNext())
			{
				maxId = cursor.getInt(0) + 1;
			}
			else
			{
				maxId = 1;
			}
			cursor.close();
			return maxId;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	private boolean add(int roomId, String roomName, String roomType, int deviceId, String deviceName, String deviceType, String ipAddress)
	{
		try
		{
			SQLiteDatabase database = getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(HomeController.ROOM_ID_COLUMN_NAME, roomId);
			values.put(HomeController.ROOM_NAME_COLUMN_NAME, roomName);
			values.put(HomeController.ROOM_TYPE_COLUMN_NAME, roomType);
			values.put(HomeController.DEVICE_ID_COLUMN_NAME, deviceId);
			values.put(HomeController.DEVICE_NAME_COLUMN_NAME, deviceName);
			values.put(HomeController.DEVICE_TYPE_COLUMN_NAME, deviceType);
			values.put(HomeController.IP_ADDRESS_COLUMN_NAME, ipAddress);
			long newRowId = database.insert(HomeController.TABLE_NAME, null, values);
			return (newRowId != -1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private boolean update(ContentValues values, String roomOrDeviceIdColumnName, int roomOrDeviceId)
	{
		try
		{
			SQLiteDatabase database = getWritableDatabase();

			String whereClause = roomOrDeviceIdColumnName + " = ?";
			String[] whereArgs = {String.valueOf(roomOrDeviceId)};
			int noOfRowsUpdated = database.update(HomeController.TABLE_NAME, values, whereClause, whereArgs);
			return (noOfRowsUpdated != 0);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private boolean delete(String roomOrDeviceIdColumnName, int roomOrDeviceId)
	{
		try
		{
			SQLiteDatabase database = getReadableDatabase();

			String whereClause = roomOrDeviceIdColumnName + " = ?";
			String[] whereArgs = {String.valueOf(roomOrDeviceId)};
			int noOfRowsDeleted = database.delete(HomeController.TABLE_NAME, whereClause, whereArgs);
			return (noOfRowsDeleted != 0);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
}