package fr.erias.IAMsystemRomedi.soundex;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.script.ScriptException;

/**
 * Logistic regression with 3 variables and 3437 classified examples (see regFautes.R). Variables are : 
 * <ul> 
 * <li> First Letter identical
 * <li> Word length (in the text not in the dictionary)
 * <li> LevenshteinDistancePhonetic : computed by {@link CalcSoundex}
 * </ul>
 * @author Romain Griffier
 *
 */
public class PredictTypoImp1 implements PredicTypo {
	
	private CalcSoundex calcSoundex ;
	
	/**
	 * An instance that implements the results of a Logistic Regression to predict if 2 words (drugs) are similar
	 * @throws ScriptException Javascript exception
	 * @throws IOException File not found (js)
	 * @throws URISyntaxException Javascript exception
	 */
	public PredictTypoImp1() throws ScriptException, IOException, URISyntaxException {
		this.calcSoundex = new CalcSoundex();
	}
	
	/**
	 * Logistic Regression Results to predict a typo (see regFautes.R)
	 */
	private final double intercept = -5.3219973;
	private final double betaFirstLetter = 0.1767335;
	private final double betaWordLength = 0.9044521;
	private final double betaLevenshteinDistancePhonetic = -0.7975920;
	
	
	/**
	 * This threshold : specificity of 0.95% and sensitivity of 0.52
	 * We want very few false positives
	 */
	private double threshold = 0.9;
	
	/**
	 * Change the default threshold of 0.9
	 * @param newThreshold : a new threshold
	 */
	public void setThreshold(double newThreshold) {
		this.threshold = newThreshold;
	}
	
	
	@Override
	public boolean isTypo(String word1, String word2) {
		double prob = getProb(word1, word2);
		return(prob > threshold);
	}
	
	/**
	 * Get Probability
	 * @param word1 first word
	 * @param word2 second word
	 * @return the probability that the 2 words are similar
	 */
	public double getProb(String word1, String word2) {
		double xFirstLetter = getFirstLetterIdentical(word1, word2);
		double xWordLength = word1.length();
		double xLevenshteinPhonetic = getBinaryLevenshteinPhonetic(word1, word2);
		double somme = intercept + xFirstLetter*betaFirstLetter + xWordLength*betaWordLength 
				+ xLevenshteinPhonetic*betaLevenshteinDistancePhonetic ;
		double prob = Math.exp(somme) / (1+Math.exp(somme));
		return(prob);
	}
	
	/***
	 * The first letter is identical
	 * @param word1 first word
	 * @param word2 second word
	 * @return true if first letter is identical
	 */
	private int getFirstLetterIdentical(String word1, String word2) {
		if(word1.substring(0, 1).equalsIgnoreCase(word2.substring(0, 1))){
			return(1);
		} else {
			return(0);
		}
	}
	
	/**
	 * BinaryLevenshteinPhonetic
	 * @param word1 first word
	 * @param word2 second word
	 * @return 0 if error or if the distance is more than 1 ; else 1
	 */
	private int getBinaryLevenshteinPhonetic(String word1, String word2) {
		int result = calcSoundex.getPhonetic(word1, word2).getDistanceW12();
		// case error
		if (result == -1) {
			return(0);
		}
		
		if (result == 0) {
			return(result);
		} else {
			return(1);
		}
	}
	
	public static void main(String[] args) throws ScriptException, IOException, URISyntaxException {
		PredictTypoImp1 predictTypoImp1 = new PredictTypoImp1();
		System.out.println(predictTypoImp1.isTypo("escitalopramm", "escitalopram"));
		System.out.println(predictTypoImp1.isTypo("prochaine", "procaine"));
		System.out.println(predictTypoImp1.isTypo("acetylsalicilique", "acetylsalicylique"));
	}
}
