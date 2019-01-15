package fr.erias.IAMsystemRomedi.detect;

import java.util.HashMap;
import java.util.HashSet;

import fr.erias.IAMsystem.detect.Synonym;

/**
 * Drugs abbreviations
 * 
 * @author Cossin Sebastien
 *
 */
public class AbbreviationsDrug implements Synonym {
	
	private HashMap<String, HashSet<String[]>> abbreviations;
	
	public AbbreviationsDrug() {
		abbreviations = new HashMap<String, HashSet<String[]>>();
		
		// adding abbreviations :
		HashSet<String[]> temp = new HashSet<String[]>();
		temp.add(new String[] {"acide"});
		abbreviations.put("ac", temp);
		
		temp = new HashSet<String[]>();
		temp.add(new String[] {"kardegic"});
		abbreviations.put("kdg", temp);
		
		temp = new HashSet<String[]>();
		temp.add(new String[] {"methotrexate"});
		abbreviations.put("mtx", temp);
		
		temp = new HashSet<String[]>();
		temp.add(new String[] {"doliprane"});
		abbreviations.put("doli", temp);
		
		// vit à New York => vitamine A
//		temp = new HashSet<String[]>();
//		temp.add(new String[] {"vitamine"});
//		abbreviations.put("vit", temp);
		
		temp = new HashSet<String[]>();
		temp.add(new String[] {"amoxicilline"});
		abbreviations.put("amox", temp);
		
		temp = new HashSet<String[]>();
		temp.add(new String[] {"chlorure"});
		abbreviations.put("chlr", temp);
		
	}
	
	public HashSet<String[]> getSynonyms(String token){
		HashSet<String[]> arraySynonyms = new HashSet<String[]>();
		if (abbreviations.containsKey(token)) {
			arraySynonyms = abbreviations.get(token);
		}
		return(arraySynonyms);
	}
}
