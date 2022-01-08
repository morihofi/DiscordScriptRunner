package de.morihofi.scriptrunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public class Start {
	public static String startchar = "!";
	private static String token = "";

	public static String db_database = "jdbc:h2:~/scriptrunnerdb"; // will changed on main()
	public static final String db_user = "scriptrunner";
	public static final String db_pass = "";
	public static String db_table = "scriptrunner";

	public static String getstartupdir() {
		return System.getProperty("user.dir");
	}

	/*
	* Funtktion zum laden einer Datei in einen String
	*/
	private static String readLineByLineJava8(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentBuilder.toString();
	}

	public static void main(String[] args) throws Exception {

		//Config einlesen
		String settingsfilepath = getstartupdir() + File.separator + "settings.json";

		File settingsfile = new File(settingsfilepath);
		if (settingsfile.exists()) {

			System.out.println("Reading " + settingsfilepath + "...");

			String settingsjson = readLineByLineJava8(settingsfilepath);
																			
			JSONObject obj = new JSONObject(settingsjson);
			token = obj.getString("token");
			startchar = obj.getString("startcharacter");

			System.out.println("Reading complete!");

		} else {
			System.out.println(settingsfilepath + " doesn't exist. Exiting...");
			System.exit(1);
		}

		//Datenbank laden
		try {
			System.out.println("Loading H2-Database driver...");
			Class.forName("org.h2.Driver");

			System.out.println("Initializing database...");
			db_database = "jdbc:h2:" + getstartupdir() + File.separator + "scriptrunnerdb";
			Database.db_init();

		} catch (Exception ex) {
			System.out.println("Error loading H2-Database driver! Exiting...");
			ex.printStackTrace();
			System.exit(1);
		}

		System.out.println("Starting bot...");

		//Extrahiere ClientID aus Discord-Token
		String clientid = TokenHelper.getclientidfromtoken(token);
		System.out.println("Add this bot to your server using this link:");
		System.out.println("https://discord.com/api/oauth2/authorize?client_id=" + clientid
				+ "&permissions=534723950656&scope=bot");
		

		Bot.startbot(token);

	}
}
