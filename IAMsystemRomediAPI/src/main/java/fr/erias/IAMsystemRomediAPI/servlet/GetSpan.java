package fr.erias.IAMsystemRomediAPI.servlet;

/**
 * Get a HTML span for a detection
 * 
 * @author Cossin Sebastien
 *
 */
public class GetSpan {
	
	public static String getSpan(String uri, String label, String type, String candidateTermString) {
		String span = "<a class='detected' href='javascript:void(0)' " + 
				" label=\"" + label + "\" " + 
				" uri=\"" + uri + "\" " +
				" type=\"" + type + "\" " +
				" onclick=\"sendURI(this.getAttribute('label'), this.getAttribute('type'),this.getAttribute('uri'))\">" +
				candidateTermString +
				"</a>";
		return(span);
	}
}
