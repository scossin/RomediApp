package fr.erias.IAMsystemRomediAPI.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.script.ScriptException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.InvalidArraysLength;
import fr.erias.IAMsystem.exceptions.MyExceptions;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystemRomedi.config.ConfigRomedi;
import fr.erias.IAMsystemRomedi.detect.DetectDrug;
import fr.erias.IAMsystemRomedi.soundex.PredictTypoImp1;
import fr.erias.romedi.sparql.connection.ConfigEndpoint;
import fr.erias.romedi.sparql.connection.Request;
import fr.erias.romedi.sparql.terminology.RomediTerminologySPARQL;
import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediURI;

/**
 * The class that processes clients requests
 * 
 * @author Cossin Sebastien
 *
 */
public class ProcessInput {

	final static Logger logger = LoggerFactory.getLogger(ProcessInput.class);

	/**
	 * Contains a RomediTerminology by RomediType
	 */
	public static DetectDrugByType detectDrugByType;

	/**
	 * Contains all the terminology
	 */
	public static DetectDrug detectDrug;

	/**
	 * all the terminology with all the instances
	 */
	public static RomediTerminology romediTerminology;

	/**
	 * for the levenshtein distance:
	 */
	public static PredictTypoImp1 predictTypoImp1;

	/**
	 * Remove some URIs
	 * 
	 * @throws IOException 
	 */
	private static HashSet<RomediIRI> getRomediIRI2Remove() throws IOException {
		HashSet<RomediIRI> iri2remove = new HashSet<RomediIRI>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(ConfigRomedi.uriExcluded);
		BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
		String line ;
		while ((line = br.readLine()) !=null) {
			RomediIRI romediIRI = new RomediIRI(line);
			iri2remove.add(romediIRI);
		}
		br.close();
		in.close();
		return(iri2remove);
	}

