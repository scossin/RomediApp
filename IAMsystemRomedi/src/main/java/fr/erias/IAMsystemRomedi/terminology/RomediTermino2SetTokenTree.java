package fr.erias.IAMsystemRomedi.terminology;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystem.tree.TokenTree;
import fr.erias.romedi.terminology.GetRomediTerminology;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediTerminology;

/**
 * Convert a {@link RomediTerminology} to a {@link SetTokenTree} for drug detection
 * 
 * @author Cossin Sebastien
 *
 */

public class RomediTermino2SetTokenTree {
	final static Logger logger = LoggerFactory.getLogger(RomediTermino2SetTokenTree.class);
	
	/**
	 * Transform a {@link RomediTerminology} to a tree datastructure ({@link SetTokenTree})
	 * @param romediTerminology A {@link RomediTerminology}. See {@link GetRomediTerminology} to create an instance
	 * @param stopwordsRomedi A {@link Stopwords} instance to remove stopwords from the terminology
	 * @return A {@link SetTokenTree}
	 */
	public static SetTokenTree getSetTokenTree(RomediTerminology romediTerminology, Stopwords stopwordsRomedi) {
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwordsRomedi);
		SetTokenTree setTokenTree = new SetTokenTree();
		for (RomediInstance romediInstance : romediTerminology.getMapURI2instance().values()) {
			String label = romediInstance.getPrefLabel();
			String libNormal = tokenizerNormalizer.normalizeLabel(label);
			String code = romediInstance.getIRI();
			HashSet<TokenTree> altTokenTrees = getAltTokenTree(tokenizerNormalizer, romediInstance, stopwordsRomedi);
			HashSet<TokenTree> hiddenTokenTrees = getHiddenTokenTree(tokenizerNormalizer, romediInstance, stopwordsRomedi);
			setTokenTree.addTokenTrees(altTokenTrees);
			setTokenTree.addTokenTrees(hiddenTokenTrees);
			setTokenTree.addTokenTree(getTokenTree(libNormal, code, stopwordsRomedi));
		}
		return(setTokenTree);
	}
	
	
	/**
	 * Create a {@link TokenTree} for each alternative label and add it to a HashSet
	 * @param tokenizerNormalizer {@link TokenizerNormalizer} to normalize each label
	 * @param romediInstance {@link RomediInstance}
	 * @param stopwordsRomedi {@link Stopwords} 
	 * @return An hashset of TokenTree containing alternative labels
	 */
	private static HashSet<TokenTree> getAltTokenTree(TokenizerNormalizer tokenizerNormalizer, RomediInstance romediInstance, Stopwords stopwordsRomedi){
		 HashSet<TokenTree> altTokenTrees = new HashSet<TokenTree>();
		 for (String altLabel : romediInstance.getAltLabels()) {
			 String libNormal = tokenizerNormalizer.normalizeLabel(altLabel);
			 TokenTree tokenTree = getTokenTree(libNormal, romediInstance.getIRI(), stopwordsRomedi);
			 if (tokenTree != null) {
				 altTokenTrees.add(tokenTree);
			 }
		 }
		 return(altTokenTrees);
	}
	
	/**
	 * Create a {@link TokenTree} for each hidden label (typo) and add it to a HashSet
	 * @param tokenizerNormalizer {@link TokenizerNormalizer} to normalize each label
	 * @param romediInstance {@link RomediInstance}
	 * @param stopwordsRomedi {@link Stopwords} 
	 * @return An hashset of TokenTree containing hidden labels
	 */
	private static HashSet<TokenTree> getHiddenTokenTree(TokenizerNormalizer tokenizerNormalizer,RomediInstance romediInstance, Stopwords stopwordsRomedi){
		 HashSet<TokenTree> altTokenTrees = new HashSet<TokenTree>();
		 for (String hiddenLabel : romediInstance.getHiddenLabels()) {
			 String libNormal = tokenizerNormalizer.normalizeLabel(hiddenLabel);
			 TokenTree tokenTree = getTokenTree(libNormal, romediInstance.getIRI(), stopwordsRomedi);
			 if (tokenTree != null) {
				 altTokenTrees.add(tokenTree);
			 }
		 }
		 return(altTokenTrees);
	}
	
	/**
	 * The function creating the {@link TokenTree}
	 * @param libNormal a normalized label of a terminology
	 * @param code the uri(code) of the terminology
	 * @param stopwordsRomedi A list of stopwords to remove in the label
	 * @return {@link TokenTree}
	 */
	private static TokenTree getTokenTree(String libNormal, String code, Stopwords stopwordsRomedi) {
		if (stopwordsRomedi.isStopWord(libNormal)) {
			return null;
		}
		String[] tokensArray = TokenizerNormalizer.tokenizeAlphaNum(libNormal);
		tokensArray = Loader.removeStopWords(stopwordsRomedi, tokensArray);
		if (tokensArray.length == 0) {
			return null;
		}
		TokenTree tokenTree = new TokenTree(null,tokensArray, code);
		return(tokenTree);
	}
}
