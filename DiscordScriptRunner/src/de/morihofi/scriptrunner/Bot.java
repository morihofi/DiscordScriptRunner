package de.morihofi.scriptrunner;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Bot extends ListenerAdapter {

	public static void startbot(String token) throws Exception {

		JDA jda = JDABuilder.createDefault(token).addEventListeners(new Bot())
				.setActivity(Activity.playing("Type " + Start.startchar + "help")).build();

	}

	public static String getlangofcodeblock(String codeblock) {

		String lang = "";

		String[] result = codeblock.split("\n");

		lang = result[0].substring(3);

		return lang;
	}

	public static String getcodeofcodeblock(String codeblock) {

		String code = "";

		String[] result = codeblock.split("\n");

		LinkedList<String> resultlist = new LinkedList();

		for (String l : result) {
			resultlist.add(l);
		}

		resultlist.removeLast();
		resultlist.removeFirst();

		code = String.join("\n", resultlist);

		return code;
	}

	public static String getenginenameoflang(String codelang) {
		String enginename = "";
		switch (codelang) {
		case "js": // JavaScript
			System.out.println("Language is JavaScript");
			enginename = "javascript";
			break;
		case "python": // Python
			System.out.println("Language is Python");
			enginename = "python";
			break;
		case "ruby": // Ruby
			System.out.println("Language is Ruby");
			enginename = "jruby";
			break;
		case "lua": // Lua
			System.out.println("Language is Lua");
			enginename = "luaj";
			break;
		case "groovy": // Ruby
			System.out.println("Language is Groovy");
			enginename = "groovy";
			break;
		
		default:
			break;
		}

		return enginename;
	}
	
	
	public Boolean containscodeinblacklist(String code) {
		
		Boolean isinbl = false;
		
		LinkedList<String> blacklist = new LinkedList<String>();
		blacklist.clear();
		//blacklist.addLast("Socket");
		//blacklist.addLast("Thread");
		
		
		String[] strgs = code.split(" ");
		
		for(String word : strgs) {
			
			if(blacklist.contains(word)) {
				isinbl = true;
				break;
			}
			
		}
		
		return isinbl;
		
		
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message msg = event.getMessage();

		
		
		if (msg.getContentRaw().startsWith(Start.startchar + "help")) {
			
			msg.getChannel().sendMessage("❓ Script Runner Help \n```"
					+ "Type \"" + Start.startchar + "run\" an then your script to run\n"
					+ "\n"
					+ "Currently supported languages:\n"
					+ "  - JavaScript (use print() instead of console.log())\n"
					+ "  - Python 2.7\n"
					+ "  - Ruby\n"
					+ "  - Lua\n"
					+ "  - Groovy\n"
					+ "\n"
					+ "ScriptRunner is written by Moritz Hofmann/morihofi.de\n"
					+ "```").complete();
		}
		
				
		
		if (msg.getContentRaw().startsWith(Start.startchar + "run")) {
			MessageChannel channel = event.getChannel();

			final String codeblock = msg.getContentRaw().substring((Start.startchar + "run ").length());
			final String codelang = getlangofcodeblock(codeblock); 
			final String codeonly = getcodeofcodeblock(codeblock);
			final String enginename = getenginenameoflang(codelang);

			msg.getChannel().sendMessage("⌛ Executing, please wait...").queue(response /* => Message */ -> {

				String output = "";
				
				if (enginename != "") {

					try {
						ScriptEngine engine = new ScriptEngineManager().getEngineByName(enginename);
						ScriptContext context = engine.getContext();
						
						StringWriter writer = new StringWriter();
						context.setWriter(writer);

						context.setErrorWriter(writer);

						if(containscodeinblacklist(codeonly)) {
							response.editMessage("⚠️ This code has blacklisted codes").queue();
							return;
						}
										
						engine.eval(codeonly);

						
						output = writer.toString();

						output = output + " ";
						
						response.editMessage("ℹ️ Script output: ```" + output + "```").queue();

					} catch (ScriptException | org.luaj.vm2.LuaError | org.jruby.embed.EvalFailedException e) {
						response.editMessage("❌ Error on execution: ```" + e.getMessage() + "```").queue();
					
					} catch (Exception e) {
						
						
						response.editMessage("❌ Internal exception: \n" + e.getMessage() + "\n\nStack-Trace:\n" + Tools.StackTraceToString(e)).queue();
						e.printStackTrace();
					}
					
					//Schreibe ausgeführten Befehl in die Datenbank
					Database.db_insert(response.getMember().getId(), String.valueOf(java.time.Instant.now().getEpochSecond()), codelang, codeonly, output);

				}else{
					response.editMessage("❌ This programming language \"" + codelang + "\" is currently not supported!").queue();
				}

			});

		}
		
		
		

	}
	
	
	
	
	

}