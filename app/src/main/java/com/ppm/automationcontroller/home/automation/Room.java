package com.ppm.automationcontroller.home.automation;

public class Room
{
	private final int id;
	private final String name;
	private final String type;
	private final int noOfDevices;

	public Room(int id, String name, String type, int noOfDevices)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.noOfDevices = noOfDevices;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getType()
	{
		return type;
	}

	public int getNoOfDevices()
	{
		return noOfDevices;
	}
}