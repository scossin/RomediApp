package fr.erias.romedi.terminology;

import org.json.JSONObject;

public class RomediInstanceCIS extends RomediInstance {

	private boolean isCommercialized = false;
	
	public RomediInstanceCIS(RomediIRI romediIRI, RomediType romediType, String prefLabel) {
		super(romediIRI, romediType, prefLabel);
	}
	
	public void setIsCommercialized (boolean isCommercialized) {
		this.isCommercialized = isCommercialized;
	}
	
	public JSONObject getJSONObject() {
		String label = this.prefLabel;
		JSONObject outputObject = new JSONObject();
		outputObject.put("localName", romediIRI.getLocalName());
		outputObject.put("uri", romediIRI.getIRIstring());
		outputObject.put("label", label);
		outputObject.put("altLabels", this.getAltLabelsArrayJson());
		outputObject.put("type", romediType.toString());
		outputObject.put("isCommercialized", this.isCommercialized);
		return(outputObject);
	}
}
