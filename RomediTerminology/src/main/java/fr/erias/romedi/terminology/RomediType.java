package fr.erias.romedi.terminology;


/**
 * Enumeration of Romedi types
 * 
 * @author Cossin Sebastien
 *
 */
public enum RomediType {
		CIS,
		BN,
		IN,
		PIN,
		PINdosage,
		INdosage,
		BNdosage,
		CIP13,
		ATC7, 
		ATC5, 
		ATC4,
		ATC,
		Reference,
		CUI,
		MOLECULE,
		Dosage,
		UCD13,
		UNKNOWN; // case of an error
	
	/**
	 * List of RomediType one2one with a CIS
	 * Relation with a CIS (drug): one2one or one to many
	 * Ex : a CIS (drug) can have multiple ingredients but only one ATC code or one Brand Name
	 */
	public static RomediType[] one2one = {RomediType.BN, RomediType.BNdosage,
			RomediType.CIS, RomediType.ATC7, RomediType.ATC5, RomediType.ATC4}; 
	
	/**
	 * List of RomediType one2many with a CIS
	 * Relation with a CIS (drug): one2one or one to many
	 * Ex : a CIS (drug) can have multiple ingredients but only one ATC code or one Brand Name
	 */
	public static RomediType[] one2many = {RomediType.PINdosage, RomediType.INdosage,
			RomediType.IN, RomediType.PIN}; 
	
	/**
	 * Relation with a CIS (drug): one2one or one to many
	 * Ex : a CIS (drug) can have multiple ingredients but only one ATC code or one Brand Name
	 * @param type A romedi type
	 * @return true if the relation is one2one
	 */
	public static boolean isOne2oneCIS(RomediType type) {
		for (int i = 0; i<one2many.length; i++) {
			if (type == one2many[i]) {
				//System.out.println("merged type : " + type );
				return(true);
			}
		}
		return(false);
	}
	
	/**
	 * Retrieve a {@link RomediType} by its name
	 * @param typeName the name of a romediType (ex : BN, IN)
	 * @return a {@link RomediType} 
	 * @throws UnknownRomediType This RomediType is unknown
	 */
	public static RomediType getRomediType(String typeName) throws UnknownRomediType {
		for (RomediType type : RomediType.values()) {
			if (type.toString().equals(typeName)) {
				return(type);
			}
		}
		throw new UnknownRomediType(typeName);
	}
	
	public RomediIRI getRomediIRI() {
		RomediIRI romediIRI = new RomediIRI(this.toString());
		return(romediIRI);
	}
}
