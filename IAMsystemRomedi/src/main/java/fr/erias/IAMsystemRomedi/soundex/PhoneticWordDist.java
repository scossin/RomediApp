package fr.erias.IAMsystemRomedi.soundex;


/**
 * A class to store two words and their phonetic and a distance the two words
 * @author Cossin Sebastien
 *
 */
public class PhoneticWordDist {

	private String word1;
	private String phoneticWord1;
	private String word2;
	private String phoneticWord2;

	/**
	 * the phonetic distance between word1 and word 2
	 */
	private int distanceW12;

	/**
	 * 	Create an instance of PhoneticWordDist
	 * @param word1 word1 the first word
	 * @param word2 word2 the second word
	 * @param phoneticWord1 the phonetic of word1
	 * @param phoneticWord2 the phonetic of word2
	 * @param distanceW12 the Levenshtein distance between 2 phonetics or -1 in case of error
	 */
	public PhoneticWordDist(String word1, String word2, String phoneticWord1, String phoneticWord2, int distanceW12) {
		this.word1 = word1;
		this.word2 = word2;
		this.phoneticWord1 = phoneticWord1;
		this.phoneticWord2 = phoneticWord2;
		this.distanceW12 = distanceW12;
	}

	public void setDistanceW12(int distanceW12) {
		this.distanceW12 = distanceW12;
	}

	public String getWord1() {
		return word1;
	}

	public String getWord2() {
		return word2;
	}

	public int getDistanceW12() {
		return distanceW12;
	}

	public String getPhoneticWord1() {
		return phoneticWord1;
	}

	public void setPhoneticWord1(String phoneticWord1) {
		this.phoneticWord1 = phoneticWord1;
	}

	public String getPhoneticWord2() {
		return phoneticWord2;
	}

	public void setPhoneticWord2(String phoneticWord2) {
		this.phoneticWord2 = phoneticWord2;
	}
}