	static {
		try {
			logger.info("trying to create a new detectDrug instance");
			// load the terminology from a SparqlEndpoint : 
			
			RomediType[] romediTypes = {RomediType.BN, RomediType.IN, RomediType.PIN, RomediType.BNdosage};
			detectDrugByType = new DetectDrugByType(romediTypes);
			
			RomediTerminologySPARQL romediTerminologySPARQL = new RomediTerminologySPARQL(ConfigEndpoint.chosenEndpoint, Request.outputType);

			logger.info("trying to load the RomediTerminology");
			romediTerminology = romediTerminologySPARQL.getRomediTerminology();
			// remove some URIs:
			romediTerminology.removeURI(getRomediIRI2Remove());
			//System.out.println(romediTerminology.getMapURI2instance().size());
			//SetTokenTree setTokenTree = RomediTermino2SetTokenTree.getSetTokenTree(romediTerminology, DetectDrug.getStopwordsRomedi()); 
			Stopwords stopwordsRomedi = DetectDrug.getStopwordsRomedi();
			detectDrug = new DetectDrug(romediTerminology, stopwordsRomedi);
			detectDrug.createLuceneIndex(romediTerminology, stopwordsRomedi);
			detectDrug.addLuceneTypoDetection();
		} catch (ScriptException | URISyntaxException | IOException e) {
			MyExceptions.logException(logger, e);
			e.printStackTrace();
		}

		logger.info("trying to create an instance of predictTypoImp1");
		try {
			predictTypoImp1 = new PredictTypoImp1();
		} catch (ScriptException | URISyntaxException | IOException e) {
			MyExceptions.logException(logger, e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param word1 first word
	 * @param word2 second word
	 * @return The probability that word2 is a typo of word1
	 * @throws ScriptException Javascript script error
	 * @throws IOException File error
	 * @throws URISyntaxException
	 */
	public static synchronized double getProb(String word1, String word2) throws ScriptException, IOException, URISyntaxException {
		predictTypoImp1 = new PredictTypoImp1();
		double prob = predictTypoImp1.getProb(word1, word2);
		return(prob);
	}

	/**
	 * Extract drugs and return HTML as a String
	 * @param sentence The textual content in input
	 * @return HTML string
	 * @throws ProcessSentenceException
	 * @throws InvalidArraysLength
	 * @throws UnfoundTokenInSentence
	 * @throws IOException
	 * @throws ParseException
	 */
	public static synchronized String getHTML(String sentence) throws ProcessSentenceException, InvalidArraysLength, UnfoundTokenInSentence, IOException, ParseException {
		logger.info("getting HTML");
		// Extract drugs and remove overlap
		// Ordered each CT drugs
		Set<CTcode> unorderedCTcodes = detectDrug.getCTcodes(sentence);
		TreeSet<CTcode> orderedCTdrugs = new TreeSet<CTcode>() ;
		for (CTcode ctCode : unorderedCTcodes) {
			orderedCTdrugs.add(ctCode);
		}

		// For each candidate Term drug
		Iterator<CTcode> iter = orderedCTdrugs.iterator();
		CTcode currentCTcode = null;

		// output sentence :
		StringBuilder sb = new StringBuilder();
		int sentencePosition = 0;
		while (iter.hasNext()) {
			currentCTcode = iter.next();
			int startPositionNextCTdrug = currentCTcode.getStartPosition();
			while (sentencePosition != startPositionNextCTdrug) {
				sb.append(sentence.charAt(sentencePosition));
				sentencePosition = sentencePosition + 1;
			}

			String uri = currentCTcode.getCode();
			RomediInstance romediInstance;
			try {
				romediInstance = romediTerminology.getRomediInstance(new RomediIRI(uri));
				String label = romediInstance.getPrefLabel();
				RomediType type = romediInstance.getType();
				String candidateTermString = currentCTcode.getCandidateTermString();
				// append the span
				sb.append(GetSpan.getSpan(uri,label,type.toString(),candidateTermString));
				sentencePosition = currentCTcode.getEndPosition()+1; // + 1 : next char after the CT
			} catch (UnknownRomediURI e) { // unfound URI
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Remaining sentence after the end of the last CT :
		String sentenceEnd = sentence.substring(sentencePosition, sentence.length());
		sb.append(sentenceEnd);

		String htmlOutput = sb.toString();
		// remove newline : 
		htmlOutput = htmlOutput.replaceAll("\\r\\n","<br>").replaceAll("\\n", "<br>");
		logger.info("output : ");
		logger.info("\t" + htmlOutput);
		return(htmlOutput);
	}

	/**
	 * Extract drugs and return JSON 
	 * @param detectDrug A detect Drug instance
	 * @param sentence the textual content
	 * @return JSON string
	 * @throws ProcessSentenceException
	 * @throws InvalidArraysLength
	 * @throws UnfoundTokenInSentence
	 * @throws IOException
	 * @throws ParseException
	 */
	public static synchronized String getJSON(DetectDrug detectDrug, String sentence) throws ProcessSentenceException, InvalidArraysLength, UnfoundTokenInSentence, IOException, ParseException {
		logger.info("extracting drug and returning jsonOutput ...");
		// Extract drugs and remove overlap

		JSONObject jsonObject = new JSONObject();
		// Ordered each CT drugs
		Set<CTcode> unorderedCTcodes = detectDrug.getCTcodes(sentence);

		// For each candidate Term drug
		Iterator<CTcode> iter = unorderedCTcodes.iterator();
		CTcode currentCTcode = null;

		// output sentence :
		int i = 0;
		while (iter.hasNext()) {
			currentCTcode = iter.next();
			String uri = currentCTcode.getCode();
			RomediInstance romediInstance;
			try {
				romediInstance = romediTerminology.getRomediInstance(new RomediIRI(uri));
				String label = romediInstance.getPrefLabel();
				RomediType type = romediInstance.getType();
				JSONObject jsonCT = currentCTcode.getJSONobject();
				jsonCT.put("terminoLabel", label);
				jsonCT.put("type", type.toString());
				jsonObject.put(Integer.toString(i), jsonCT);
				i = i + 1 ;
			} catch (UnknownRomediURI e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Remaining sentence after the end of the last CT :
		String json = jsonObject.toString();
		return(json);
	}

	public static void main(String[] args) throws IOException, ScriptException, URISyntaxException, UnfoundTokenInSentence, ParseException, ProcessSentenceException, InvalidArraysLength, UnknownRomediURI {
		Set<CTcode> results = detectDrug.getCTcodes("gaviscon");
		System.out.println(results.size());
		for (CTcode codes : results) {
			System.out.println(codes.getJSONobject());
		}
	}
}
