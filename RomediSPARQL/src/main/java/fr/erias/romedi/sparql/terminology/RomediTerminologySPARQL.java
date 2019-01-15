package fr.erias.romedi.sparql.terminology;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.erias.romedi.sparql.connection.ConfigEndpoint;
import fr.erias.romedi.sparql.connection.DBconnection;
import fr.erias.romedi.sparql.queries.SendQuery;
import fr.erias.romedi.sparql.queries.SparqlQueries;
import fr.erias.romedi.terminology.GetRomediTerminology;
import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediType;

/**
 * Load a {@link RomediTerminology} with a SparqlEndpoint
 * 
 * @author Cossin Sebastien
 *
 */
public class RomediTerminologySPARQL implements GetRomediTerminology{

	final static Logger logger = LoggerFactory.getLogger(RomediTerminologySPARQL.class);

	/**
	 * The RomediType wanted
	 */
	private RomediType[] romediTypes;

	/**
	 * The terminology
	 */
	private RomediTerminology romediTerminology = new RomediTerminology();

	/**
	 * Retrieve a Romedi terminology
	 * @param sparqlEndpoint An url to connect to a SparqlEndpoint. See {@link ConfigEndpoint}
	 * @param romediTypes The RomediType wanted
	 */
	public RomediTerminologySPARQL(String sparqlEndpoint, RomediType[] romediTypes) {
		DBconnection connection = new DBconnection(sparqlEndpoint);
		this.romediTypes = romediTypes;
		setLabels(connection);
		setHiddenLabels(connection);
		setAltLabels(connection);
		connection.close();
	}

	/**
	 * Retrieve a Romedi terminology
	 * @param connection A connection to a SparqlEndpoint
	 * @param romediTypes The RomediType wanted
	 */
	public RomediTerminologySPARQL(DBconnection connection, RomediType[] romediTypes) {
		this.romediTypes = romediTypes;
		setLabels(connection);
		setHiddenLabels(connection);
		setAltLabels(connection);
	}

	@Override
	public RomediTerminology getRomediTerminology() {
		return this.romediTerminology;
	}

	/**
	 * Retrieve all hidden labels
	 * @param connection A connection to a SparqlEndpoint
	 */
	private void setHiddenLabels(DBconnection connection) {
		String sparqlQuery = SparqlQueries.getHiddenLabel(romediTypes);
		TupleQueryResult tupleQueryResult = SendQuery.sendQuery(connection, sparqlQuery);
		int counter = 0;
		while(tupleQueryResult.hasNext()){
			BindingSet set = tupleQueryResult.next();
			// CIS first : 
			Value value = set.getValue("instance");
			IRI iri = getIRI(value);
			String localName = iri.getLocalName();
			value = set.getValue("hiddenLabel");
			String hiddenLabel = value.stringValue();
			RomediIRI romediIRI = new RomediIRI(localName);
			if (!romediTerminology.existsURI(romediIRI)) {
				logger.error("romedi IRI not found with a hidden label !. Does this iri has a rdfs:label?: " + romediIRI.getIRIstring());
			}
			romediTerminology.getMapURI2instance().get(romediIRI).getHiddenLabels().add(hiddenLabel);
			counter += 1;
		}
		logger.info("number of hidden labels added:" + counter);
		tupleQueryResult.close();
	}

	/**
	 * Retrieve all alternative labels
	 * @param connection A connection to a SparqlEndpoint
	 */
	private void setAltLabels(DBconnection connection) {
		String sparqlQuery = SparqlQueries.getAltLabel(romediTypes);
		TupleQueryResult tupleQueryResult = SendQuery.sendQuery(connection, sparqlQuery);
		int counter = 0 ;
		while(tupleQueryResult.hasNext()){
			BindingSet set = tupleQueryResult.next();
			// CIS first : 
			Value value = set.getValue("instance");
			IRI iri = getIRI(value);
			String localName = iri.getLocalName();
			value = set.getValue("altLabel");
			String hiddenLabel = value.stringValue();
			RomediIRI romediIRI = new RomediIRI(localName);
			if (!romediTerminology.existsURI(romediIRI)) {
				logger.error("romedi IRI not found with a hidden label !. Does this iri has a rdfs:label?: " + romediIRI.getIRIstring());
			}
			romediTerminology.getMapURI2instance().get(romediIRI).getAltLabels().add(hiddenLabel);
			counter  += 1;
		}
		logger.info("number of alt labels added:" + counter);
		tupleQueryResult.close();
	}

	/**
	 * Retrieve all labels
	 * @param connection A connection to a SparqlEndpoint
	 */
	private void setLabels(DBconnection connection){
		String sparqlQuery = SparqlQueries.getQueryLabels(romediTypes);
		TupleQueryResult tupleQueryResult = SendQuery.sendQuery(connection, sparqlQuery);
		// clear : 
		int i = 0;
		while(tupleQueryResult.hasNext()){
			BindingSet set = tupleQueryResult.next();
			// CIS first : 
			Value value = set.getValue("instance");
			IRI iri = getIRI(value);
			String localName = iri.getLocalName();
			value = set.getValue("type");
			String typename = getIRI(value).getLocalName();
			value = set.getValue("label");
			String label = value.stringValue();
			RomediType romediType = null;
			try {
				romediType = RomediType.getRomediType(typename);
			} catch (UnknownRomediType e) {
				logger.error("Unknown RomediType:" + typename);
				logger.error(e.getLocalizedMessage());
				romediType = RomediType.UNKNOWN;
			}
			RomediIRI romediIRI = new RomediIRI(localName);
			RomediInstance romediInstance = new RomediInstance(romediIRI, romediType, label);
			romediTerminology.getMapURI2instance().put(romediIRI, romediInstance);
			i = i + 1;
			// break;
		}
		tupleQueryResult.close();
		logger.info("number of labels added:" + i);
	}

	private IRI getIRI(Value value) {
		IRI iri = null; 
		try{
			iri = (IRI) value;
		} catch (ClassCastException e){
			logger.info("error");
		}
		return(iri);
	}

	public static void main(String[] args) throws UnknownRomediType {
		RomediType[] romediTypes = {RomediType.PIN,RomediType.BN, RomediType.IN};
		RomediTerminologySPARQL romediTerminologySPARQL = new RomediTerminologySPARQL(ConfigEndpoint.chosenEndpoint, romediTypes);
		System.out.println(romediTerminologySPARQL.getRomediTerminology().getMapURI2instance().size());
	}
}
