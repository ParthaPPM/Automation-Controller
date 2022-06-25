package com.ppm.automationcontroller.home.automation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ppm.automationcontroller.R;
import com.ppm.automationcontroller.activity.HomeAutomationRoomActivity;
import com.ppm.http.client.HttpClientHelper;
import com.ppm.http.client.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeviceAdapter extends ArrayAdapter<Device>
{
	public DeviceAdapter(Activity context, ArrayList<Device> devices)
	{
		super(context, 0, devices);
	}

	public View getView(int position, View convertView, ViewGroup parentView)
	{
		View listItemView;
		if(convertView == null)
		{
			listItemView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_item, parentView, false);
		}
		else
		{
			listItemView = convertView;
		}

		ImageView deviceImageView = listItemView.findViewById(R.id.device_image);
		TextView deviceNameTextView = listItemView.findViewById(R.id.device_name_text_view);
		TextView deviceTypeTextView = listItemView.findViewById(R.id.device_type_text_view);
		TextView ipAddressTextView = listItemView.findViewById(R.id.ip_address_text_view);
		Button onOffButton = listItemView.findViewById(R.id.on_off_button);

		Device currentDevice = getItem(position);
		String deviceType = currentDevice.getType();
		String deviceTypeLight = getContext().getString(R.string.light);
		String deviceTypeFan = getContext().getString(R.string.fan);
		String deviceTypeNightLight = getContext().getString(R.string.night_bulb);

		if(deviceType.equals(deviceTypeLight))
		{
			deviceImageView.setImageResource(R.drawable.image_light);
		}
		else if(deviceType.equals(deviceTypeFan))
		{
			deviceImageView.setImageResource(R.drawable.image_fan);
		}
		else if (deviceType.equals(deviceTypeNightLight))
		{
			deviceImageView.setImageResource(R.drawable.image_nightlight);
		}
		else
		{
			deviceImageView.setImageResource(R.drawable.image_unknown);
		}
		deviceNameTextView.setText(currentDevice.getName());
		deviceTypeTextView.setText(deviceType);
		ipAddressTextView.setText(currentDevice.getIpAddress());
		onOffButton.setOnClickListener(view -> sendRequest(currentDevice.getIpAddress()));

		return listItemView;
	}

	private void sendRequest(String ipAddress)
	{
		String message = "toggle";
		Thread t = new Thread(() ->
		{
			String url = "http://" + ipAddress;
			Map<String, String> parametersMap = new HashMap<>();
			parametersMap.put("message", message);
			Response response = HttpClientHelper.get(url, parametersMap);
			int responseCode = response.getResponseCode();

			int toastMessage;
			if (responseCode == 0)
			{
				toastMessage = R.string.send_error;
			}
			else if (responseCode == 200)
			{
				toastMessage = R.string.send_success;
			}
			else
			{
				toastMessage = R.string.send_failure;
			}
			((HomeAutomationRoomActivity)getContext()).runOnUiThread(() -> Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT).show());
		});
		t.start();
	}
}
