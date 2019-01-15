package fr.erias.romedi.terminology;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A class that represents the Romedi terminology
 * 
 * @author Cossin Sebastien
 *
 */
public class RomediTerminology {
	
	/**
	 * link between a URI and RomediInstance 
	 */
	private HashMap<RomediIRI, RomediInstance> mapURI2instance = new HashMap<RomediIRI,RomediInstance>(); 
	
	/**
	 * Check if a romedi URI exists
	 * @param romediIRI a {@link RomediIRI}
	 * @return true if the URI exists
	 */
	public boolean existsURI(RomediIRI romediIRI) {
		return (mapURI2instance.containsKey(romediIRI));
	}
	
	/**
	 * Get the {@link RomediInstance} of a known uri
	 * @param romediIRI a {@link RomediIRI}
	 * @return the {@link RomediInstance}
	 * @throws UnknownRomediURI the URI is not found
	 */
	public RomediInstance getRomediInstance(RomediIRI romediIRI) throws UnknownRomediURI {
		if (!mapURI2instance.containsKey(romediIRI)) {
			throw new UnknownRomediURI(romediIRI.getIRIstring());
		}
		return (mapURI2instance.get(romediIRI));
	}
	
	/**
	 * 
	 * @return A map between a URI and a RomediInstance
	 */
	public HashMap<RomediIRI,RomediInstance> getMapURI2instance(){
		return(mapURI2instance);
	}
	
	/**
	 * Write the terminology to a CSV file
	 * @param csvOutputFile The CSV outputFile
	 * @param sep the separator
	 * @param header true if we want an header in the file
	 * @throws IOException Not possible to write to the File
	 */
	public void toCSV(File csvOutputFile, String sep, boolean header) throws IOException {
		csvOutputFile.createNewFile();
		StringBuilder sb = new StringBuilder();
		if (header) {
			sb.append("uri");
			sb.append(sep);
			sb.append("type");
			sb.append(sep);
			sb.append("label");
			sb.append(sep);
			sb.append("normalizedLabel");
			sb.append("\n");
			writeLine(sb.toString(),csvOutputFile);
		}
		System.out.println(getMapURI2instance().size() + "number of instances");
		for (RomediInstance romediInstance : getMapURI2instance().values()) {
			sb = new StringBuilder();
			sb.append(romediInstance.getIRI());
			sb.append(sep);
			sb.append(romediInstance.getType());
			sb.append(sep);
			sb.append(romediInstance.getPrefLabel());
			sb.append(sep);
			sb.append(romediInstance.getNormalizedLabel());
			sb.append("\n");
			writeLine(sb.toString(),csvOutputFile);
		}
	}
	
	/**
	 * When a drugname is ambiguous, we exclude drugs by IRI.
	 * Remove a set of {@link RomediIRI}
	 * @param romediIRIs
	 */
	public void removeURI(HashSet<RomediIRI> romediIRIs) {
		for (RomediIRI romediIRI : romediIRIs) {
				this.mapURI2instance.remove(romediIRI);
		}
	}
	
	/**
	 * Write the output to a file
	 * @param newLine A newline to append
	 * @throws IOException Not possible to write to the File
	 */
	private void writeLine(String newLine, File outputFile) throws IOException {
		 Files.write(Paths.get(outputFile.getAbsolutePath()), newLine.getBytes(), StandardOpenOption.APPEND);
	}
}
