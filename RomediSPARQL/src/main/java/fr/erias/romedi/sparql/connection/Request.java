package fr.erias.romedi.sparql.connection;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.romedi.sparql.queries.SendQuery;
import fr.erias.romedi.sparql.queries.SparqlQueries;
import fr.erias.romedi.sparql.terminology.RomediTerminologySPARQL;
import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediInstanceCIS;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediType;
import fr.erias.romedi.terminology.UnknownRomediURI;

/**
 * Retrieve CIS from a RomediInstance and its links
 * 
 * @author Cossin Sebastien
 *
 */
public class Request {

	final static Logger logger = LoggerFactory.getLogger(Request.class);

	/**
	 * Connection to SPARQL endpoint
	 */
	private DBconnection connection = null;

	/**
	 * The romediTerminology
	 */
	private RomediTerminology romediTerminology;

	/**
	 * @return A {@link RomediTerminology}
	 */
	public RomediTerminology getRomediTerminology() {
		return(romediTerminology);
	}

	/**
	 * The different types returned by the SPARQL request
	 */
	public static RomediType[] outputType = {
			RomediType.BN, 
			RomediType.BNdosage,
			RomediType.IN,
			RomediType.CIS, 
			// RomediType.INdosage, 
			//RomediType.PIN, 
			//RomediType.PINdosage, 
			RomediType.UCD13,
			RomediType.CIP13,
			RomediType.ATC7, 
			RomediType.ATC5, 
			RomediType.ATC4,
			RomediType.DrugClass}; 

	/**
	 * @param connection  Connection to SPARQL endpoint
	 * @throws UnknownRomediType Unknown romediType
	 */
	public Request(DBconnection connection) throws UnknownRomediType {
		RomediTerminologySPARQL romediTerminologySPARQL = new RomediTerminologySPARQL(connection,outputType);
		this.romediTerminology = romediTerminologySPARQL.getRomediTerminology();
		logger.info("romediTerminology size:" + this.romediTerminology.getMapURI2instance().size());
		this.connection = connection;
	}

	/********************************************** Getters : *******************************/

	/**
	 * Key function to retrieve CIS
	 * Retrieve all RomediInstance of type "CIS" from any given romediInstance (retrieve all drugs from an ingredient, brand name...)
	 * @param romediInstance any {@link RomediInstance} 
	 * @return a set of {@link RomediInstanceCIS} 
	 * @throws UnknownRomediURI URI not found
	 */
	public HashSet<RomediInstanceCIS> getCisIRI(RomediInstance romediInstance) throws UnknownRomediURI{
		HashSet<RomediInstanceCIS> cisIRI = new HashSet<RomediInstanceCIS>();
		String queryString = SparqlQueries.getInitialRequest(romediInstance);
		TupleQueryResult tupleResult = SendQuery.sendQuery(this.connection,queryString);
		while(tupleResult.hasNext()){
			BindingSet set = tupleResult.next();
			// CIS:
			Value valueCIS = set.getValue(RomediType.CIS.toString());
			IRI iriCIS = getIRI(valueCIS);
			RomediIRI romediIRIcis = new RomediIRI(iriCIS.getLocalName());

			// preflabel:
			Value valueLabel = set.getValue("label");
			String prefLabel = valueLabel.stringValue();

			// isCommercialized boolean :
			Value valueCom = set.getValue("isCommercialized");
			Boolean isCommercialized = valueCom.stringValue().equals("true");
			RomediInstanceCIS romediInstanceCIS = new RomediInstanceCIS(romediIRIcis,RomediType.CIS,prefLabel);
			romediInstanceCIS.setIsCommercialized(isCommercialized);
			cisIRI.add(romediInstanceCIS);
		}
		tupleResult.close();
		logger.info("number of CIS found : " + cisIRI.size());
		return(cisIRI);
	}

	/**
	 * Key function to retrieve links for a CIS
	 * Retrieve all the romediInstances (ingredient, brand name...) link to a CIS
	 * @param romediInstancesCIS a set of {@link RomediInstanceCIS}
	 * @return a collection of {@link ResultLinks}
	 */
	public Collection<ResultLinks> getResultsLinks(HashSet<RomediInstanceCIS> romediInstancesCIS) {
		HashMap<RomediIRI,ResultLinks> mapCISlinks = new HashMap<RomediIRI,ResultLinks>();
		// for each CIS
		for (RomediInstanceCIS romediInstanceCIS : romediInstancesCIS) {
			ResultLinks res = new ResultLinks(romediInstanceCIS, Request.outputType); // one per CIS
			mapCISlinks.put(romediInstanceCIS.getRomediIRI(), res);
		}
		// retrieve all links in one single query
		// it's much faster like this than one CIS at a time
		String queryString = SparqlQueries.getCISRequest(romediInstancesCIS);
		
		TupleQueryResult tupleResult = SendQuery.sendQuery(this.connection,queryString);
		while (tupleResult.hasNext()) {
			BindingSet set = tupleResult.next(); // next row
			// CIS involved by this row :
			Value valueCIS = set.getValue(RomediType.CIS.toString());
			IRI iriCIS = getIRI(valueCIS);
			RomediIRI romediIRIcis = new RomediIRI(iriCIS.getLocalName());
			mapCISlinks.get(romediIRIcis).addSet(set, romediTerminology, outputType); // add link to CIS involved
		}
		tupleResult.close();
		return(mapCISlinks.values());
	}
	
	
	/**
	 * Get the IRI from a Value object by down casting the Value object
	 * @param value
	 * @return
	 */
	private IRI getIRI(Value value) {
		IRI iri = null; 
		try{
			iri = (IRI) value;
		} catch (ClassCastException e){
			logger.info("error");
		}
		return(iri);
	}
	

	/**
	 * Retrieve a {@link RomediInstance} by its URI 
	 * @param romediIRI a {@link RomediIRI}
	 * @return a {@link RomediInstance}
	 * @throws UnknownRomediURI the uri was not found
	 */
	public RomediInstance getRomediInstance(RomediIRI romediIRI) throws UnknownRomediURI {
		return romediTerminology.getRomediInstance(romediIRI);
	}

	// test
	public static void main(String[] args) throws FileNotFoundException, UnknownRomediType, UnknownRomediURI {
		DBconnection connection = new DBconnection(ConfigEndpoint.chosenEndpoint);
		Request request = new Request(connection);
		String uri = "INkr028ur6tprf3l98f6nnt0vlobdaqame";
		RomediIRI romediIRI = new RomediIRI(uri);
		// request.searchIRI(romediIRI);
		RomediInstance romediInstance = request.getRomediInstance(romediIRI);
		HashSet<RomediInstanceCIS> romediInstancesCIS = request.getCisIRI(romediInstance);
		for (RomediInstanceCIS romediInstanceCIS : romediInstancesCIS) {
			System.out.println(romediInstanceCIS.getJSONObject().toString());
		}
		connection.close();
	}

}
