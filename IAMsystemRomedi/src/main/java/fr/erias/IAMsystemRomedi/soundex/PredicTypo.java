package fr.erias.IAMsystemRomedi.soundex;

/**
 * Classes that predict typo between two words
 * @author Romain Griffier
 *
 */
public interface PredicTypo {

	/**
	 * A method to predict a typo between a word in the text and a word in the dictionary
	 * @param word1 : in the text, maybe a typo
	 * @param word2 : in the dictionary
	 * @return true if the algorithm predicts a typo
	 */
	public boolean isTypo(String word1, String word2);
	
}
