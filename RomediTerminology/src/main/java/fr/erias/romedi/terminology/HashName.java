package fr.erias.romedi.terminology;

import java.nio.charset.Charset;

import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

/**
 * The class used to hash a drug name :
 * Ex : ESCITALOPRAM = rth34o4uohkjffrahl2pepb44lkgp5ui
 * http://www.romedi.fr/romedi/BNrth34o4uohkjffrahl2pepb44lkgp5ui
 *
 * @author Cossin Sebastien
 *
 */
public class HashName {
	private static final Charset OUTPUT_CHARSET = Charsets.UTF_8;

	/**
	 * 
	 * @param name a drug name to hash
	 * @return the hash of the drug name
	 */
	public static String hashName(String name) {
		HashCode hashCode = Hashing.sha1().hashString(name, OUTPUT_CHARSET);
		String hash = BaseEncoding.base32Hex().omitPadding().lowerCase().encode(hashCode.asBytes());
		return(hash);
	}

	/**
	 * @param name a drug name to hash
	 * @return the hash of the drug name
	 * @param romediType A {@link RomediType}
	 * @return the hash of the drug name
	 */
	public static String hashName(String name, RomediType romediType) {
		String hash = hashName(name);
		return(romediType.toString() + hash);
	}

	/**
	 * For API 
	 * @param name a drug name to hash
	 * @return a JSON object with 2 keys : the name and the hash
	 */
	public static JSONObject getHashName(String name) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", name);
		jsonObject.put("hash", hashName(name));
		return jsonObject;
	}

	public static void main(String[]args) {
		System.out.println(hashName("ESCITALOPRAM"));
	}

}
