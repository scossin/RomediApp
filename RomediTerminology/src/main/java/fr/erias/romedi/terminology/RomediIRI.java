package fr.erias.romedi.terminology;

import java.util.HashSet;
import java.util.Objects;

/**
 * Romedi URI of an instance
 * 
 * @author Cossin Sebastien
 *
 */
public class RomediIRI {

	public static final String namespace = "http://www.romedi.fr/romedi/";
	
	/**
	 * The IRI string : http://www.romedi.fr/romedi/BNqojc85n788lv66jj23g3jfvcdhqkrogn
	 */
	private String iriString;
	
	/**
	 * The localname : BNqojc85n788lv66jj23g3jfvcdhqkrogn
	 */
	private String localName;

	/**
	 * Creates a RomediIRI
	 * @param iriString a full URI (http://www.romedi.fr/romedi/BNqojc85n788lv66jj23g3jfvcdhqkrogn) or just a localName (BNqojc85n788lv66jj23g3jfvcdhqkrogn)
	 */
	public RomediIRI(String iriString) {
		setIRIString(iriString);
	}

	/**
	 * Set the iriString
	 * @param iriString a full URI (http://www.romedi.fr/romedi/BNqojc85n788lv66jj23g3jfvcdhqkrogn) or just a localName (BNqojc85n788lv66jj23g3jfvcdhqkrogn)
	 */
	public void setIRIString(String iriString) {
		Objects.requireNonNull(iriString, "iriString must not be null");
		if(iriString.startsWith(namespace)) {
			this.iriString = iriString;
			return;
		}
		this.iriString = namespace + iriString;
	}

	@Override
	public String toString() {
		return iriString;
	}

	/**
	 * 
	 * @return A string value of the URI (IRI)
	 */
	public String stringValue() {
		return iriString;
	}
	
	/**
	 * 
	 * @return A string value of the URI (IRI)
	 */
	public String getIRIstring() {
		return iriString;
	}

	/**
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	public String getLocalName() {
		return localName;
	}

	@Override
	public int hashCode() {
		return iriString.hashCode();
	}
	
	public String getIRI4query() {
		return("<"+iriString+">");
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof RomediIRI) {
			return toString().equals(o.toString());
		}
		return false;
	}
	
	public static void main(String[] args) {
		RomediIRI romediIRI = new RomediIRI("BNqojc85n788lv66jj23g3jfvcdhqkrogn");
		System.out.println(romediIRI.getIRIstring());
		System.out.println(romediIRI.hashCode());
		RomediIRI romediIRI2 = new RomediIRI("BNqojc85n788lv66jj23g3jfvcdhqkrogn");
		System.out.println(romediIRI2.hashCode());
		HashSet<RomediIRI> setIRIs = new HashSet<RomediIRI>();
		setIRIs.add(romediIRI);
		System.out.println(romediIRI.equals(romediIRI2));
		System.out.println(setIRIs.contains(romediIRI2));
	}
}