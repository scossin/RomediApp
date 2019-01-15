package fr.erias.IAMsystemRomediAPI.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.lucene.queryparser.classic.ParseException;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.InvalidArraysLength;
import fr.erias.IAMsystem.exceptions.ProcessSentenceException;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystemRomedi.detect.DetectDrug;
import fr.erias.IAMsystemRomediAPI.servlet.ProcessInput;
import fr.erias.romedi.sparql.connection.ConfigEndpoint;
import fr.erias.romedi.sparql.terminology.RomediTerminologySPARQL;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;

public class Example {
	
	public static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	public static void drugDetectionExample(String sentence)  throws IOException, UnfoundTokenInSentence, ParseException, ScriptException, URISyntaxException {
		System.out.println("------------------ DrugDetection Example --------------------------");
		DetectDrug detectDrug = new DetectDrug();
		detectDrug.addLuceneTypoDetection();
		Set<CTcode> results = detectDrug.getCTcodes(sentence);
		for (CTcode codes : results) {
			System.out.println(codes.getCandidateTermString() + "\t" + codes.getCode());
		}
		System.out.println("------------------ End --------------------------");
	}
	
	public static void htmlOutputExample(String sentence) throws ProcessSentenceException, InvalidArraysLength, UnfoundTokenInSentence, IOException, ParseException {
		System.out.println("------------------ HTML output Example --------------------------");
		String html = ProcessInput.getHTML(sentence);
		System.out.println(html);
		System.out.println("------------------ End --------------------------");
	}
	// example
	public static void main2(String[] args) throws IOException, UnfoundTokenInSentence, ParseException, ProcessSentenceException, InvalidArraysLength, ScriptException, URISyntaxException {
		drugDetectionExample("spsfons");
		drugDetectionExample("ac acetylsalicilique paracetamol codeine, escitalopram et dompéridon");
		htmlOutputExample("Le patient prend de l'ac acetylsalicilique et de l'escitalopram sans domperidon"
				+ "\nmais pas que ça : il prend de la vitamine C aussi");
		htmlOutputExample("escitalopramm");
	}
	
	// example
	public static void main(String[] args) throws IOException, UnfoundTokenInSentence, ParseException, ProcessSentenceException, InvalidArraysLength, ScriptException, URISyntaxException {
		RomediType[] romediTypes = RomediType.values();
		// load the terminology from a SparqlEndpoint : 
		RomediTerminologySPARQL romediTerminologySPARQL = new RomediTerminologySPARQL(ConfigEndpoint.chosenEndpoint, romediTypes);
		RomediTerminology romediTerminology = romediTerminologySPARQL.getRomediTerminology();
		System.out.println(romediTerminology.getMapURI2instance().size());
		
		//SetTokenTree setTokenTree = RomediTermino2SetTokenTree.getSetTokenTree(romediTerminology, DetectDrug.getStopwordsRomedi()); 
		DetectDrug detectDrug = new DetectDrug(romediTerminology, DetectDrug.getStopwordsRomedi());
		detectDrug.createLuceneIndex(romediTerminology, DetectDrug.getStopwordsRomedi());
		detectDrug.addLuceneTypoDetection();
		String sentence = "ac acetylsalicilique paracetamol codeine, escitalopram et dompéridon";
		Set<CTcode> results = detectDrug.getCTcodes(sentence);
		for (CTcode codes : results) {
			System.out.println(codes.getCandidateTermString() + "\t" + codes.getCode());
		}
	}
}
