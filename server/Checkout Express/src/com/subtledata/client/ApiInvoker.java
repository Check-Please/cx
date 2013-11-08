package com.subtledata.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.client.WebResource.Builder;

import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;

public class ApiInvoker {
	private static ApiInvoker INSTANCE = new ApiInvoker();
	private Map<String, Client> hostMap = new HashMap<String, Client>();
	private Map<String, String> defaultHeaderMap = new HashMap<String, String>();

	public static ApiInvoker getInstance() {
		return INSTANCE;
	}

	public void addDefaultHeader(String key, String value) {
		defaultHeaderMap.put(key, value);
	}

	public String escapeString(String str) {
		return str;
	}

	public static String serialize(JSONObject obj) throws ApiException {
		return obj == null ? null : obj.toString();
	}

	public String invokeAPI(String host, String path, String method, Map<String, String> queryParams, JSONObject body, Map<String, String> headerParams) throws ApiException {
		Client client = getClient(host);

		StringBuilder b = new StringBuilder();

		for(String key : queryParams.keySet()) {
			String value = queryParams.get(key);
			if (value != null){
				if(b.toString().length() == 0) b.append("?");
				else b.append("&");
				b.append(escapeString(key)).append("=").append(escapeString(value));
			}
		}
		String querystring = b.toString();

		Builder builder = client.resource(host + path + querystring).type("application/json");
		for(String key : headerParams.keySet()) {
			builder.header(key, headerParams.get(key));
		}

		for(String key : defaultHeaderMap.keySet()) {
			if(!headerParams.containsKey(key)) {
				builder.header(key, defaultHeaderMap.get(key));
			}
		}
		ClientResponse response = null;

		if("GET".equals(method)) {
			response = (ClientResponse) builder.get(ClientResponse.class);
		}
		else if ("POST".equals(method)) {
			response = builder.post(ClientResponse.class, serialize(body));
		}
		else if ("PUT".equals(method)) {
			response = builder.put(ClientResponse.class, serialize(body));
		}
		else if ("DELETE".equals(method)) {
			response = builder.delete(ClientResponse.class, serialize(body));
		}
		else {
			throw new ApiException(500, "unknown method type " + method);
		}
		if(response.getClientResponseStatus() == ClientResponse.Status.OK) {
			return (String) response.getEntity(String.class);
		}
		else {
			throw new ApiException(
					response.getClientResponseStatus().getStatusCode(),
					response.getEntity(String.class));      
		}
	}

	private Client getClient(String host) {
		if(!hostMap.containsKey(host)) {
			Client client = Client.create();
			client.addFilter(new LoggingFilter());
			hostMap.put(host, client);
		}
		return hostMap.get(host);
	}
}

