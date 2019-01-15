package fr.erias.IAMsystemRomedi.detect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;

/**
 * A stopwords implemntation for Romedi
 * @author Cossin Sebastien
 *
 */
public class StopwordsRomedi implements Stopwords {

	final static Logger logger = LoggerFactory.getLogger(TokenizerNormalizer.class);

	/**
	 * A set of stopwords
	 */
	private HashSet<String> stopwordsSet = new HashSet<String>();

	@Override
	public boolean isStopWord(String token) {
		// if the set of stopwords contains it
		if (stopwordsSet.contains(token.toLowerCase())) {
			return(true);
		}
		return(false);
	}

	/**
	 * Change the set of stopwords
	 * @param in An inputstream of a file containing a list of stopword ; one by line
	 * @throws IOException if the file can't be found
	 */
	public void setStopWords (InputStream in) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			//String normalizedLabel = normalizedSentence(line);
			stopwordsSet.add(line);
		}
		logger.info("stopwords size : " + stopwordsSet.size());
		br.close();
	}

	/**
	 * Change the set of stopwords
	 * @param file A file containing a list of stopword ; one by line
	 * @throws IOException if the file can't be found
	 */
	public void setStopWords (File file) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			//String normalizedLabel = normalizedSentence(line);
			stopwordsSet.add(line);
		}
		logger.info("stopwords size : " + stopwordsSet.size());
		br.close();
	}

	/**
	 * Get the set of stopwords
	 * @return A set of stopwords
	 */
	public HashSet<String> getStopWords(){
		return(stopwordsSet);
	}
}
