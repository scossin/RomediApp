package fr.erias.IAMsystemRomediAPI.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.exceptions.InvalidArraysLength;
import fr.erias.IAMsystem.exceptions.MyExceptions;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;


/**
 * Return JSON String to an HTTP POST request
 * 
 * @author Cossin Sebastien
 *
 */
public class GetJSONdrugsDetected extends HttpServlet implements SingleThreadModel {

	final static Logger logger = LoggerFactory.getLogger(GetJSONdrugsDetected.class);
	
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
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException ,IOException {
		logger.info("New string to extract drugs");
		
		resp.setContentType("application/json");
		resp.setHeader("Content-Disposition","attachment;filename="+"candidateTerms.json");
		setEncoding(req);
		
		logger.info("charset : ", charset);
		
		String sentence = getStringInput(req, charset);
		String jsonOutput = null;
		try {
			jsonOutput = ProcessInput.getJSON(ProcessInput.detectDrug,sentence);
			// here is the different with getHTMLdrugs ...
		} catch (ProcessSentenceException | InvalidArraysLength | UnfoundTokenInSentence | ParseException e) {
			MyExceptions.logException(logger, e);
			e.printStackTrace();
		}
		
		if (jsonOutput == null) {
			logger.info("An error occured");
			jsonOutput = "Sorry, an error occured";
		}
		OutputStream os = resp.getOutputStream();
		os.write(jsonOutput.getBytes());
		os.close();
		return;
	}
	
	private String getStringInput(HttpServletRequest req,Charset charset) throws ServletException {
		ServletInputStream in;
		String stringInput = null;
		try {
			in = req.getInputStream();
			stringInput = convert(in);
			in.close();
		} catch (IOException e) {
			MyExceptions.logMessage(logger, "Something went wrong while reading the body of request");
			MyExceptions.logException(logger, e);
			throw new ServletException();
		}
		logger.info("stringInput has " + stringInput.getBytes(charset).length + " bytes");
		return(stringInput);
	}
	
	private String convert(InputStream inputStream) throws IOException {
		try (Scanner scanner = new Scanner(inputStream, charset.name())) {
			return scanner.useDelimiter("\\A").next();
		}
	}
}
