package me.dylanmullen.spigot.core;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.dylanmullen.spigot.listener.EventListener;
import me.dylanmullen.spigot.managers.ConfigManager;

public class Main extends JavaPlugin
{

	private SpigotSocket socket;
	private static Main instance;
	
	private ArrayList<String> autoLogin = new ArrayList<>();
	private ArrayList<String> login = new ArrayList<>();
	public ArrayList<String> log = new ArrayList<>();
	
	public void onEnable()
	{
		if(instance == null)
			instance = this;
		
		ConfigManager.getManager().load();
		socket = new SpigotSocket(ConfigManager.getManager().getConfigFile().getConfiguration().getInt("port"));
		socket.start();
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}

	public SpigotSocket getSocket()
	{
		return socket;
	}
	
	public static Main getInstance()
	{
		return instance;
	}

	public ArrayList<String> getAutoLogin()
	{
		return autoLogin;
	}

	public ArrayList<String> getLogin()
	{
		return login;
	}
}
