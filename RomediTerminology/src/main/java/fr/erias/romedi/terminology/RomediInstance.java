package fr.erias.romedi.terminology;

import java.util.HashSet;

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
	private HashSet<String> altLabels = new HashSet<String>();
	
	/**
	 * Hidden labels ; ex : typos
	 */
	private HashSet<String> hiddenLabels = new HashSet<String>();

	/**
	 * Escitalopram
	 */
	private String prefLabel;

	/**
	 * escitalopram
	 */
	private String normalizedLabel;

	/**
	 * BN, IN...
	 */
	private RomediType romediType;

	/**
	 * substance200204
	 */
	private RomediIRI romediIRI;

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

	public HashSet<String> getAltLabels() {
		return altLabels;
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
