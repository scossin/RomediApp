package fr.erias.romedi.sparql.connection;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to establish a connection to the SparqlEndpoint
 * 
 * @author Sebastien Cossin
 *
 */

public class DBconnection {

	final static Logger logger = LoggerFactory.getLogger(DBconnection.class);
	
	/**
	 * A SPARQLRepository
	 */
	private Repository repo ;
	
	/**
	 * A RepositoryConnection to the SPARQLRepository
	 */
	private RepositoryConnection DBcon;
	
	/**
	 * The sparqlEndpoint URL
	 */
	private final String sparqlEndpoint ;
	
	/**
	 * Establish a new connection to the sparqlEndpoint Repository
	 * @param sparlqEndpoint The sparlqEndpoint URL
	 */
	public DBconnection (String sparlqEndpoint) throws RepositoryException{
		this.sparqlEndpoint = sparlqEndpoint;
		repo = new SPARQLRepository(sparqlEndpoint);
		repo.initialize();
		try{
			DBcon = repo.getConnection();
		} catch (RepositoryException e){
			System.out.println("Fail to connect to " + sparqlEndpoint);
			throw e;
		}
	}
	
	/**
	 * 
	 * @return The RepositoryConnection to the SPARQLRepository
	 */
	public RepositoryConnection getDBcon(){
		return(DBcon);
	}
	
	/**
	 * Close the connection to the SPARQLRepository and shutDown the initialized SPARQLRepository
	 */
	public void close(){
		DBcon.close();
		repo.shutDown();
	}
	

	// test connection to DB
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBconnection con = new DBconnection(ConfigEndpoint.chosenEndpoint);
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
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			con.close();
		}
	}
}
