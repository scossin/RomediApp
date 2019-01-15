package fr.erias.romedi.terminology;

public class UnknownRomediURI extends Exception {
	public UnknownRomediURI(String typeName) {
		super(new Exception(typeName));
	}
}
