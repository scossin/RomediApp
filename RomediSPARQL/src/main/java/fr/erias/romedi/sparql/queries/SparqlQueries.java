package fr.erias.romedi.sparql.queries;

import java.util.HashSet;

import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediInstanceCIS;
import fr.erias.romedi.terminology.RomediType;


/**
 * A list of useful Sparql Queries
 * 
 * @author Cossin Sebastien
 *
 */
public class SparqlQueries {
	
	/***************************************** Labels **************************************************/
	
	/**
	 * get a Sparql Query to retrieve the labels of Romedi
	 * @param romediTypes An array of {@link RomediType}
	 * @return a String representing a sparql query
	 */
	public static String getQueryLabels(RomediType[] romediTypes) {
		StringBuilder sb = new StringBuilder();
		for (RomediType romediType : romediTypes) {
			sb.append(romediType.getRomediIRI().getIRI4query());
			sb.append(" ");
		}
		String query = "PREFIX romedi:<http://www.romedi.fr/romedi/> \n" + 
		        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" +
				"SELECT ?instance ?type ?label ?hiddenLabel ?altLabel \n" + 
				"WHERE { \n" + 
				"  # type\n" + 
				"       ?instance a ?type ;\n" + 
				"                   rdfs:label ?label.\n" + 
				"      OPTIONAL { ?instance skos:hiddenLabel ?hiddenLabel} \n" + 
				"      OPTIONAL { ?instance skos:altLabel ?altLabel} \n" + 
				"  # selection\n" + 
				"       Values ?type {"
				+ sb.toString()
				+ "} \n" + 
				"      }";
		return(query);
	}
	
	/**
	 * Retrieve hidden labels
	 * @param romediTypes @{link RomediType}
	 * @return a SPARQL query to retrieve hidden labels
	 */
	public static String getHiddenLabel(RomediType[] romediTypes) {
		StringBuilder sb = new StringBuilder();
		for (RomediType romediType : romediTypes) {
			sb.append(romediType.getRomediIRI().getIRI4query());
			sb.append(" ");
		}
		String query = "PREFIX romedi:<http://www.romedi.fr/romedi/> \n" + 
		        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" +
				"SELECT ?instance ?type ?hiddenLabel \n" + 
				"WHERE { \n" + 
				"  # type\n" + 
				"       ?instance a ?type ;\n" + 
				"                   skos:hiddenLabel ?hiddenLabel.\n" + 
				"  # selection\n" + 
				"       Values ?type {"
				+ sb.toString()
				+ "} \n" + 
				"      }";
		return(query);
	}
	
	/**
	 * Retrieve alternative lables
	 * @param romediTypes @{link RomediType}
	 * @return a SPARQL query to retrieve alternative labels
	 */
	public static String getAltLabel(RomediType[] romediTypes) {
		StringBuilder sb = new StringBuilder();
		for (RomediType romediType : romediTypes) {
			sb.append(romediType.getRomediIRI().getIRI4query());
			sb.append(" ");
		}
		String query = "PREFIX romedi:<http://www.romedi.fr/romedi/> \n" + 
		        "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" +
				"SELECT ?instance ?type ?altLabel \n" + 
				"WHERE { \n" + 
				"  # type\n" + 
				"       ?instance a ?type ;\n" + 
				"                   skos:altLabel ?altLabel.\n" + 
				"  # selection\n" + 
				"       Values ?type {"
				+ sb.toString()
				+ "} \n" + 
				"      }";
		return(query);
	}
	
	/**
	 * Retrieve all nodes linked to a IRI
	 * @param romediInstance a {@link RomediInstance}
	 * @return a SPARQL query
	 */
	public static String getInitialRequest(RomediInstance romediInstance) {
		String templateRequest = "PREFIX romedi:<http://www.romedi.fr/romedi/>\n" + 
				"SELECT distinct ?CIS ?label ?isCommercialized \n" + 
				"WHERE { \n" + 
				"  # type\n" +
				"      ?CIS a romedi:CIS .\n" + 
				"      ?BNdosage a romedi:BNdosage . \n" + 
				"      ?BN a romedi:BN . \n" + 
				"  	   ?PIN a romedi:PIN.\n" + 
				"  	   ?PINdosage a romedi:PINdosage .\n" + 
				"      ?IN a romedi:IN .\n" + 
				"      ?INdosage  a romedi:INdosage .\n" + 
				"      ?CIP13 a romedi:CIP13 .\n" +
				"      ?DrugClass a romedi:DrugClass .\n" +
				"  \n" + 
				"  # links\n" + 
			    " 	   ?CIS romedi:isCommercialized  ?isCommercialized . \n" +
				"      ?CIS rdfs:label ?label                          . \n" +
			    " 	   ?CIS romedi:CIShasCIP13 ?CIP13 .\n" + 
				"      ?CIS romedi:CIShasBNdosage ?BNdosage . \n" + 
				"      ?CIS romedi:CIShasPINdosage ?PINdosage .\n" + 
				"      ?PINdosage romedi:PINdosagehasINdosage ?INdosage .\n" + 
				"      ?PINdosage romedi:PINdosagehasPIN ?PIN .\n" + 
				"      ?INdosage romedi:INdosagehasIN ?IN .\n" + 
				"      ?BNdosage romedi:BNdosagehasBN ?BN .\n" + 
				"      ?CIS romedi:CIShasATC7 ?ATC7 .\n" + 
				"      ?CIS romedi:CIShasATC5 ?ATC5 .\n" + 
				"      ?CIS romedi:CIShasATC4 ?ATC4 .\n" + 
				"      ?CIS romedi:CIShasDrugClass ?DrugClass .\n" +
				"  \n" + 
				"  # selection\n" + 
				"       Values ?" + romediInstance.getType().toString() + "{<"+romediInstance.getIRI()+">}\n" + 
				"      }\n" + 
				"\n" + 
				"ORDER BY DESC(?CIS)";
		return(templateRequest);
	}
	
	
	/**
	 * Count different type
	 */
	public static final String countType = "PREFIX romedi:<http://www.romedi.fr/romedi/>\n" + 
			"SELECT ?type (count (?type) as ?total) where {   \n" + 
			"  ?drug rdf:type ?type\n" + 
			"}\n" + 
			"group by ?type";
	
