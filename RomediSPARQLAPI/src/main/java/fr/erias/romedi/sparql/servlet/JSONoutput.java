package fr.erias.romedi.sparql.servlet;

import java.util.Collection;
import java.util.HashSet;

import org.json.JSONArray;

import fr.erias.romedi.sparql.connection.ResultLinks;
import fr.erias.romedi.terminology.RomediInstanceCIS;

public class JSONoutput {
	
	/**
	 * Get a JSON array from a set of {@link RomediInstanceCIS}
	 * @param romediInstancesCIS a Set of RomediInstance of type "CIS"
	 * @return a JSON array
	 */
	public static JSONArray getJSONcis (HashSet<RomediInstanceCIS> romediInstancesCIS){
		JSONArray jsonArray = new JSONArray();
		for (RomediInstanceCIS romediInstanceCIS : romediInstancesCIS) {
			jsonArray.put(romediInstanceCIS.getJSONObject());
		}
		return(jsonArray);
	}
	
	/**
	 * Get a JSON array from a collection of {@link ResultLinks}
	 * @param resultsLinks all the links for a Romedi CIS instance
	 * @return ResultLinks
	 */
	public static JSONArray getJSONlinks(Collection<ResultLinks> resultsLinks) {
		JSONArray jsonArray = new JSONArray();
		for (ResultLinks resultLinks : resultsLinks) {
			jsonArray.put(resultLinks.getJSONobject());
		}
		return(jsonArray);
	}
}
