package fr.erias.IAMsystemRomedi.detect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import fr.erias.IAMsystem.exceptions.InvalidCSV;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystemRomedi.config.ConfigRomedi;
import fr.erias.IAMsystemRomedi.terminology.RomediTermino2SetTokenTree;
import fr.erias.IAMsystemRomedi.terminology.RomediTerminologyCSV;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.UnknownRomediType;

/**
 * Normalized the terms of the Romedi terminology. This class was used to normalize the Romedi terminology. <br>
 * Deprecated: the {@link RomediTermino2SetTokenTree} is used to load and normalize the Romedi terminology
 * 
 * @author Cossin Sebastien 
 */
@Deprecated
public class NormalizeLabelRomedi {
	
	/**
	 * An instance to normalize the labels
	 */
	private TokenizerNormalizer tokenizerNormalizer ;
	
	/**
	 * Create a class to normalize RomediTerminology
	 * @param tokenizerNormalizer
	 */
	public NormalizeLabelRomedi(TokenizerNormalizer tokenizerNormalizer) {
		this.tokenizerNormalizer = tokenizerNormalizer;
	}
	
	/**
	 * 
	 * @param romediTerminology A {@link RomediTerminology} to normalize
	 * @return The {@link RomediTerminology} with normalizedLabel set
	 */
	public RomediTerminology normalizeTerminology(RomediTerminology romediTerminology) {
		for (RomediInstance romediInstance : romediTerminology.getMapURI2instance().values()) {
			String label = romediInstance.getPrefLabel();
			String normalizedLabel = tokenizerNormalizer.normalizeLabel(label);
			romediInstance.setNormalizedLabel(normalizedLabel);
		}
		return(romediTerminology);
	}
	
	public static void main(String[] args) throws IOException, InvalidCSV, ProcessSentenceException, UnknownRomediType {
		StopwordsRomedi stopwordsRomedi = DetectDrug.getStopwordsRomedi();
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwordsRomedi);
		NormalizeLabelRomedi normalizeLabelRomedi = new NormalizeLabelRomedi(tokenizerNormalizer);
		// load the terminology:
		InputStream in = ConfigRomedi.classLoader.getResourceAsStream(ConfigRomedi.romediTermsINBN);
		RomediTerminologyCSV romediTerminologyCSV = new RomediTerminologyCSV(in,"\t",2, 0,1);
		in.close();
		RomediTerminology romediTerminology = romediTerminologyCSV.getRomediTerminology();
		romediTerminology = normalizeLabelRomedi.normalizeTerminology(romediTerminology);
		File csvOutputFile = new File("src/main/resources/romediNormalized.csv");
		romediTerminology.toCSV(csvOutputFile, "\t",false);
	}
}
