package fr.erias.IAMsystemRomedi.soundex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A class to compute a Levenshtein Phonetic between 2 words using phonetic.js and the method phonetic
 * 
 * @author Romain Griffier
 *
 */
public class CalcSoundex implements LevenshteinPhonetic {

	private final String jsPhoneticFileName = "library/phonetic.js";

	private final String funct = "phonetic";

	/**
	 * The instance that invokes the javascript function
	 */
	private Invocable invocable = null;

	/**
	 * 
	 * @throws ScriptException Javascript exception
	 * @throws IOException File not found
	 * @throws URISyntaxException Javascript exception
	 */
	public CalcSoundex () throws ScriptException, IOException, URISyntaxException{
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(jsPhoneticFileName);
		byte[] bytes = readAllBytes(in);
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		String script = new String(bytes);
		engine.eval(script);
		this.invocable = (Invocable) engine;
		in.close();
	}

	/**
	 * 
	 * @param inputStream the stream of the file
	 * @return bytes
	 * @throws IOException file not found
	 */
	private static byte[] readAllBytes(InputStream inputStream) throws IOException {
		final int bufLen = 4 * 0x400; // 4KB
		byte[] buf = new byte[bufLen];
		int readLen;
		IOException exception = null;

		try {
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
					outputStream.write(buf, 0, readLen);
				return outputStream.toByteArray();
			}
		} catch (IOException e) {
			exception = e;
			throw e;
		} finally {
			if (exception == null) inputStream.close();
			else try {
				inputStream.close();
			} catch (IOException e) {
				exception.addSuppressed(e);
			}
		}
	}

	@Override
	public PhoneticWordDist getPhonetic(String word1, String word2) {
		// return the distance
		PhoneticWordDist phoneticWordDist = new PhoneticWordDist(word1, word2, null,null,-1) ;
		try {
			String phoneticWord1 = executeJavaStript(word1).toString();
			String phoneticWord2 = executeJavaStript(word2).toString();

			phoneticWordDist.setPhoneticWord1(phoneticWord1);
			phoneticWordDist.setPhoneticWord2(phoneticWord2);

			int distanceW12 = Levenshtein.levenshteinDistance(phoneticWord1, phoneticWord2);
			phoneticWordDist.setDistanceW12(distanceW12);
		} catch (NoSuchMethodException | ScriptException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return phoneticWordDist;
	}

	private Object executeJavaStript(String param) throws ScriptException, IOException, NoSuchMethodException{
		Object result = invocable.invokeFunction(this.funct, param);
		return(result);
	}

	/**
	 * Write a file for the logistic regression
	 * @param inputFile goldstandard
	 * @param outputFile goldstandard + features
	 * @param csvSplitBy the separator 
	 * @throws IOException files not found
	 * @throws ScriptException Javascript error
	 * @throws URISyntaxException 
	 */
	public static void writeGoldStandard(File inputFile, File outputFile, String csvSplitBy) throws IOException, ScriptException, URISyntaxException{
		LevenshteinPhonetic calcSoundex = new CalcSoundex();
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		String[] columns = null;
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		BufferedWriter wr = new BufferedWriter(new FileWriter(outputFile,true));
		String line = null;

		line = br.readLine(); // header
		columns = line.split(csvSplitBy);

		wr.write(columns[0]);	
		wr.write(csvSplitBy);

		wr.write(columns[1]);
		wr.write(csvSplitBy);

		wr.write("phoneticwriteWord");
		wr.write(csvSplitBy);

		wr.write("phoneticCanditatWord");
		wr.write(csvSplitBy);

		wr.write("levenshteinDistancePhonetic");
		wr.write(csvSplitBy);

		wr.write("firstLetterIdentical");
		wr.write(csvSplitBy);

		wr.write("wordLength");
		wr.write(csvSplitBy);

		wr.write(columns[2]);
		wr.write("\n");

		while ((line = br.readLine()) != null) {
			columns = line.split(csvSplitBy);
			String word1 = columns[0];
			String word2 = columns[1];
			PhoneticWordDist phoneticWordDist = calcSoundex.getPhonetic(word1, word2);		

			int firstLetterIdentical;
			if(word1.substring(0, 1).equalsIgnoreCase(word2.substring(0, 1))){
				firstLetterIdentical = 1;
			} else {
				firstLetterIdentical = 0;
			}
			wr.write(word1);
			wr.write(csvSplitBy);

			wr.write(word2);
			wr.write(csvSplitBy);

			wr.write(phoneticWordDist.getPhoneticWord1());
			wr.write(csvSplitBy);

			wr.write(phoneticWordDist.getPhoneticWord2());
			wr.write(csvSplitBy);

			wr.write(String.valueOf(phoneticWordDist.getDistanceW12()));
			wr.write(csvSplitBy);

			wr.write(Integer.toString(firstLetterIdentical));
			wr.write(csvSplitBy);

			wr.write(Integer.toString(word1.length()));
			wr.write(csvSplitBy);

			wr.write(columns[2]);
			wr.write("\n");
		}
		wr.close();
		br.close();
	}

	public static void main(String[] args) throws IOException, NoSuchMethodException, ScriptException, URISyntaxException{
		File inputFile = new File("./data/dfFautePrediction.csv");
		File outputFile = new File("./data/dfFautePredictionSoundex.csv");
		CalcSoundex.writeGoldStandard(inputFile, outputFile, "\t");
	}
}