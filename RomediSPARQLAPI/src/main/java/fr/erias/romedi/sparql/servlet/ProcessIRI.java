package fr.erias.romedi.sparql.servlet;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.romedi.sparql.connection.ConfigEndpoint;
import fr.erias.romedi.sparql.connection.DBconnection;
import fr.erias.romedi.sparql.connection.Request;
import fr.erias.romedi.terminology.UnknownRomediType;

/**
 * The class that handle the HTTP client requests
 * 
 * @author Cossin Sebastien
 *
 */
public class ProcessIRI {
	final static Logger logger = LoggerFactory.getLogger(ProcessIRI.class);

	public static Request request;

	static {
		logger.info("Initializing the connection to the database");
		DBconnection connection = new DBconnection(ConfigEndpoint.chosenEndpoint);
		try {
			ProcessIRI.request = new Request(connection);
		} catch (UnknownRomediType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("checking the connection...");
		checkConnection(connection);
	}

	public static void checkConnection(DBconnection con) {
		try {
			TupleQuery keywordQuery = con.getDBcon().prepareTupleQuery("SELECT * WHERE {?s ?p ?o} LIMIT 1");
			TupleQueryResult keywordQueryResult = keywordQuery.evaluate();
			if (!keywordQueryResult.hasNext()){
				logger.info("Repository is empty");
			}
			while(keywordQueryResult.hasNext()){
				logger.info("Printing one statement :");
				BindingSet set = keywordQueryResult.next();
				logger.info(set.toString());
				break;
			}
			keywordQueryResult.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}