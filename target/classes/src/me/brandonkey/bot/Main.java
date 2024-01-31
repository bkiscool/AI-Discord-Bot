package me.brandonkey.bot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
	
	private static String DISCORD_BOT_TOKEN;
	private static String OPEN_AI_API_TOKEN;
	private static boolean ENABLE_DISCORD_BOT;
	private static boolean ENABLE_OPEN_AI_API;

	public static void main(String[] args)
	{
		loadConfig();
		
		if (ENABLE_DISCORD_BOT)
		{
			new DiscordBot(DISCORD_BOT_TOKEN);
		}
		
		if (ENABLE_OPEN_AI_API)
		{
			new AI(OPEN_AI_API_TOKEN);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private static void loadConfig()
	{
		File config = new File("config.json");
		
		if (!config.exists())
		{
			Logger.LOG.print("config.json not found. Loading default config values...");
			
			try {
				JSONObject jsonValues = new JSONObject();
				jsonValues.put("discord-bot-token", "bot_token_here");
				jsonValues.put("openai-api-token", "api_token_here");
				jsonValues.put("enable-discord-bot", true);
				jsonValues.put("enable-openai-api", true);
				
				FileWriter fileWriter = new FileWriter(config);
				jsonValues.writeJSONString(fileWriter);
				fileWriter.close();
				
				Logger.LOG.print("Loaded default JSON values:\n" + jsonValues.toString());
				
			} catch (IOException e) {
				Logger.WARNING.print("Could not load default JSON values!");
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		
		Logger.LOG.print("Loading config.json");
		
		try {
			FileReader fileReader = new FileReader(config);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonValues = (JSONObject) jsonParser.parse(fileReader);
			
			DISCORD_BOT_TOKEN = (String) jsonValues.get("discord-bot-token");
			OPEN_AI_API_TOKEN = (String) jsonValues.get("openai-api-token");
			ENABLE_DISCORD_BOT = (boolean) jsonValues.get("enable-discord-bot");
			ENABLE_OPEN_AI_API = (boolean) jsonValues.get("enable-openai-api");
			
			Logger.LOG.print("Loaded JSON values from config.json:\n" + jsonValues.toString());
			
		} catch (IOException | ParseException | NullPointerException e) {
			Logger.WARNING.print("Could not load JSON values from config.json!");
			e.printStackTrace();
			System.exit(1);
		}
		
	}

}
