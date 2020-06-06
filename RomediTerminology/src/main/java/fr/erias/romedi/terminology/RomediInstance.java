package fr.erias.romedi.terminology;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The representation of a Romedi instance
 * 
 * @author Cossin Sebastien
 *
 */
public class RomediInstance implements Comparable<RomediInstance> {

	/**
	 * Alternative labels ; ex : acide ascorbique = vitamine c
	 */
	protected HashSet<String> altLabels = new HashSet<String>();
	
	/**
	 * Hidden labels ; ex : typos
	 */
	protected HashSet<String> hiddenLabels = new HashSet<String>();

	/**
	 * Escitalopram
	 */
	protected String prefLabel;

	/**
	 * escitalopram
	 */
	protected String normalizedLabel;

	/**
	 * BN, IN...
	 */
	protected RomediType romediType;

	/**
	 * substance200204
	 */
	protected RomediIRI romediIRI;

	/**
	 * Create a representation of a romedi instance 
	 * @param romediIRI a {@link RomediIRI}
	 * @param romediType the {@link RomediType} of the uri (brand name, ingredient...)
	 * @param prefLabel the pref label of this URI
	 */
	public RomediInstance(RomediIRI romediIRI, RomediType romediType, String prefLabel) {
		this.setPrefLabel(prefLabel);
		this.setRomediType(romediType);
		this.setRomediIRI(romediIRI);
	}

	/**
	 * Create a representation of a romedi instance 
	 * @param romediIRI a {@link RomediIRI}
	 * @param romediType the {@link RomediType} of the uri (brand name, ingredient...)
	 * @param prefLabel the pref label of this URI
	 * @param normalizedLabel the label normalized
	 */
	public RomediInstance(RomediIRI romediIRI, RomediType romediType, String prefLabel, String normalizedLabel) {
		this.setPrefLabel(prefLabel);
		this.setRomediType(romediType);
		this.setRomediIRI(romediIRI);
		this.setNormalizedLabel(normalizedLabel);
	}

	/**
	 * 
	 * @return the label of a term
	 */
	public String getPrefLabel() {
		return prefLabel;
	}

	/**
	 * Set the pref label
	 * @param label the pref label
	 */
	public void setPrefLabel(String label) {
		this.prefLabel = label;
	}

	/**
	 * 
	 * @return the type (BN, IN...)
	 */
	public RomediType getType() {
		return romediType;
	}

	public void setRomediType(RomediType romediType) {
		this.romediType = romediType;
	}

	/**
	 * 
	 * @return the uri : http://www.romedi.fr/romedi/substance200204
	 */
	public RomediIRI getRomediIRI() {
		return romediIRI;
	}

	public void setRomediIRI(RomediIRI romediIRI) {
		this.romediIRI = romediIRI;
	}

	/**
	 * 
	 * @return the label normalized
	 */
	public String getNormalizedLabel() {
		return normalizedLabel;
	}

	/**
	 * 
	 * @param normalizedLabel a normalized label
	 */
	public void setNormalizedLabel(String normalizedLabel) {
		this.normalizedLabel = normalizedLabel;
	}

	/**
	 * Retrieve the alternative labels 
	 * @return
	 */
	public HashSet<String> getAltLabels() {
		return altLabels;
	}
	
	/**
	 * Retrieve the alternative labels in a JSONArray
	 * @return
	 */
	public JSONArray getAltLabelsArrayJson() {
		JSONArray jsonArray = new JSONArray();
		for (String altLabel : altLabels) {
			jsonArray.put(altLabel);
		}
		return(jsonArray);
	}

	/**
	 * Add an alternative label
	 * @param altLabel an alternative label
	 */
	public void addAltLabel(String altLabel) {
		this.altLabels.add(altLabel);
	}

	/**
	 * Add alternative labels
	 * @param altLabels A set of Alternative labels
	 */
	public void setAltLabels(HashSet<String> altLabels) {
		this.altLabels = altLabels;
	}

	/**
	 * 
	 * @return A JsonRepresentation of the romediInstance
	 */
	public JSONObject getJSONObject() {
		String label = this.prefLabel;
		if (this.romediType.equals(RomediType.BN) || this.romediType.equals(RomediType.BNdosage)) {
		} else {
			label = label.toLowerCase();
		}
		JSONObject outputObject = new JSONObject();
		outputObject.put("localName", romediIRI.getLocalName());
		outputObject.put("uri", romediIRI.getIRIstring());
		outputObject.put("label", label);
		outputObject.put("altLabels", this.getAltLabelsArrayJson());
		outputObject.put("type", romediType.toString());
		return(outputObject);
	}
	
	public String getIRI() {
		return(this.romediIRI.getIRIstring());
	}

	@Override
	public int compareTo(RomediInstance o) {
		return(this.romediIRI.getIRIstring().compareTo(o.romediIRI.getIRIstring()));
	}

	public HashSet<String> getHiddenLabels() {
		return hiddenLabels;
	}

	public void setHiddenLabels(HashSet<String> hiddenLabels) {
		this.hiddenLabels = hiddenLabels;
	}
}
