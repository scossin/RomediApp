package fr.erias.romedi.sparql.connection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.romedi.sparql.queries.SendQuery;
import fr.erias.romedi.sparql.queries.SparqlQueries;

/**
 * Descriptive analysis of the content
 * 
 * @author Cossin Sebastien
 *
 */
public class DescribeContent {

	final static Logger logger = LoggerFactory.getLogger(DescribeContent.class);
	
	/**
	 * Connection to SPARQL endpoint
	 */
	private DBconnection connection = null;
	
	public DescribeContent(DBconnection connection) {
		this.connection = connection;
	}
	
	public void getNumberOfType() {
		logger.info("****************************** Number of instances by types ************************");
		TupleQueryResult tupleResult = SendQuery.sendQuery(connection,SparqlQueries.countType);
		while(tupleResult.hasNext()){
			BindingSet set = tupleResult.next();
			// CIS first : 
			Value value = set.getValue("type");
			IRI type = (IRI) value;
			value = set.getValue("total");
			int total = Integer.parseInt(value.stringValue());
			logger.info(type.getLocalName() + " ----> " + total);
		}
	}
	
	public void getNumberOfLinkedIN() {
		logger.info("****************************** Number of isolated IN ************************");
		TupleQueryResult tupleResult = SendQuery.sendQuery(connection,SparqlQueries.countIN);
		while(tupleResult.hasNext()){
			BindingSet set = tupleResult.next();
			// CIS first : 
			Value value = set.getValue("total");
			int total = Integer.parseInt(value.stringValue());
			logger.info("Number of IN linked to a INdosage ----> " + total);
		}
		tupleResult.close();
	}
	
	public void getInstanceNoLabel() {
		logger.info("****************************** Number of instances with no labels by classes ************************");
		TupleQueryResult tupleResult = SendQuery.sendQuery(connection,SparqlQueries.countNoLabel);
		while(tupleResult.hasNext()){
			BindingSet set = tupleResult.next();
			Value value = set.getValue("type");
			IRI type = (IRI) value;
			// CIS first : 
			value = set.getValue("total");
			int total = Integer.parseInt(value.stringValue());
			logger.info(type.getLocalName() + " ----> " + total);
		}
		tupleResult.close();
	}
	
	public void createURIterms() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./src/main/resources/URIterms.csv")));
		logger.info("****************************** createURIterms.csv ************************");
		String queryString = "PREFIX romedi:<http://www.romedi.fr/romedi/>\n" + 
				"SELECT distinct ?uri ?type ?label where {    \n" + 
				"   ?uri  a ?type ; \n" + 
				"  rdfs:label ?label . \n" + 
				"}\n";
		System.out.println(queryString);
		TupleQueryResult tupleResult = SendQuery.sendQuery(connection,queryString);
		while(tupleResult.hasNext()){
			BindingSet set = tupleResult.next();
			Value value = set.getValue("uri");
			IRI iri = (IRI) value;
			String uri = iri.stringValue();
			value = set.getValue("type");
			IRI type = (IRI) value;
			value = set.getValue("label");
			writer.append(uri + "\t" + type.getLocalName() + "\t" + value.stringValue() + "\n");
		}
		tupleResult.close();
		writer.close();
	}
	
	
	public static void main(String[] args) throws IOException {
		DBconnection connection = new DBconnection(ConfigEndpoint.chosenEndpoint);
		DescribeContent describeContent = new DescribeContent(connection);
		describeContent.getNumberOfType();
		describeContent.getNumberOfLinkedIN();
		describeContent.getInstanceNoLabel();
		describeContent.connection.close();
	}
}
