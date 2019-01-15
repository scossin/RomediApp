package fr.erias.IAMsystemRomedi.soundex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.detect.HashSetStringArray;
import fr.erias.IAMsystem.detect.Synonym;
import fr.erias.IAMsystem.exceptions.MyExceptions;
import fr.erias.IAMsystem.lucene.SearchIndex;

/**
 * This class uses Lucene to perform a Levenshtein distance and uses a {@link PredicTypo instance} to predict if it's a typo or not
 * @author Cossin Sebastien
 *
 */
public class LevenshteinTypoLucenePhonetic implements Synonym {
	
	final static Logger logger = LoggerFactory.getLogger(LevenshteinTypoLucenePhonetic.class);
	
	/**
	 * A lucene search engine to perform Levenshtein distance
	 */
	private SearchIndex searchIndex = null;
	
	/**
	 * the name of the Lucene field in the index containing a bigram concatenation (ex : meningoencephalite)
	 */
	private String concatenationField = null;
	
	/**
	 * bigramField the name of the Lucene field in the index containing the initial bigram (ex : meningo encephalite)
	 * Not used here !
	 */
	 private String bigramField = null;
	
	/**
	 * Save unmatched, no need to search multiples times the same token that had no match
	 */
	private HashSet<String> unmatched = new HashSet<String>() ;
	
	/**
	 * Save matched, no need to search multiples times the same token that had a match 
	 */
	private HashMap<String, HashSet<String[]>> matched = new HashMap<String, HashSet<String[]>>();
	
	
	private PredicTypo predicTypo;
	
	/**
	 * store typoDetected and the prediction
	 */
	private File typoFile = new File("typoFile.csv");
	
	/**
	 * Constructor 
	 * @param indexFolder The indexFolder of the Lucene Index to perform fuzzy queries (Levenshtein distance)
	 * @param concatenationField the name of the Lucene field in the index containing a bigram concatenation (ex : meningoencephalite)
	 * @param bigramField the name of the Lucene field in the index containing the initial bigram (ex : meningo encephalite)
	 * @param predicTypo An instance to predict if a word is a typo or not
	 * @throws IOException If the Lucene index can't be found
	 */
	public LevenshteinTypoLucenePhonetic(File indexFolder, String concatenationField, 
			String bigramField, PredicTypo predicTypo) throws IOException {
		this.searchIndex = new SearchIndex(indexFolder);
		this.concatenationField = concatenationField;
		this.bigramField = bigramField;
		this.predicTypo = predicTypo;
		// a file to store typos detected
		if (!typoFile.exists()) {
			typoFile.createNewFile();
		}
		this.predicTypo = predicTypo;
	}
	
	/**
	 * Create an instance to connect to the Lucene Index
	 * @return the searchIndex instance
	 */
	public SearchIndex getSearchIndex() {
		return(searchIndex);
	}
	
	
	/**
	 * Search a normalized term with a Levenshtein distance (fuzzy query of Lucene)
	 * @param term The normalized term to search in the Lucene Index
	 * @return A set of synonyms
	 * @throws IOException If the index is not found
	 * @throws ParseException If the Lucene query fails
	 */
	private HashSet<String[]> searchIndexLeven(String term) throws IOException, ParseException {
		// return this : exact term and typos in term
		HashSetStringArray synonyms = new HashSetStringArray(); // A customized HashSet of array
		
		logger.debug("search a typo for : " + term);
		
		// don't search anything if less than 4 characters (to avoid noise)
		if (term.length()<5) { 
			return(synonyms);
		}

		int maxEdits = 1; // number of insertion, deletion of the Levenshtein distance. Max 2
		int maxHits = 20; // number of maximum hits - results

		// search a typo in a term (ex : cardique for cardiaque) or a concatenation (meningoencephalite for meningo encephalite)
		Query query = searchIndex.fuzzyQuery(term, concatenationField, maxEdits);
		ScoreDoc[] hits = searchIndex.evaluateQuery(query, maxHits);
		
		logger.debug("number of hits : " + hits.length);
		
		// if no hits return
		if (hits.length == 0) {
			addUnmatched(term);
			return(synonyms);
		}

		// if hits, add the array of synonyms
		for (int i = 0; i<hits.length ; i++) {
			Document doc = searchIndex.getIsearcher().doc(hits[i].doc);
			// no bigram here ; it's just a unigram
			// String bigram = doc.get(bigramField);
			String unigram = doc.get(concatenationField);
			if (unigram.equals(term)) {
				continue;
			}
			logger.debug("detected typo : " + unigram);
			
			// check the prediction : 
			boolean isTypo = predicTypo.isTypo(term, unigram);
			writeTypoDetected(term,unigram,isTypo);
			
			// if it's not a typo, then continue
			if (!isTypo) {
				continue;
			}
			
			// just an array of length 1 :
			String[] bigramArray = unigram.split(" ");
			if (!synonyms.containsArray(bigramArray)) {
				synonyms.add(bigramArray);
			}
		}
		logger.debug("synonyms size : " + synonyms.size());
		
		addMatched(term, synonyms);
		return(synonyms);
	}
	
	private void writeTypoDetected(String word1, String word2, boolean prediction) throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter(typoFile,true));
		String line = word1 + "\t" + word2 + "\t" + prediction + "\n";
		wr.write(line);
		wr.close();
	}
	
	/**
	 * Add an unmatched token that was not found in the Lucene Index
	 * @param token an unmatched token
	 */
	private void addUnmatched(String token) {
		unmatched.add(token);
	}
	
	/**
	 * Add a set of unmatched terms - words that won't be matched or we don't want to be matched
	 * @param tokensSet A set of tokens
	 */
	public void addUnmatched(Set<String> tokensSet) {
		unmatched.addAll(tokensSet);
	}
	
	/**
	 * Add a matched token that was found in the Lucene Index
	 * @param token a token found in this index
	 * @param synonyms a list of "synonyms" (aka lexical variants)
	 */
	private void addMatched(String token, HashSet<String[]> synonyms) {
		matched.put(token, synonyms);
	}
	
	@Override
	public HashSet<String[]> getSynonyms(String token) {
		HashSet<String[]> output = new HashSet<String[]>();
		// if already searched and no matched
		if (unmatched.contains(token)) {
			return(output);
		}
		
		// if already searched and matched found 
		if (matched.containsKey(token)) {
			output = matched.get(token);
			return(output);
		}
		
		// if it's the first time we searched this token :
		try {
			output = searchIndexLeven(token);
		} catch (IOException | ParseException e) {
			logger.debug("an error occured while searching in the lucene index");
			MyExceptions.logException(logger, e);
		}
		return(output);
	}

}
