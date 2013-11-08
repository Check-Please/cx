package com.subtledata.api;

import com.subtledata.client.ApiException;
import com.subtledata.client.ApiInvoker;
import java.util.*;

import kinds.Globals;

import org.json.JSONException;
import org.json.JSONObject;

public class ConcessionsApi {
	private static final String basePath = "https://api.subtledata.com/v1";
	private static final ApiInvoker apiInvoker = ApiInvoker.getInstance();

	public static JSONObject createConcessionOrder (Integer location_id, JSONObject body) throws ApiException, JSONException {
		// create path and map variables
		String path = "/concessions/{location_id}/order".replaceAll("\\{format\\}","json").replaceAll("\\{" + "location_id" + "\\}", apiInvoker.escapeString(location_id.toString()));

		// query params
		Map<String, String> queryParams = new HashMap<String, String>();
		Map<String, String> headerParams = new HashMap<String, String>();

		// verify required params are set
		if(location_id == null || Globals.subtleApiKey == null || body == null ) {
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
}

