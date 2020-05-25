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
import fr.erias.romedi.terminology.RomediTerminology;
import fr.erias.romedi.terminology.RomediType;
import fr.erias.romedi.terminology.UnknownRomediURI;

/**
 * Handle the result of {@link Request}
 * 
 * @author Cossin Sebastien
 *
 */
public class Result {

	final static Logger logger = LoggerFactory.getLogger(Result.class);

	private JSONObject jsonOutput = new JSONObject();

	/**
	 * Main function : retrieve the JSON 
	 * @return the JSONObject with all the links
	 */
	public JSONObject getJSONobject() {
		return jsonOutput;
	}
	
	public Result(TupleQueryResult tupleResult, RomediTerminology romediTerminology, RomediType[] outputType) {
		// Map a RomediInstance to its RomediInstanceCIS
		HashMap<RomediInstance, HashSet<RomediInstance>> mapRomediInstance2CIS = new HashMap<RomediInstance, HashSet<RomediInstance>>();
		
		// set of romediInstances in the SPARQL result
		HashSet<RomediInstance> romediInstances = new HashSet<RomediInstance>();
		
		RomediInstance romediInstance = null;
		while(tupleResult.hasNext()){
			BindingSet set = tupleResult.next();
			// RomediInstance CIS first : 
			Value valueCIS = set.getValue(RomediType.CIS.toString());
			IRI iriCIS = getIRI(valueCIS);
			RomediInstance romediInstanceCIS = null;
			RomediIRI romediIRI = new RomediIRI(iriCIS.getLocalName());
			try {
				romediInstanceCIS = romediTerminology.getRomediInstance(romediIRI);
			} catch (UnknownRomediURI e1) {
				logger.error(e1.getMessage());
				continue;
			}

			// others instances : 
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
				romediInstances.add(romediInstance);
				
				// save the couple romediInstance to romediInstanceCIS
				if (mapRomediInstance2CIS.containsKey(romediInstance)) {
					mapRomediInstance2CIS.get(romediInstance).add(romediInstanceCIS);
				} else {
					HashSet<RomediInstance> tempRomediInstances = new HashSet<RomediInstance>();
					tempRomediInstances.add(romediInstanceCIS);
					mapRomediInstance2CIS.put(romediInstance, tempRomediInstances);
				}
			}
		}
		tupleResult.close();
		setJsonObject(mapRomediInstance2CIS, romediInstances, outputType);
	}
	
	private void setJsonObject(HashMap<RomediInstance, HashSet<RomediInstance>> mapRomediInstance2CIS,
			HashSet<RomediInstance> romediInstances,
			RomediType[] outputType) {
		// output object:  
		jsonOutput = new JSONObject();
		for (RomediType type : outputType) {
			addType2JsonResult(mapRomediInstance2CIS, romediInstances, type);
		}
	}
	
	/**
	 * Get all the instances of a specific type
	 * @param type
	 * @return
	 */
	private TreeSet<RomediInstance> getInstances(HashSet<RomediInstance> romediInstances, RomediType type) {
		TreeSet<RomediInstance> typeInstances = new TreeSet<RomediInstance>();
		Iterator<RomediInstance> iter = romediInstances.iterator();
		while(iter.hasNext()) {
			RomediInstance romediInstance = iter.next();
			RomediType thisType = romediInstance.getType();
			if(thisType.equals(type)) {
				typeInstances.add(romediInstance);
			}
		}
		return(typeInstances);
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

	private void addType2JsonResult(HashMap<RomediInstance, HashSet<RomediInstance>> mapRomediInstance2CIS,
			HashSet<RomediInstance> romediInstances,
			RomediType romediType) {
		// When relation is 1 - 1 between CIS and romediType, :
		if (!RomediType.isOne2Many(romediType)) {
			JSONArray jsonArrayRomediType = new JSONArray();
			TreeSet<RomediInstance> romediInstancesSingleType = getInstances(romediInstances, romediType);
			Iterator<RomediInstance> iter = romediInstancesSingleType.iterator();
			while (iter.hasNext()) {
				RomediInstance romediInstance = iter.next();
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(romediInstance.getJSONObject());
				// {0:[{"uri":"theURI", "label":"theLabel"}...]}
				jsonArrayRomediType.put(jsonArray);
			}
			jsonOutput.put(romediType.toString(), jsonArrayRomediType);
		} else { // when the relation is one to many : 
			TreeSet<RomediInstance> romediInstancesSingleType = getInstances(romediInstances, romediType);
			Iterator<RomediInstance> iter = romediInstancesSingleType.iterator();

			// we need to group 
			logger.debug("Creating new mapCISinstances for type : " + romediType.toString());
			// First we need to map all instances to a specific CIS (instances must be attached to its CIS)
			HashMap<RomediInstance, TreeSet<RomediInstance>> mapCISinstances = new HashMap<RomediInstance, TreeSet<RomediInstance>>();
			logger.info("terminology size:" + mapRomediInstance2CIS.size());
			while (iter.hasNext()) {
				RomediInstance romediInstanceOneType = iter.next();
				for (RomediInstance romediInstanceCIS : mapRomediInstance2CIS.get(romediInstanceOneType)) {
					if (mapCISinstances.containsKey(romediInstanceCIS)) {
						mapCISinstances.get(romediInstanceCIS).add(romediInstanceOneType);
						//					if (romediType.toString() == "IN" && romediInstanceCIS.getUri().equals("http://www.romedi.fr/romedi#CIS69540835")) {
						//						logger.debug("size after adding : " + mapCISinstances.get(romediInstanceCIS).size());
						//					}
					} else {
						TreeSet<RomediInstance> tempRomediInstance = new TreeSet<RomediInstance>();
						tempRomediInstance.add(romediInstanceOneType);
						mapCISinstances.put(romediInstanceCIS,tempRomediInstance);
					}

				}
			}

			// then we need to get all distinct TreeSet<RomediInstance> for each CIS:
			// example of TreeSet<RomediInstance> : hydrochlorothiazide / irbésartan
			JSONArray jsonArrayRomediType = new JSONArray();
			int i = 0;
			// save all unique TreeSet<RomediInstance>
			HashSet<TreeSet<RomediInstance>> setDone = new HashSet<TreeSet<RomediInstance>>();
			for (RomediInstance romediInstanceCIS : mapCISinstances.keySet()) {
				JSONArray jsonArray = new JSONArray();
				TreeSet<RomediInstance> currentSet = mapCISinstances.get(romediInstanceCIS);
				// if set already done, next set
				if (setDone.contains(currentSet)) {
					logger.debug("currentSet already present");
					continue;
				}
				setDone.add(currentSet);
				for (RomediInstance romediInstance : currentSet) {
					jsonArray.put(romediInstance.getJSONObject());
				}
				jsonArrayRomediType.put(jsonArray);
				i = i + 1 ;
			}
			// output : 
			jsonOutput.put(romediType.toString(), jsonArrayRomediType);
		}
	}
}
