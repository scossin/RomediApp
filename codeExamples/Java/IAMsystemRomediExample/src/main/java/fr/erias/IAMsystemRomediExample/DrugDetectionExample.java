package fr.erias.IAMsystemRomediExample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.script.ScriptException;
import org.apache.lucene.queryparser.classic.ParseException;

import com.pengyifan.brat.BratDocument;

import fr.erias.IAMsystem.brat.BratDocumentWriter;
import fr.erias.IAMsystem.brat.CTbrat;
import fr.erias.IAMsystem.ct.CTcode;
import fr.erias.IAMsystem.exceptions.UnfoundTokenInSentence;
import fr.erias.IAMsystem.normalizer.Stopwords;
import fr.erias.IAMsystemRomedi.config.ConfigRomedi;
import fr.erias.IAMsystemRomedi.detect.DetectDrug;
import fr.erias.romedi.sparql.connection.ConfigEndpoint;
import fr.erias.romedi.sparql.terminology.RomediTerminologySPARQL;
import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;

/**
 * Romedi DrugDetection API
 * 
 * @author Cossin Sebastien
 *
 */
public class DrugDetectionExample 
{
	/**
	 * Remove some URIs (eau, huile d'olive...)
	 * @throws IOException 
	 */
	public HashSet<RomediIRI> getRomediIRI2Remove() throws IOException {
		HashSet<RomediIRI> iri2remove = new HashSet<RomediIRI>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream in = classLoader.getResourceAsStream(ConfigRomedi.uriExcluded);
		BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
		String line ;
		while ((line = br.readLine()) !=null) {
			RomediIRI romediIRI = new RomediIRI(line);
			iri2remove.add(romediIRI);
		}
		br.close();
		in.close();
		return(iri2remove);
	}
	
	private DetectDrug detectDrug;
	
	public DetectDrug getDetectDrug() {
		return(detectDrug);
	}
	
	/**
	 * Wanted RomediType (only retrieve the labels and code of these types)
	 */
	private RomediType[] romediTypes = {RomediType.BN, RomediType.IN, RomediType.PIN, RomediType.BNdosage};
	
	/**
	 * A Romedi SPARQL endpoint to retrieve the terminology
	 * 
	 * @param endpoint
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws ScriptException 
	 */
	
	public DrugDetectionExample(String endpoint) throws IOException, ScriptException, URISyntaxException {
		// Retrieve the terminology from an endpoint
		RomediTerminologySPARQL romediTerminologySPARQL = new RomediTerminologySPARQL(endpoint,romediTypes);
		RomediTerminology romediTerminology = romediTerminologySPARQL.getRomediTerminology();
		// remove unwanted URI (ambiguous words such 'eau', 'thym'...)
		romediTerminology.removeURI(getRomediIRI2Remove());
		
		Stopwords stopwordsRomedi = DetectDrug.getStopwordsRomedi();
		
		// new instance to detect drug
		detectDrug = new DetectDrug(romediTerminology, stopwordsRomedi);
		
		// Lucene Index creation to detect typo
		detectDrug.createLuceneIndex(romediTerminology, stopwordsRomedi);
		detectDrug.addLuceneTypoDetection();
	}
			
    public static void main( String[] args ) throws IOException, ScriptException, URISyntaxException, UnfoundTokenInSentence, ParseException
    {
    	String endpoint = ConfigEndpoint.chosenEndpoint; // autodetect the endpoint
    	System.out.println("ChosenEndpoint:" + endpoint);
    	DrugDetectionExample drugDetectionExample = new DrugDetectionExample(endpoint);
    	String text = "Traitement à l'admission: MTX, dompéridonne et escitalopram et donormil";
    	Set<CTcode> CTcodes = drugDetectionExample.getDetectDrug().getCTcodes(text);
    	for (CTcode CTcode : CTcodes) {
    		// extract whatever you want from CTcode
    		System.out.println(CTcode.getJSONobject().toString());
    	}
    	
		
		
		
		// Brat output example (in the console):
    	String bratType = "drug";
		BratDocumentWriter bratDocumentWriter = new BratDocumentWriter(new PrintWriter(System.out));
		BratDocument doc = new BratDocument();
		for (CTcode codes : CTcodes) {
			CTbrat ctbrat = new CTbrat(codes, bratType);
			doc.addAnnotation(ctbrat.getBratEntity());
		}
		bratDocumentWriter.write(doc);
		bratDocumentWriter.close();
		CTbrat.resetIdNumber(); // for the next document : reset the IdNumber to 1
		System.out.println("------------------ End --------------------------");
    }
}
