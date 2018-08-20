package me.dylanmullen.spigot.managers;

import me.dylanmullen.spigot.core.Main;
import me.dylanmullen.spigot.util.Config;

public class ConfigManager
{
	static ConfigManager m = new ConfigManager();

	Config config, messages;
	
	public static ConfigManager getManager()
	{
		return m;
	}

	public void load()
	{
		config = new Config("config.yml", Main.getInstance().getDataFolder().getAbsolutePath(), true);
		messages = new Config("messages.yml", Main.getInstance().getDataFolder().getAbsolutePath(), true);
	}

	public Config getConfigFile()
	{
		return config;
	}

	public Config getMessages()
	{
		return messages;
	}
}