package fr.erias.IAMsystemRomediAPI.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryparser.classic.ParseException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.io.EncodingPrintWriter.out;
import fr.erias.IAMsystem.exceptions.InvalidArraysLength;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystemRomedi.detect.DetectDrug;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediType;


/**
 * Return JSON String to an HTTP POST request for a specific RomediType
 * 
 * @author Cossin Sebastien
 *
 */
public class GetJSONdrugDetectedByType extends HttpServlet implements SingleThreadModel {

	final static Logger logger = LoggerFactory.getLogger(GetJSONdrugDetectedByType.class);

	protected Charset charset;

	private void setEncoding(HttpServletRequest req) {
		// 
		String reqCharset = req.getCharacterEncoding();
		if (reqCharset == null) {
			charset = Charset.forName("UTF-8");
		} else if (Charset.isSupported(reqCharset)) {
			charset = Charset.forName(reqCharset);
		} else {
			charset = Charset.forName("UTF-8");
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException ,IOException {
		logger.info("New string to extract drugs");
		resp.setContentType("application/json");
		resp.setHeader("Content-Disposition","attachment;filename="+"prob.json");
		setEncoding(req);


		String drugname = req.getParameter("drugname");
		String type = req.getParameter("romeditype");

		if (drugname == null || type == null) {
			resp.setStatus(404);
			JSONObject jsonOutput = new JSONObject();
			jsonOutput.put("error", "drugname or type is null");
			sendResponse(resp, jsonOutput);
			return;
		}

		logger.info("drugname : " + drugname);
		logger.info("romeditype : " + type);

		RomediType romediType;

		try {
			romediType = RomediType.getRomediType(type);
			DetectDrug detectDrug = ProcessInput.detectDrugByType.getDetectDrug(romediType);
			String outputString = ProcessInput.getJSON(detectDrug, drugname);
			resp.setStatus(200);
			sendResponse(resp, outputString);
			return;
		} catch (UnknownRomediType | ProcessSentenceException | InvalidArraysLength | UnfoundTokenInSentence | ParseException e) {
			resp.setStatus(404);
			JSONObject jsonOutput = new JSONObject();
			jsonOutput.put("error", e.getMessage());
			sendResponse(resp, jsonOutput);
			return;
		}
	}

	private void sendResponse(HttpServletResponse resp, JSONObject jsonObject) throws IOException {
		OutputStream os = resp.getOutputStream();
		os.write(jsonObject.toString().getBytes());
		os.close();
		return;
	}

	private void sendResponse(HttpServletResponse resp, String outputString) throws IOException {
		OutputStream os = resp.getOutputStream();
		os.write(outputString.getBytes());
		os.close();
		return;
	}

	public static void main(String[] args) throws ProcessSentenceException, InvalidArraysLength, UnfoundTokenInSentence, IOException, ParseException {
		DetectDrug detectDrug = ProcessInput.detectDrugByType.getDetectDrug(RomediType.DrugClass);
		String outputString = ProcessInput.getJSON(detectDrug, "statine");
		System.out.println(outputString);
	}
}
