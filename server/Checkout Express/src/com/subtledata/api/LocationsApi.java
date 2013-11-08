package com.subtledata.api;

import com.subtledata.client.ApiException;
import com.subtledata.client.ApiInvoker;
import java.util.*;

import kinds.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationsApi {
	private static final String basePath = "https://api.subtledata.com/v1";
	private static final ApiInvoker apiInvoker = ApiInvoker.getInstance();

	public static JSONArray getAllLocations (Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations".replaceAll("\\{format\\}","json");

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(use_cache)))
			queryParams.put("use_cache", String.valueOf(use_cache));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONArray getLocationsNear (Boolean use_cache, Float latitude, Float longitude, Float radius) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/filter/near".replaceAll("\\{format\\}","json");

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(Globals.subtleApiKey == null || latitude == null || longitude == null || radius == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(use_cache)))
			queryParams.put("use_cache", String.valueOf(use_cache));
		if(!"null".equals(String.valueOf(latitude)))
			queryParams.put("latitude", String.valueOf(latitude));
		if(!"null".equals(String.valueOf(longitude)))
			queryParams.put("longitude", String.valueOf(longitude));
		if(!"null".equals(String.valueOf(radius)))
			queryParams.put("radius", String.valueOf(radius));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject getLocation (Integer location_id, Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(use_cache)))
			queryParams.put("use_cache", String.valueOf(use_cache));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONArray getLocationMenu (Integer location_id, Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/menu".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(use_cache)))
			queryParams.put("use_cache", String.valueOf(use_cache));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject getMenuItem (Integer location_id, Integer item_id, Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/menu/items/{item_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "item_id" + "\\}", apiInvoker.escapeString(item_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || item_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(use_cache)))
			queryParams.put("use_cache", String.valueOf(use_cache));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONArray getLocationEmployees (Integer location_id, Integer manager_id, Integer revenue_center_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/employees".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null || manager_id == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(manager_id)))
			queryParams.put("manager_id", String.valueOf(manager_id));
		if(!"null".equals(String.valueOf(revenue_center_id)))
			queryParams.put("revenue_center_id", String.valueOf(revenue_center_id));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONArray getTableList (Integer location_id, Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tables".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(use_cache)))
			queryParams.put("use_cache", String.valueOf(use_cache));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONArray getTickets (Integer location_id, Boolean condensed) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(condensed)))
			queryParams.put("condensed", String.valueOf(condensed));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject createTicket (Integer location_id, String ticket_type, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null || body == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(ticket_type)))
			queryParams.put("ticket_type", String.valueOf(ticket_type));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONArray getTabs (Integer location_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tabs".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject getTable (Integer location_id, Integer table_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tables/{table_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "table_id" + "\\}", apiInvoker.escapeString(table_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || table_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject getTicket (Integer location_id, Integer ticket_id, Integer user_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(user_id)))
			queryParams.put("user_id", String.valueOf(user_id));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject voidTicket (Integer location_id, Integer ticket_id, Integer user_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(user_id)))
			queryParams.put("user_id", String.valueOf(user_id));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "DELETE", queryParams, null, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject getTicketWithPOS_ID (Integer location_id, Integer pos_ticket_id, Integer user_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/pos/{pos_ticket_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "pos_ticket_id" + "\\}", apiInvoker.escapeString(pos_ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || pos_ticket_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		if(!"null".equals(String.valueOf(user_id)))
			queryParams.put("user_id", String.valueOf(user_id));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject submitOrder (Integer location_id, Integer ticket_id, Integer user_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}/users/{user_id}/order".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString())).replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || user_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, null, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject addItemsToOrder (Integer location_id, Integer ticket_id, Integer user_id, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}/users/{user_id}/order".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString())).replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || user_id == null || Globals.subtleApiKey == null || body == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONArray getUsersConnectedToTicket (Integer location_id, Integer ticket_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}/users".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, null, headerParams);
			if(response != null){
				return new JSONArray(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject connectUserToTicket (Integer location_id, Integer ticket_id, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}/users".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || Globals.subtleApiKey == null || body == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject discountTicket (Integer location_id, Integer ticket_id, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}/discount".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || Globals.subtleApiKey == null || body == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, body, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject addPaymentToTicket (Integer location_id, Integer ticket_id, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}/payments".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || Globals.subtleApiKey == null || body == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
	public static JSONObject addExternalPaymentToTicket (Integer location_id, Integer ticket_id, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/locations/{location_id}/tickets/{ticket_id}/payments/external".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString())).replaceAll("\\{" + "ticket_id" + "\\}", apiInvoker.escapeString(ticket_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || ticket_id == null || Globals.subtleApiKey == null || body == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
		try {
			String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, body, headerParams);
			if(response != null){
				return new JSONObject(response);
			}
			else {
				return null;
			}
		} catch (ApiException ex) {
			if(ex.getCode() == 404) {
				return null;
			}
			else {
				throw ex;
			}
		}
	}
}

