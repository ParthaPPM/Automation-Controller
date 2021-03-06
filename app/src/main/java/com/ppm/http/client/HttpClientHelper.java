package com.ppm.http.client;

import com.ppm.java.StringJoiner;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpClientHelper
{
	public static Response get(String url)
	{
		return get(url, null);
	}

	public static Response get(String url, Map<String, String> parametersMap)
	{
		return get(url, parametersMap, null);
	}

	public static Response get(String url, Map<String, String> parametersMap, Map<String, String> extraHeaders)
	{
		return request("GET", url, parametersMap, extraHeaders, null);
	}

	public static Response post(String url)
	{
		return post(url, "");
	}

	public static Response post(String url, String body)
	{
		return post(url, null, body);
	}

	public static Response post(String url, Map<String, String> parametersMap)
	{
		return post(url, null, getParametersAsString(parametersMap));
	}

	public static Response post(String url, Map<String, String> parametersMap, Map<String, String> extraHeaders)
	{
		return post(url, extraHeaders, getParametersAsString(parametersMap));
	}

	public static Response post(String url, Map<String, String> extraHeaders, String body)
	{
		return post(url, extraHeaders, body.getBytes(StandardCharsets.UTF_8));
	}

	public static Response post(String url, Map<String, String> extraHeaders, byte[] body)
	{
		return request("POST", url, null, extraHeaders, body);
	}

	private static Response request(String requestMethod, String url, Map<String, String> extraParameters, Map<String, String> extraHeaders, byte[] body)
	{
		try
		{
			// extracting the request parameters
			URL parsedUrl = new URL(url);
			String protocol = parsedUrl.getProtocol();
			String host = parsedUrl.getHost();
			int port = parsedUrl.getPort();
			if(port == -1)
			{
				if(protocol.equals("https"))
				{
					port = 443;
				}
				else
				{
					port = 80;
				}
			}
			String path = parsedUrl.getPath();
			if(path.equals(""))
			{
				path = "/";
			}
			Map<String, String> parametersMap = getParametersAsMap(parsedUrl.getQuery());
			if(extraParameters != null)
			{
				parametersMap.putAll(extraParameters);
			}

			// making the request
			if(protocol.equals("http"))
			{
				HttpClient client = new HttpClient(host, port);
				client.createRequest(requestMethod, path, parametersMap, extraHeaders, body);
				Response response = client.makeRequest();
				client.close();
				return response;
			}
			else
			{
				System.out.println("HTTPS not supported yet.");
				return new Response();
			}
		}
		catch (MalformedURLException e)
		{
			return new Response();
		}
	}

	private static Map<String, String> getParametersAsMap(String query)
	{
		Map<String, String> parametersMap = new HashMap<>();
		if(query != null)
		{
			Scanner sc = new Scanner(query);
			sc.useDelimiter("&");
			while (sc.hasNext())
			{
				String s = sc.next();
				int indexOfEquals = s.indexOf('=');
				String key = s.substring(0, indexOfEquals).trim();
				String value = s.substring(indexOfEquals + 1).trim();
				parametersMap.put(key, value);
			}
		}
		return parametersMap;
	}

	private static String getParametersAsString(Map<String, String> parametersMap)
	{
		com.ppm.java.StringJoiner parameterString = new StringJoiner("&");
		if(parametersMap != null)
		{
			Set<String> keySet = parametersMap.keySet();
			for (String key : keySet)
			{
				try
				{
					String k = URLEncoder.encode(key, "UTF-8");
					String v = URLEncoder.encode(parametersMap.get(key), "UTF-8");
					parameterString.add(k + "=" + v);
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
		}
		return parameterString.toString();
	}
}