package fr.erias.romedi.terminology;

/**
 * Available links
 * 
 * @author Cossin Sebastien
 *
 */
public enum RomediLinks {
	CIShasCIP13,
	CIShasBNdosage,
	BNdosagehasBN,
	
	CIShasPINdosage,
	PINdosagehasINdosage,
	PINdosagehasPIN,
	INdosagehasIN,
	CIShasATC7,
	CIShasATC5,
	CIShasATC4; 
	
	public RomediIRI getRomediIRI() {
		RomediIRI romediIRI = new RomediIRI(this.toString());
		return(romediIRI);
	}
}
