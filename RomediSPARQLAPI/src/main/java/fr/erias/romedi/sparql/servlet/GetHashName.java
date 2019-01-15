package fr.erias.romedi.sparql.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.romedi.terminology.HashName;

/**
 * Hash the name of a drug
 * 
 * @author Cossin Sebastien
 *
 */
public class GetHashName extends HttpServlet {
	final static Logger logger = LoggerFactory.getLogger(GetHashName.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("GetHashName called");
		resp.setContentType("application/json");
		String name = req.getParameter("name");
		if (name == null) {
			String msg = "name parameter is null";
			logger.info(msg);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("error:",msg );
			resp.setStatus(404);
			sendJSON(resp, jsonObject);
			return;
		}
		
		JSONObject jsonObject = HashName.getHashName(name);
		resp.setStatus(200);
		sendJSON(resp, jsonObject);
		return;

	}

	private void sendJSON(HttpServletResponse resp, JSONObject jsonObject) throws IOException {
		OutputStream os = resp.getOutputStream();
		os.write(jsonObject.toString().getBytes());
		os.close();
	}
}