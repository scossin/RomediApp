package fr.erias.IAMsystemRomedi.config;

/**
 * Configuration file for the project
 * @author Cossin Sebastien
 *
 */
public class ConfigRomedi {
	
	/************************************************* Drugs : ******************************************/
	
	private static final String DRUGS_FOLDER = "drugs/";
	
	
	
	
	
	/*********************************** See uriExcluded.R and termino2CSV.R to produce these files :
	/**
	 * Drugs dictionary file:
	 */
	public static final String romediTermsINBN = DRUGS_FOLDER + "RomediBNINselection.csv";
	
	/**
	 * Alternative and typos: 
	 */
	public static final String hiddenAltLabels = DRUGS_FOLDER + "hiddenAltLabels.csv";
	
	
	/**
	 * list of Romedi instances excluded (or, thym, eau...):
	 */
	public static final String uriExcluded = DRUGS_FOLDER + "uriExcluded.txt";
	
	
	
	
	
	
	
	/**
	 * Stopwords file for drugs (very few words)
	 */
	public static final String STOPWORDS_DRUGS_FILE = DRUGS_FOLDER + "stopwordsRomedi.txt";
	
	/**
	 * List of words to ignore for typo detection: (a list of common French words)
	 */
	public static final String frenchTermsNormalized = "liste_francais_normalise.csv";
	
	public static final String frenchTermsNormalized2 = "liste_francais_ajout.csv";

	/**
	 * The name Lucene index folder to perform fuzzy queries (Levenshtein distance). 
	 * The folder must be in the src/main/resources folder of the maven project to let {@link ClassLoader} find it
	 */
	public static final String INDEX_FOLDER = "IndexUniqueTokens";
	
	/**
	 * The fieldname of the Lucene index that contains bigram. ex : "meningo encephalite"
	 */
	public static final String BIGRAM_FIELD = "bigram";
	
	/**
	 * The fieldname that contains the concatenation of the bigram. ex : "meningoencephalite"
	 */
	
	public static final String CONCATENATION_FIELD = "collapse";
	
	/**
	 * To load resources with classLoader's context
	 */
	public final static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
}
