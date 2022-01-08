package de.morihofi.scriptrunner;

import java.util.Base64;

public class TokenHelper {

	public static String getclientidfromtoken(String token) {
		String clientid = "";
		
		clientid = token.split("\\.")[0];
		byte[] decodedBytes = Base64.getDecoder().decode(clientid);
		clientid = new String(decodedBytes);
		
		return clientid;
	}

}
