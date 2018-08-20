package me.dylanmullen.bungee.managers;

import me.dylanmullen.bungee.core.Main;
import me.dylanmullen.bungee.util.Config;

public class ConfigManager
{
	static ConfigManager m = new ConfigManager();

	private Config config, messages;
	
	public static ConfigManager getManager()
	{
		return m;
	}

	public void load()
	{
		config = new Config("config.yml", Main.getInstance().getDataFolder().getAbsolutePath() + "/config.yml", true);
		messages = new Config("messages.yml", Main.getInstance().getDataFolder().getAbsolutePath() + "/messages.yml", true);
	}

	public Config getConfigFile()
	{
		return config;
	}
	
	public Config getMessagesConfig()
	{
		return messages;
	}
	
}