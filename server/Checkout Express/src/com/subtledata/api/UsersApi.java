package com.subtledata.api;

import com.subtledata.client.ApiException;
import com.subtledata.client.ApiInvoker;
import java.util.*;

import kinds.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UsersApi {
	private static final String basePath = "https://api.subtledata.com/v1";
	private static final ApiInvoker apiInvoker = ApiInvoker.getInstance();

	public static JSONObject createUser (JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users".replaceAll("\\{format\\}","json");

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(Globals.subtleApiKey == null || body == null ) {
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
	public static JSONObject getUser (Integer user_id, Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users/{user_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(user_id == null || Globals.subtleApiKey == null ) {
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
	public static JSONObject deleteUser (Integer user_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users/{user_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(user_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
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
	public static JSONObject authUser (JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users/authenticate".replaceAll("\\{format\\}","json");

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(Globals.subtleApiKey == null || body == null ) {
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
	public static JSONObject searchUsersByName (String user_name, Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users/search/name/{user_name}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "user_name" + "\\}", apiInvoker.escapeString(user_name.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(user_name == null || Globals.subtleApiKey == null ) {
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
	public static JSONArray getUsersCards (Integer user_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users/{user_id}/cards".replaceAll("\\{format\\}","json").replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(user_id == null || Globals.subtleApiKey == null ) {
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
	public static JSONObject createCardForUser (Integer user_id, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users/{user_id}/cards".replaceAll("\\{format\\}","json").replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(user_id == null || Globals.subtleApiKey == null || body == null ) {
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
	public static JSONObject deleteUserCreditCard (Integer user_id, Integer card_id) throws ApiException, JSONException {
		// create path and map variables
		String path = "/users/{user_id}/cards/{card_id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "user_id" + "\\}", apiInvoker.escapeString(user_id.toString())).replaceAll("\\{" + "card_id" + "\\}", apiInvoker.escapeString(card_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(user_id == null || card_id == null || Globals.subtleApiKey == null ) {
			throw new ApiException(400, "missing required params");
		}
		if(!"null".equals(String.valueOf(Globals.subtleApiKey)))
			queryParams.put("api_key", String.valueOf(Globals.subtleApiKey));
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
}

