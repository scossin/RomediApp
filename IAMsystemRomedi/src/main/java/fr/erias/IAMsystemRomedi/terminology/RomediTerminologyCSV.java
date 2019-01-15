package fr.erias.IAMsystemRomedi.terminology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystemRomedi.detect.DetectDrug;
import fr.erias.romedi.terminology.GetRomediTerminology;
import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediType;

/**
 * A CSV implementation to load Romedi Terminology
 * 
 * @author Cossin Sebastien
 *
 */
public class RomediTerminologyCSV implements GetRomediTerminology {
	
	final static Logger logger = LoggerFactory.getLogger(RomediTerminologyCSV.class);
	
	private RomediTerminology romediTerminology = new RomediTerminology();
	
	/**
	 * Deprecated: use the inputstream method
	 * @param fileCSV a CSV Romedi Terminology
	 * @param sep CSV separator
	 * @param colLabel column with label
	 * @param colCode column with the uri (code)
	 * @param colType column with the type
	 * @throws IOException The file was not found
	 * @throws UnknownRomediType 
	 */
	@Deprecated
	public RomediTerminologyCSV(File fileCSV, String sep, int colLabel, int colCode, int colType) throws IOException, UnknownRomediType {
		this.romediTerminology = new RomediTerminology();
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(fileCSV));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String label = columns[colLabel];
			String code = columns[colCode];
			String typename = columns[colType];
			RomediType type = RomediType.getRomediType(typename);
			RomediIRI romediIRI = new RomediIRI(code);
			RomediInstance romediInstance = new RomediInstance(romediIRI, type, label);
			romediTerminology.getMapURI2instance().put(romediIRI,romediInstance);
		}
		br.close();
	}
	
	/**
	 * Load the Romedi Terminology with a CSV file as in inputstream
	 * @param in InputStream of the CSV Romedi Terminology
	 * @param sep CSV separator
	 * @param colLabel column with label
	 * @param colCode column with the uri (code)
	 * @param colType column with the type
	 * @throws IOException The file was not found
	 */
	public RomediTerminologyCSV(InputStream in, String sep, int colLabel, int colCode, int colType) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line = null;
		int lineCounter = 0;
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String label = columns[colLabel];
			String code = columns[colCode];
			String typename = columns[colType];
			RomediType romediType = null;
			try {
				romediType = RomediType.getRomediType(typename);
			} catch (UnknownRomediType e) {
				logger.error("Unknown RomediType:" + typename);
				logger.error(e.getLocalizedMessage());
				romediType = RomediType.UNKNOWN;
			}
			RomediIRI romediIRI = new RomediIRI(code);
			RomediInstance romediInstance = new RomediInstance(romediIRI, romediType, label);
			if (romediTerminology.getMapURI2instance().containsKey(romediIRI)) {
				romediTerminology.getMapURI2instance().get(romediIRI).addAltLabel(label);
			} else {
				romediTerminology.getMapURI2instance().put(romediIRI,romediInstance);
			}
			lineCounter += 1;
		}
		logger.info("number of lines loaded:" + lineCounter);
		logger.info("number of distinct uri: " + romediTerminology.getMapURI2instance().size());
		br.close();
	}
	
	/**
	 * 
	 * @param in The inputStream of a CSV file containing additional labels
	 * @param sep CSV separator
	 * @param colLabel column with label
	 * @param colCode column with the uri (code)
	 * @param colBoolhidden true if it's a hidden label (typo : donormil/donormyl), false if it's an alternative label (acide ascorbique / vitamine C)
	 * @throws IOException The file was not found
	 */
	public void addAdditionalLabels(InputStream in, String sep, int colLabel, int colCode, int colBoolhidden) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line = null;
		int lineCounter = 0;
		while ((line = br.readLine()) != null) {
			String[] columns = line.split(sep);
			String label = columns[colLabel];
			String code = columns[colCode];
			String boolHidden = columns[colBoolhidden];
			boolean hiddenLabel = boolHidden.equals("TRUE");
			RomediIRI romediIRI = new RomediIRI(code);
			if (!romediTerminology.getMapURI2instance().containsKey(romediIRI)) {
				logger.error("Error adding others labels:" + romediIRI.getIRIstring() + " not found");
				continue;
			}
			if (hiddenLabel) {
				romediTerminology.getMapURI2instance().get(romediIRI).getHiddenLabels().add(label);
			} else {
				romediTerminology.getMapURI2instance().get(romediIRI).addAltLabel(label);
			}
			lineCounter += 1;
		}
		logger.info("number of additional labels loaded:" + lineCounter);
		br.close();
	}
	
	@Override
	public RomediTerminology getRomediTerminology() {
		return(this.romediTerminology);
	}
	
	public static void main(String[] args) throws IOException, UnfoundTokenInSentence, ParseException {
		DetectDrug detectDrug = new DetectDrug();
		System.out.println(detectDrug.getCTcodes("prends de l'escitalopram").size());
	}
}
