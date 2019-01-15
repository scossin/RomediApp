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

import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.UnknownRomediURI;

/**
 * Retrieve the label of a Romedi URI
 * 
 * @author Cossin Sebastien
 *
 */
public class GetJSONlabelsByIRI extends HttpServlet {
	final static Logger logger = LoggerFactory.getLogger(GetJSONlabelsByIRI.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("asking a JSON representation by a URI");
		resp.setContentType("application/json");
		resp.setHeader("Content-Disposition","attachment;filename=ViewRomedi.json");
		String iri = req.getParameter("IRI");
		if (iri == null) {
			String msg = "this uri is null";
			logger.info(msg);
			JSONObject jsonObject = UtilServlet.getErrorMessage(msg);
			resp.setStatus(404);
			sendJSON(resp, jsonObject);
			return;
		}
		
		RomediIRI romediIRI = new RomediIRI(iri);
		// trying to remove the namespace : 
		boolean isKnownURI = ProcessIRI.request.getRomediTerminology().existsURI(romediIRI);
		if (!isKnownURI) {
			String msg = "this uri : " + iri + " \n doesn't exist";
			logger.info(msg);
			JSONObject jsonObject = getErrorMessage(msg);
			resp.setStatus(404);
			sendJSON(resp, jsonObject);
		} else {
			resp.setStatus(200);
			RomediInstance romediInstance;
			try {
				romediInstance = ProcessIRI.request.getRomediTerminology().getRomediInstance(romediIRI);
				JSONObject jsonObject = romediInstance.getJSONObject();
				sendJSON(resp, jsonObject);
			} catch (UnknownRomediURI e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return;
	}
	
	private void sendJSON(HttpServletResponse resp, JSONObject jsonObject) throws IOException {
		OutputStream os = resp.getOutputStream();
		os.write(jsonObject.toString().getBytes());
		os.close();
	}

	private JSONObject getErrorMessage(String msg) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ErrorMessage", msg);
		return jsonObject;
	}
	
	public static void main(String[] args) throws UnknownRomediURI {
		String uri = "INdku2cefkijqo9ntgknbkh0t88n79jmel";
		RomediIRI romediIRI = new RomediIRI(uri);
		RomediInstance romediInstance = ProcessIRI.request.getRomediTerminology().getRomediInstance(romediIRI);
		JSONObject jsonObject = romediInstance.getJSONObject();
		System.out.println(jsonObject.toString());
	}
}
