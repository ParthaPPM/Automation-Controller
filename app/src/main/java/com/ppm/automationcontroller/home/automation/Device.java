package com.ppm.automationcontroller.home.automation;

public class Device
{
	private final int id;
	private final String name;
	private final String type;
	private final String ipAddress;

	public Device(int id, String name, String type, String ipAddress)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.ipAddress = ipAddress;
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

	public String getIpAddress()
	{
		return ipAddress;
	}
}