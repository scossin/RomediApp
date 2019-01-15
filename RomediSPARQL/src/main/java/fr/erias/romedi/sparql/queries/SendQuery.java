package fr.erias.romedi.sparql.queries;

import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;

import fr.erias.romedi.sparql.connection.DBconnection;

/**
 * A static method to send a sparql Query to an endpoint
 * 
 * @author Cossin Sebastien
 *
 */
public class SendQuery {
	
	/**
	 * Send a query to an endpoint
	 * @param connection a connection to a SparqlEndpoint {@link DBconnection}
	 * @param query a sparqlQuery. See {@link SparqlQueries}
	 * @return a SparqlResult : {@link TupleQueryResult}
	 */
	public static TupleQueryResult sendQuery(DBconnection connection, String query){
		TupleQuery keywordQuery = connection.getDBcon().prepareTupleQuery(query);
		TupleQueryResult keywordQueryResult = keywordQuery.evaluate();
		return(keywordQueryResult);
	}
}
