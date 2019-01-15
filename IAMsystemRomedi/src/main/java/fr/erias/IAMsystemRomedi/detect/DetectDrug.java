package fr.erias.IAMsystemRomedi.detect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.detect.DetectDictionaryEntry;
import fr.erias.IAMsystem.detect.Synonym;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.load.Loader;
import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystem.tokenizer.TokenizerNormalizer;
import fr.erias.IAMsystem.tree.SetTokenTree;
import fr.erias.IAMsystemRomedi.config.ConfigRomedi;
import fr.erias.IAMsystemRomedi.soundex.LevenshteinTypoLucenePhonetic;
import fr.erias.IAMsystemRomedi.soundex.PredictTypoImp1;
import fr.erias.IAMsystemRomedi.terminology.RomediTermino2SetTokenTree;
import fr.erias.IAMsystemRomedi.terminology.RomediTerminologyCSV;
import fr.erias.romedi.terminology.RomediTerminology;

/**
 * A class to detect drugs in textual content
 * @author Cossin Sebastien
 */
public class DetectDrug {

	final static Logger logger = LoggerFactory.getLogger(DetectDrug.class);

	private DetectDictionaryEntry detectDictionaryEntry;

	/**
	 * 
	 * @return A {@link DetectDictionaryEntry} that performs the detection
	 */
	public DetectDictionaryEntry getDetectDictionaryEntry() {
		return(detectDictionaryEntry);
	}
	
	/**
	 * This constructor creates a new DetectDrug instance by loading local CSV files of the Romedi Terminology (possibly outdated)<br>
	 * See the R program in /src/main/resources to create these local files
	 * This constructor can be used if a SparqlEndpoint can't be set or reached 
	 * It's better to use a SparqlEndpoint to retrieve the last version of the terminology
	 * @throws IOException Impossible to found the file
	 */
	@Deprecated
	public DetectDrug() throws IOException {
		InputStream in = ConfigRomedi.classLoader.getResourceAsStream(ConfigRomedi.romediTermsINBN);
		RomediTerminologyCSV romediTerminologyCSV = new RomediTerminologyCSV(in,"\t",2, 0,1);
		in.close();
		in = ConfigRomedi.classLoader.getResourceAsStream(ConfigRomedi.hiddenAltLabels);
		romediTerminologyCSV.addAdditionalLabels(in,"\t",2, 0,3);
		RomediTerminology romediTerminology = romediTerminologyCSV.getRomediTerminology();
		in.close();
		SetTokenTree tokenTreeSet0 = RomediTermino2SetTokenTree.getSetTokenTree(romediTerminology, getStopwordsRomedi());
		// load the dictionary of ingredient and brand name normalized :
		this.initialize(tokenTreeSet0, getStopwordsRomedi());
	}

	/**
	 * Create a new DetectDrug instance by loading an up-to-date Romedi Terminology 
	 * New instance of DetectDrug to detect Romedi terms in textual content
	 * @param romediTerminology A {@link RomediTerminology}
	 * @param stopwordsRomedi A {@link Stopwords}
	 */
	public DetectDrug(RomediTerminology romediTerminology, Stopwords stopwordsRomedi) {
		SetTokenTree tokenTreeSet0 = RomediTermino2SetTokenTree.getSetTokenTree(romediTerminology, stopwordsRomedi);
		this.initialize(tokenTreeSet0, stopwordsRomedi);
	}
	
	/**
	 * @param tokenTreeSet0 A tree datastructure of the Romedi terminology. See {@link SetTokenTree}
	 * @param stopwordsRomedi A {@link Stopwords}
	 */
	private void initialize(SetTokenTree tokenTreeSet0, Stopwords stopwordsRomedi){
		logger.info("Initializing a DetectDrug instance");
		logger.info("Creating a TokenizerNormalizer");
		// Tokenizer
		TokenizerNormalizer tokenizerNormalizer = Loader.getTokenizerNormalizer(stopwordsRomedi);
		logger.info("Loading abbreviations / typos :");
		// Abbreviations : 
		AbbreviationsDrug abbreviationsDrug = new AbbreviationsDrug();
		// find synonyms with abbreviations and typos : 
		HashSet<Synonym> synonyms = new HashSet<Synonym>();
		synonyms.add(abbreviationsDrug);
		this.detectDictionaryEntry = new DetectDictionaryEntry(tokenTreeSet0, tokenizerNormalizer,synonyms);
	}
	
