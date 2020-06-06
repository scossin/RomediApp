package fr.erias.romedi.sparql.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.impl.SimpleIRI;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediInstanceCIS;
import fr.erias.romedi.terminology.UnknownRomediURI;

/**
 * Given a Romedi URI, retrieve the links (CIS, BN, ATC...) to this URI
 * 
 * @author Cossin Sebastien
 *
 */
public class GetCIS extends HttpServlet {
	final static Logger logger = LoggerFactory.getLogger(GetCIS.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("asking a list of CIS by IRI");
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

		// send request
		logger.info("searching CIS..." + iri);
		try {
			RomediInstance romediInstance = ProcessIRI.request.getRomediInstance(romediIRI);
			HashSet<RomediInstanceCIS> romediInstancesCIS = ProcessIRI.request.getCisIRI(romediInstance);
			logger.info("sending JSON result");
			// sending results : 
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("CIS", JSONoutput.getJSONcis(romediInstancesCIS));
			jsonObject.put("request",romediInstance.getJSONObject());
			resp.setStatus(200);
			sendJSON(resp, jsonObject);
			logger.info("the end");
			return;
		} catch (UnknownRomediURI e) {
			String msg = "the URI was not found. It may exist but the current configuration will not display it";
			logger.info(msg);
			JSONObject jsonObject = UtilServlet.getErrorMessage(msg);
			resp.setStatus(404);
			sendJSON(resp, jsonObject);
		}
	}

	private void sendJSON(HttpServletResponse resp, JSONObject jsonObject) throws IOException {
		OutputStream os = resp.getOutputStream();
		os.write(jsonObject.toString().getBytes());
		os.close();
	}
	
	public static void main(String[] args) throws UnknownRomediURI {
		String iri = "INqv8icrj0cuu3370modvklp0rl0gk1al5";
		RomediIRI romediIRI = new RomediIRI(iri);
		RomediInstance romediInstance = ProcessIRI.request.getRomediInstance(romediIRI);
		HashSet<RomediInstanceCIS> romediInstancesCIS = ProcessIRI.request.getCisIRI(romediInstance);
		logger.info("sending JSON result");
		// sending results : 
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("CIS", JSONoutput.getJSONcis(romediInstancesCIS));
		jsonObject.put("request",romediInstance.getJSONObject());
		System.out.println(jsonObject.toString());
	}
}