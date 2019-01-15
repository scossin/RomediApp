package fr.erias.romedi.terminology;

public class UnknownRomediType extends Exception {
	public UnknownRomediType(String typeName) {
		super(new Exception(typeName));
	}
}
