package fr.erias.romedi.sparql.connection;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.erias.romedi.sparql.queries.SendQuery;
import fr.erias.romedi.sparql.queries.SparqlQueries;
import fr.erias.romedi.sparql.terminology.RomediTerminologySPARQL;
import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediType;
import fr.erias.romedi.terminology.UnknownRomediURI;

/**
 * Retrieve links to a Romedi URI
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
	 * The result of the SPARQL request
	 */
	private Result result;
	
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
			//RomediType.CIP13,
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
	
	/**
	 * Main function to call to search for a URI
	 * @param romediIRI {@link RomediIRI}
	 * @throws UnknownRomediURI the RomediURI is not found
	 */
	public void searchIRI(RomediIRI romediIRI) throws UnknownRomediURI{
		// first request : all CIS IRI linked to this IRI
		HashSet<RomediInstance> romediInstancesCIS = new HashSet<RomediInstance>();
		RomediInstance romediInstance = romediTerminology.getRomediInstance(romediIRI);
		if (romediInstance.getType().equals(RomediType.CIS)) {
			romediInstancesCIS.add(romediInstance);
		} else {
			HashSet<RomediIRI> uris = getCisIRI(romediInstance);
			for (RomediIRI uri : uris) {
				RomediInstance tempRomediInstance = romediTerminology.getRomediInstance(uri);
				romediInstancesCIS.add(tempRomediInstance);
			}
		}
		// second request : all nodes linked to CIS IRI
		setResult(romediInstancesCIS);
	}

	/********************************************** Getters : *******************************/
	/**
	 * Get the result of the SPARQL request
	 * @return The Result of the request
	 */
	public Result getResult() {
		return(result);
	}

	/**
	 * Retrieve all CIS IRI linked to a IRI by calling the first request
	 * @param romediType
	 * @param IRI
	 * @return
	 */
	private HashSet<RomediIRI> getCisIRI(RomediInstance romediInstance){
		HashSet<RomediIRI> cisIRI = new HashSet<RomediIRI>();
		String queryString = SparqlQueries.getInitialRequest(romediInstance);
		TupleQueryResult tupleResult = SendQuery.sendQuery(this.connection,queryString);
		while(tupleResult.hasNext()){
			BindingSet set = tupleResult.next();
			Value valueCIS = set.getValue(RomediType.CIS.toString());
			IRI iriCIS = getIRI(valueCIS);
			RomediIRI romediIRI = new RomediIRI(iriCIS.getLocalName());
			cisIRI.add(romediIRI);
		}
		tupleResult.close();
		logger.info("number of CIS found : " + cisIRI.size());
		return(cisIRI);
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
	
	public JSONObject getResultJson() {
		return(result.getJSONobject());
	}

	/**
	 * Retrieve the SPARQL result of the first request and extract all CIS IRI
	 * @param cisIRI
	 */
	private void setResult(HashSet<RomediInstance> romediInstanceCIS) {
		// clear previous request:
		String queryString = SparqlQueries.getCISRequest(romediInstanceCIS);
		logger.info("sending query....");
		TupleQueryResult tupleResult = SendQuery.sendQuery(this.connection,queryString);
		logger.info("tupleResult....");
		result = new Result(tupleResult, romediTerminology, outputType);
	}

//	// example : 
	public static void main(String[] args) throws FileNotFoundException, UnknownRomediType, UnknownRomediURI {
		DBconnection connection = new DBconnection(ConfigEndpoint.chosenEndpoint);
		Request request = new Request(connection);
		String uri = "ATCC09DA";
		RomediIRI romediIRI = new RomediIRI(uri);
		request.searchIRI(romediIRI);
		connection.close();
		// get JSONobject : 
		JSONObject jsonObject = request.getResultJson();
		System.out.println(jsonObject.toString());
		PrintWriter out = new PrintWriter("jsonOutputClomifene.json");
		out.println(jsonObject.toString());
		out.close();
	}
}
