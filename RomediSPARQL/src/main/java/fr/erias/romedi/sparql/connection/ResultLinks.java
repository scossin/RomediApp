package fr.erias.romedi.sparql.connection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.erias.romedi.terminology.RomediIRI;
import fr.erias.romedi.terminology.RomediInstance;
import fr.erias.romedi.terminology.RomediInstanceCIS;
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediURI;

/**
 * Handle the result of {@link Request}
 * 
 * @author Cossin Sebastien
 *
 */
public class ResultLinks {

	final static Logger logger = LoggerFactory.getLogger(ResultLinks.class);

	/**
	 * link a Romedi CIS URI to all its links (other Romedi instances)
	 */
	private HashMap<RomediType, HashSet<RomediInstance>> mapTypeToInstances = new HashMap<RomediType, HashSet<RomediInstance>>();

	/**
	 * 
	 */
	private RomediInstanceCIS romediInstanceCIS ;

	/**
	 * new ResultLinks object to retrieve all linked instances of a CIS
	 * @param romediInstanceCIS a RomediCIS instance
	 */
	public ResultLinks(RomediInstanceCIS romediInstanceCIS, RomediType[] outputType) {
		this.romediInstanceCIS = romediInstanceCIS;
		// initialize all links by romediType
		for (RomediType romediType : outputType) {
			HashSet<RomediInstance> romediInstances = new HashSet<RomediInstance>();
			mapTypeToInstances.put(romediType, romediInstances);
		}
	}

	/**
	 * Add row result of a SPARQLquery to this CIS
	 * @param set a new BindingSet of a SPARQLquery result (a row)
	 * @param romediTerminology {@link RomediTerminology} to retrieve Romedi Instances
	 * @param outputType an array of RomediTypes (must be in the SPARQL query) 
	 */
	public void addSet(BindingSet set, RomediTerminology romediTerminology, RomediType[] outputType) {
		RomediInstance romediInstance = null;
		// RomediInstance CIS first : 
		for (RomediType romediType : outputType) {
			Value value = set.getValue(romediType.toString());
			IRI iri = getIRI(value);
			RomediIRI romediIRI2 = new RomediIRI(iri.getLocalName());
			try {
				romediInstance = romediTerminology.getRomediInstance(romediIRI2);
			} catch (UnknownRomediURI e) {
				logger.error(e.getMessage());
				continue;
			}
			mapTypeToInstances.get(romediType).add(romediInstance);
		}
	}

	public JSONObject getJSONobject() {
		JSONObject json = romediInstanceCIS.getJSONObject();
		Iterator<RomediType> iter = this.mapTypeToInstances.keySet().iterator();
		JSONObject instances = new JSONObject();
		while (iter.hasNext()) {
			RomediType romediType = iter.next();
			JSONArray jsonArray = new JSONArray();
			for (RomediInstance romediInstance : this.mapTypeToInstances.get(romediType)) {
				jsonArray.put(romediInstance.getJSONObject());
			}
			instances.put(romediType.toString(), jsonArray);
		}
		json.put("links", instances);
		return(json);
	}

	/**
	 * Get the IRI from a Value object by down casting the Value object
	 * @param value
	 * @return
	 */
	private IRI getIRI(Value value) {
		IRI iri = null; 
		try {
			iri = (IRI) value;
		} catch (ClassCastException e){
			logger.info("error");
		}
		return(iri);
	}
}
