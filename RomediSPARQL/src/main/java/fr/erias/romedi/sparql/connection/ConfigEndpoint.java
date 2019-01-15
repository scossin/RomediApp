package fr.erias.romedi.sparql.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tests different connection : 1) a blazegraph docker ; 2) a localhost blazegraph ; 3) romedi sparql endpoint
 * We need to connect to a Romedi triplestore to retrieve the terminology <br>
 * 
 * @author Cossin Sebastien
 *
 */

public class ConfigEndpoint {
	
	final static Logger logger = LoggerFactory.getLogger(ConfigEndpoint.class);
	
	public static final String romediEndpoint = "http://www.romedi.fr:8890/sparql";
	
	public static final String dockerEndpoint = "http://blazegraphromedi:8080/bigdata/namespace/ROMEDI/sparql";
	
	public static final String localhostEndpoint = "http://127.0.0.1:8889/bigdata/namespace/ROMEDI/sparql";
	
	public static String chosenEndpoint;
	
	static {
		setDockerEndpoint();
		if (chosenEndpoint == null) {
			localhostEndpoint();
		}
		
		if (chosenEndpoint == null) {
			romediEndpoint();
		}
		
		if (chosenEndpoint == null) {
			throw new NullPointerException("Can't connect to a Romedi Sparql Endpoint");
		}
	}
	
	private static void setDockerEndpoint() {
		logger.info("Checking the existence of a blazegraphromedi docker container...");
		// case we are inside a the Romedi docker-compose environment ; 
		String hostname = "blazegraphromedi";
		boolean reachable = false;
		try {
			reachable = InetAddress.getByName(hostname).isReachable(1000);
		} catch (IOException e) {
			logger.info("fail to connect to a docker container");
		}
		if (reachable) {
			chosenEndpoint = ConfigEndpoint.dockerEndpoint;
		}
	}
	
	private static void localhostEndpoint() {
		logger.info("Checking the existence of a blazegraph application at port 8889");
		boolean reachable = false;
		reachable = pingHost("localhost",8889,1000);
		if (reachable) {
			chosenEndpoint = ConfigEndpoint.localhostEndpoint;
		} else {
			logger.info("Can't find a localhost blazegraph application at port 8889");
		}
	}
	
	private static void romediEndpoint() {
		logger.info("Checking a connection to www.romedi.fr");
		boolean reachable = false;
		try {
			reachable = InetAddress.getByName("www.romedi.fr").isReachable(1000);
		} catch (IOException e) {
			logger.info("fail to connect to a www.romedi.fr");
		}
		if (reachable) {
			chosenEndpoint = ConfigEndpoint.romediEndpoint;
		} else {
			logger.info("fail to connect to a www.romedi.fr");
		}
	}
	
	private static boolean pingHost(String host, int port, int timeout) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; // Either timeout or unreachable or failed DNS lookup.
	    }
	}
}
