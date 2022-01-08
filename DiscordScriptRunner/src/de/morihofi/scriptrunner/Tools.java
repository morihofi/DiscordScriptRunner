package de.morihofi.scriptrunner;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Tools {

	public static String StackTraceToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		return sw.toString();
		
	}
}
