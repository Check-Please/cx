package com.subtledata.api;

import com.subtledata.client.ApiException;
import com.subtledata.client.ApiInvoker;
import java.util.*;

import kinds.Globals;

import org.json.JSONArray;
import org.json.JSONException;

public class GeneralApi {
	private static final String basePath = "https://api.subtledata.com/v1";
	private static final ApiInvoker apiInvoker = ApiInvoker.getInstance();

	public static JSONArray getAllCountries (Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/general/countries".replaceAll("\\{format\\}","json");

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
	public static JSONArray getAllStates (Boolean use_cache) throws ApiException, JSONException {
		// create path and map variables
		String path = "/general/states".replaceAll("\\{format\\}","json");

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
}

