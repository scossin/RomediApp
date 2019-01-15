package fr.erias.romedi.sparql.servlet;

import org.json.JSONObject;

public class UtilServlet {

	/**
	 * 
	 * @param msg An error message to send
	 * @return A JSONObject to send 
	 */
	public static JSONObject getErrorMessage(String msg) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("errorMessage", msg);
		return jsonObject;
	}
}
