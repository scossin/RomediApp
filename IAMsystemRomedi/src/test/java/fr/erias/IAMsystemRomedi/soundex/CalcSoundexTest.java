package fr.erias.IAMsystemRomedi.soundex;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.script.ScriptException;

import org.junit.Test;

public class CalcSoundexTest {
	
	@Test
	public void getPhoneticTest() throws ScriptException, IOException, URISyntaxException{
		//File inputFile = new File("./data/dfFautePrediction.csv");
		//File outputFile = new File("./data/dfFautePredictionSoundex.csv");
		LevenshteinPhonetic calcSoundex = new CalcSoundex();
		PhoneticWordDist phoneticWordDist = calcSoundex.getPhonetic("metphomie", "metformine");
		assertEquals(phoneticWordDist.getPhoneticWord1(), "METFOMI");
		assertEquals(phoneticWordDist.getPhoneticWord2(), "METFORMIN");
		assertEquals(phoneticWordDist.getDistanceW12(), 2);
	}
}
