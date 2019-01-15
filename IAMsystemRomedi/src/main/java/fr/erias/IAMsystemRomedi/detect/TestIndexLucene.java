package fr.erias.IAMsystemRomedi.detect;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import fr.erias.IAMsystem.lucene.SearchIndex;
import fr.erias.IAMsystemRomedi.config.ConfigRomedi;

/**
 * A class to text the Lucene Index
 * 
 * @author Cossin Sebastien
 *
 */
public class TestIndexLucene {

	public static void main(String[] args) throws IOException {
		// only work if the Index exists
		SearchIndex searchIndex = new SearchIndex(new File(ConfigRomedi.INDEX_FOLDER));
		String concatenationField = ConfigRomedi.CONCATENATION_FIELD;
		String term = "escitalopramm";
		
		int maxEdits = 1; // number of insertion, deletion of the Levenshtein distance. Max 2
		int maxHits = 20; // number of maximum hits - results

		// search a typo in a term (ex : cardique for cardiaque) or a concatenation (meningoencephalite for meningo encephalite)
		Query query = searchIndex.fuzzyQuery(term, concatenationField, maxEdits);
		ScoreDoc[] hits = searchIndex.evaluateQuery(query, maxHits);
		System.out.println(hits.length);
		
		searchIndex.closeReader();
	}
}