	/**
	 * Create a Lucene Index for typo detection
	 * @param romediTerminology A {@link RomediTerminology} to index. Each unigram of all the terms will be indexed and used for typo detection
	 * @param stopwordsRomedi A {@link Stopwords}
	 * @throws IOException Impossible to create the Lucene index
	 */
	public void createLuceneIndex(RomediTerminology romediTerminology, Stopwords stopwordsRomedi) throws IOException {
		logger.info("trying to create a Lucene Index...");
		new IndexBigramLuceneRomedi(ConfigRomedi.INDEX_FOLDER,romediTerminology, stopwordsRomedi);
	}

	/**
	 * A Lucene index is needed to detect typo. See {@link PredictTypoImp1} to see how it works
	 * 
	 * If the lucene index is in a jar file, we will have performance issues :
	 * https://stackoverflow.com/questions/25183689/apache-lucene-how-to-read-segment-index-files-from-a-jar
	 * It's better to create the Lucene index at run-time than packaging it to a jar
	 * @throws IOException Js File can't be loaded
	 * @throws ScriptException Javascript error
	 * @throws URISyntaxException  Javascript error
	 */
	public void addLuceneTypoDetection() throws ScriptException, IOException, URISyntaxException {
		// create the folder : 
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); // classLoader of IAMsystemRomedi for loading files compressed below
		logger.info("Opening lucene index");
		// Levenshtein distance : activated 
		File indexFolder = new File(ConfigRomedi.INDEX_FOLDER);
		if (!indexFolder.exists()) {
			logger.error("unfound Lucene Index ! : " + indexFolder.getAbsolutePath() + " doesn't exist");
			return;
		}

		PredictTypoImp1 predictTypoImp1 = new PredictTypoImp1();
		LevenshteinTypoLucenePhonetic levenshteinTypoLucenePhonetic = new LevenshteinTypoLucenePhonetic(indexFolder,
				ConfigRomedi.CONCATENATION_FIELD, ConfigRomedi.BIGRAM_FIELD, predictTypoImp1);

		// load a file with french words and add it to the dictionnary: 
		InputStream in = classLoader.getResourceAsStream(ConfigRomedi.frenchTermsNormalized);
		//in = new BufferedInputStream(new FileInputStream(CSVFile));
		HashSet<String> frenchTerms = Loader.getUniqueToken(in, "\t", 1);
		in.close();

		levenshteinTypoLucenePhonetic.addUnmatched(frenchTerms);
		// adding more terms manually added :
		in = classLoader.getResourceAsStream(ConfigRomedi.frenchTermsNormalized2);
		frenchTerms = Loader.getUniqueToken(in, "\t", 0);
		in.close();

		levenshteinTypoLucenePhonetic.addUnmatched(frenchTerms);
		this.detectDictionaryEntry.addSynonym(levenshteinTypoLucenePhonetic);
	}
	
	/**
	 * Get the default Romedi Stopwords instance
	 * @return The default {@link Stopwords} for Romedi
	 * @throws IOException the Romedi stopwords file is not found
	 */
	public static StopwordsRomedi getStopwordsRomedi() throws IOException {
		logger.info("Loading stopwords");
		InputStream in = ConfigRomedi.classLoader.getResourceAsStream(ConfigRomedi.STOPWORDS_DRUGS_FILE);
		StopwordsRomedi stopwordsRomedi = new StopwordsRomedi();
		in.close();
		return(stopwordsRomedi);
	}

	/**
	 * Extract Romedi terms from textual content
	 * @param txtContent a textual content
	 * @return {@link CTcode}: Romedi URI codes detected with labels and offsets
	 * @throws UnfoundTokenInSentence Tokenization error
	 */
	public synchronized Set<CTcode> getCTcodes(String txtContent) throws UnfoundTokenInSentence{
		this.detectDictionaryEntry.detectCandidateTerm(txtContent);
		return(this.detectDictionaryEntry.getCTcode());
	}
}

