package fr.erias.IAMsystemRomedi.detect;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.lucene.IndexBigramLucene;
import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystemRomedi.config.ConfigRomedi;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediTerminology;

/**
 * Create a Lucene index to detect typos
 * 
 * @author Cossin Sebastien
 *
 */
public class IndexBigramLuceneRomedi {
	/**
	 * Create a Lucene index to detect typos
	 * @param indexFolder name of the index
	 * @param romediTerminology A {@link RomediTerminology}
	 * @param stopwords A {@link Stopwords}
	 * @throws IOException Can't create the index
	 */
	public IndexBigramLuceneRomedi(String indexFolder,RomediTerminology romediTerminology, Stopwords stopwords) throws IOException {
		HashMap<String,String> uniqueTokensBigram = getUniqueToken2index(romediTerminology,stopwords);
		IndexBigramLucene.IndexLuceneUniqueTokensBigram(uniqueTokensBigram, new File(indexFolder), 
				ConfigRomedi.CONCATENATION_FIELD,ConfigRomedi.BIGRAM_FIELD);
	}
	
	/**
	 * Tokenize all the terms and keep a set of unique token. <br>
	 * The difference with {@link Loader#getUniqueTokenBigram(Stopwords, File, String, int)} is we just ignore bigram concatenation
	 * @param stopwords a {@link Stopwords} instance
	 * @param fileCSV a CSV file
	 * @param sep the separator of the CSV file (ex : "\t")
	 * @param colLibNormal the ith column containing the libnormal (normalized label of the term)
	 * @return a set of unique tokens in the vocabulary
	 * @throws IOException if the file can't be found
	 */
	private HashMap<String,String> getUniqueToken2index(RomediTerminology romediTerminology, Stopwords stopwords) throws IOException{
		HashMap<String,String> uniqueTokens = new HashMap<String,String>();
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwords);
		for (RomediInstance romediInstance : romediTerminology.getMapURI2instance().values()) {
			String label = romediInstance.getPrefLabel();
			String libNormal = tokenizerNormalizer.normalizeLabel(label);
			String[] tokensArray = TokenizerNormalizer.tokenizeAlphaNum(libNormal);
			tokensArray = Loader.removeStopWords(stopwords, tokensArray);
			for (String token : tokensArray) {
				if (token.length() < 5) { // we won't search for a typo index if the word is less than 5 characters
					continue;
				}
				uniqueTokens.put(token, token);
			}
		}
		return(uniqueTokens);
	}
}
