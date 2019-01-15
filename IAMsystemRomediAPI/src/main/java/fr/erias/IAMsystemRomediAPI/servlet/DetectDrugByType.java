package fr.erias.IAMsystemRomediAPI.servlet;

import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystemRomedi.detect.DetectDrug;
import fr.erias.romedi.sparql.connection.ConfigEndpoint;
import fr.erias.romedi.sparql.connection.Request;
import fr.erias.romedi.sparql.terminology.RomediTerminologySPARQL;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;

/**
 * Detect Drug with a chosen RomediType
 * 
 * @author Cossin Sebastien
 *
 */
public class DetectDrugByType {
	final static Logger logger = LoggerFactory.getLogger(Request.class);
	
	private HashMap<RomediType, DetectDrug> mapTypeDetect = new HashMap<RomediType, DetectDrug>();

	public DetectDrug getDetectDrug(RomediType romediType) {
		return(this.mapTypeDetect.get(romediType));
	}
	
	public DetectDrugByType(RomediType[] romediTypes) throws IOException {
		logger.info("new DetectDrugByType");
		for (RomediType romediType : romediTypes) {
			logger.info("loading detectDrugByTypes");
			RomediType[] oneRomediType = {romediType};
			RomediTerminologySPARQL romediTerminologySPARQL = new RomediTerminologySPARQL(ConfigEndpoint.chosenEndpoint, oneRomediType);
			RomediTerminology romediTerminology = romediTerminologySPARQL.getRomediTerminology();
			DetectDrug detectDrug = new DetectDrug(romediTerminology, DetectDrug.getStopwordsRomedi());
			mapTypeDetect.put(romediType, detectDrug);
		}
	}
	
	public static void main(String[] args) throws IOException, UnfoundTokenInSentence, ParseException {
		RomediType[] romediTypes = {RomediType.BN, RomediType.IN};
		DetectDrugByType detectDrugByType =  new DetectDrugByType(romediTypes);
		for (CTcode code : detectDrugByType.getDetectDrug(RomediType.BN).getCTcodes("escitalopram")) {
			System.out.println(code.getJSONobject().toString());
		}
	}
}
