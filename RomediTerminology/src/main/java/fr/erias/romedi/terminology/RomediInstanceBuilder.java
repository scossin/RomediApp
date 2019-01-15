package fr.erias.romedi.terminology;

public class RomediInstanceBuilder {
	
	public static RomediInstance createCIS(String cisLabel) {
		RomediIRI romediIRI = new RomediIRI(getCISIRI(cisLabel));
		RomediInstance cis = new RomediInstance(romediIRI, RomediType.CIS,getCIScisLabel(cisLabel));
		return(cis);
	}
	
	private static String getCISIRI(String cisLabel) {
		if (cisLabel.startsWith("CIS")) {
			return(cisLabel);
		}
		return("CIS"+cisLabel);
	}
	
	private static String getCIScisLabel(String cisLabel) {
		if (cisLabel.startsWith("CIS")) {
			return(cisLabel.replace("CIS",""));
		}
		return(cisLabel);
	}

	public static RomediInstance createInstance(String label, RomediType romediType) {
		String hashLabel = HashName.hashName(label,romediType);
		RomediIRI romediIRI = new RomediIRI(hashLabel);
		RomediInstance romediInstance = new RomediInstance(romediIRI, romediType, label);
		return romediInstance;
	}
}
