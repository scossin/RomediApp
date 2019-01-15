package fr.erias.IAMsystemRomediAPI.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystemRomedi.soundex.PredictTypoImp1;

/**
 * Get the probability that 2 words are typos
 * 
 * @author Cossin Sebastien
 *
 */
public class GetTypoProb extends HttpServlet implements SingleThreadModel {

	final static Logger logger = LoggerFactory.getLogger(GetTypoProb.class);
	
	protected Charset charset;

	private void setEncoding(HttpServletRequest req) {
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
		
		String word1 = req.getParameter("word1");
		String word2 = req.getParameter("word2");
		
		logger.debug("word 1 : " + word1);
		logger.debug("word 2 : " + word2);
		
		double prob = 0;
		try {
			prob = ProcessInput.getProb(word1, word2);
		} catch (ScriptException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		resp.setContentType("application/json");
		resp.setHeader("Content-Disposition","attachment;filename="+"prob.json");
		setEncoding(req);
		
		JSONObject jsonOutput = new JSONObject();
		jsonOutput.put("word1", word1);
		jsonOutput.put("word2", word2);
		jsonOutput.put("prob", prob);
		
		OutputStream os = resp.getOutputStream();
		os.write(jsonOutput.toString().getBytes());
		os.close();
		return;
	}
	
	public static void main(String[] args) throws ScriptException, IOException, URISyntaxException {
		String word1 = "escitalopram";
		String word2 = "escitalopramm";
		PredictTypoImp1 predictTypoImp1 = new PredictTypoImp1();
		double prob = predictTypoImp1.getProb(word1, word2);
		System.out.println(prob);
	}
}
