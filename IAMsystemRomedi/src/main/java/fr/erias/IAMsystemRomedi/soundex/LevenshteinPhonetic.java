package fr.erias.IAMsystemRomedi.soundex;

/**
 * An interface to implement classes that compute Levenshtein distance between 2 phonetics
 */
public interface LevenshteinPhonetic {

	/**
	 * Produce a phonetic distance between 2 words
	 * @param word1 first word
	 * @param word2 second word
	 * @return a {@link PhoneticWordDist} instance
	 */
	public PhoneticWordDist getPhonetic(String word1, String word2);

}
