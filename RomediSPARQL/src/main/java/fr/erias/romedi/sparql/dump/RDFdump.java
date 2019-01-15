package fr.erias.romedi.sparql.dump;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

import fr.erias.romedi.sparql.connection.ConfigEndpoint;
import fr.erias.romedi.sparql.connection.DBconnection;

/**
 * Dump the contents of the repository to an RDF file
 * 
 * @author Cossin Sebastien
 *
 */
public class RDFdump {
	
	public final String version = "2-1-0";
	
	public final RDFFormat rdfFormat = RDFFormat.TURTLE;
	
	/**
	 * Name of the file containing all the data:
	 */
	public final String fullFileName = "Romedi";
	
	/**
	 * Dump all the data of Romedi terminology to a RDF file
	 * @param connection A connection to the Romedi terminology
	 * @throws IOException Can't write to the file
	 */
	public void dumpAllData(DBconnection connection) throws IOException {
		String fileName = getFileName(this.fullFileName);
		File outputFile = new File(fileName);
		FileWriter fileWriter = new FileWriter(outputFile);
		RDFWriter turtleWriter = Rio.createWriter(rdfFormat, fileWriter);
		connection.getDBcon().prepareGraphQuery(QueryLanguage.SPARQL,
		   "CONSTRUCT {?s ?p ?o } WHERE {?s ?p ?o } ").evaluate(turtleWriter);
	}
	
	/**
	 * 
	 * @param name The name of the file without extension 
	 * @return The name of the file of the current version and the extension 
	 */
	private String getFileName(String name) {
		return(name + version + "." + rdfFormat.getDefaultFileExtension());
	}
	
	public static void main(String[] args) throws IOException {
		DBconnection connection = new DBconnection(ConfigEndpoint.chosenEndpoint);
		new RDFdump().dumpAllData(connection);
		connection.close();
	}
}