	/**
	 * Count IN linked to INdosage
	 */
	public static final String countIN = "PREFIX romedi:<http://www.romedi.fr/romedi/>\n" + 
			"SELECT (count (distinct ?IN) as ?total) where {    \n" + 
			"   ?INdosage  a romedi:INdosage . \n" + 
			"  ?IN a romedi:IN . \n" + 
			"  ?INdosage romedi:INdosagehasIN ?IN . \n" + 
			"}";
	
	/**
	 * Count no label
	 */
	public static String countNoLabel = "PREFIX romedi:<http://www.romedi.fr/romedi/>\n" + 
			"SELECT ?type (count (distinct ?drug) as ?total) where {    \n" + 
			"   ?drug  a ?type . \n" + 
			"  MINUS { ?drug rdfs:label ?label . } \n" + 
			"}\n" + 
			"GROUP BY ?type";
	
	
	/**
	 * Retrieve all node linked to all CIS retrieved by the first request
	 * @param romediInstancesCIS a Set of {@link RomediInstance}
	 * @return a SPARQL query
	 */
	public static String getCISRequest(HashSet<RomediInstanceCIS> romediInstancesCIS) {
		String IRIs = null;
		StringBuilder sb = new StringBuilder();
		for (RomediInstance romediInstanceCIS : romediInstancesCIS) {
			String fullURI = romediInstanceCIS.getIRI();
			sb.append(" <");
			sb.append(fullURI);
			sb.append("> ");
		}
		IRIs = sb.toString();
		String templateRequest = "PREFIX romedi:<http://www.romedi.fr/romedi/>\n" + 
				"SELECT ?ATC7 ?ATC5 ?ATC4 ?CIP13 ?CIS ?BNdosage ?BN ?PINdosage ?PIN ?INdosage ?IN ?DrugClass\n" + 
				"WHERE { \n" + 
				"  # type\n" + 
				"       ?CIS a romedi:CIS .\n" + 
				"       ?BNdosage a romedi:BNdosage . \n" + 
				"       ?BN a romedi:BN . \n" + 
				"  		?PIN a romedi:PIN.\n" + 
				"  		?PINdosage a romedi:PINdosage .\n" + 
				"  		?IN a romedi:IN .\n" + 
				"  		?INdosage  a romedi:INdosage .\n" + 
				"  		?CIP13 a romedi:CIP13 .\n" +
				"  		?DrugClass a romedi:DrugClass .\n" +
				"  \n" + 
				"  # links\n" +
				"      ?CIS romedi:CIShasCIP13 ?CIP13 .\n" + 
				"      ?CIS romedi:CIShasBNdosage ?BNdosage . \n" + 
				"      ?CIS romedi:CIShasPINdosage ?PINdosage .\n" + 
				"      ?PINdosage romedi:PINdosagehasINdosage ?INdosage .\n" + 
				"      ?PINdosage romedi:PINdosagehasPIN ?PIN .\n" + 
				"      ?INdosage romedi:INdosagehasIN ?IN .\n" + 
				"      ?BNdosage romedi:BNdosagehasBN ?BN .\n" +
				"      ?CIS romedi:CIShasATC7 ?ATC7 .\n" + 
				"      ?CIS romedi:CIShasATC5 ?ATC5 .\n" + 
				"      ?CIS romedi:CIShasATC4 ?ATC4 .\n" + 
				"      ?CIS romedi:CIShasDrugClass ?DrugClass .\n" +
				"  \n" + 
				"  # selection\n" + 
				"       Values ?CIS {" + IRIs + "}\n" + 
				"      }\n" + 
				"\n" + 
				"ORDER BY DESC(?CIS)";
		return(templateRequest);
	}
}
