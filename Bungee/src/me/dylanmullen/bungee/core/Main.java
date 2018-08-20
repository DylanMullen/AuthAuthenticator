package me.dylanmullen.bungee.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;

import me.dylanmullen.bungee.commands.CheckNames;
import me.dylanmullen.bungee.listeners.EventListener;
import me.dylanmullen.bungee.managers.ConfigManager;
import me.dylanmullen.bungee.managers.SQLManager;
import me.dylanmullen.bungee.runnables.SQLRunnable;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin
{

	static Main instance;
	public ArrayList<Integer> ports = new ArrayList<>();

	public BungeeSocket socket;

	public void onEnable()
	{
		if (instance == null)
			instance = this;

		ConfigManager.getManager().load();
		SQLManager.getInstance().load();
		
		startRunnable();
		
		if(BungeeCord.getInstance().getConfig().isOnlineMode())
		{
			socket = new BungeeSocket(1300);
			socket.start();
			for (String s : ConfigManager.getManager().getConfigFile().getConfig().getStringList("bukkit-ports"))
			{
				int x = Integer.parseInt(s);
				ports.add(x);
			}
		}
		
		BungeeCord.getInstance().getPluginManager().registerCommand(this, new CheckNames("checkname", "blockdrop.checkname", "/checkname <name|uuid>",""));

		BungeeCord.getInstance().getPluginManager().registerListener(this, new EventListener());
	}

	private void startRunnable()
	{
		BungeeCord.getInstance().getScheduler().schedule(this, new SQLRunnable(), 0, 5, TimeUnit.SECONDS);
	}
	
	public UUID getOfflineUUID(String name)
	{
		return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8));	
	}

	public static Main getInstance()
	{
		return instance;
	}

	public BungeeSocket getSocket()
	{
		return socket;
	}

	public void changeUUID(ProxiedPlayer pp)
	{
		PendingConnection pc = pp.getPendingConnection();
		Class<?> initialHandlerClass = pc.getClass();
		BungeeCord.getInstance().broadcast("UUID: " + pp.getUniqueId());
		Field uuid;
		try
		{
			uuid = initialHandlerClass.getDeclaredField("uniqueId");
			uuid.setAccessible(true);
			uuid.set(pc, getOfflineUUID(pp.getName()));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		BungeeCord.getInstance().broadcast("UUID: " + pp.getUniqueId());
	}
	
	public void changeUUID(String name, UUID u)
	{
		ProxiedPlayer pp =  BungeeCord.getInstance().getPlayer(name);
		PendingConnection pc = pp.getPendingConnection();
		Class<?> initialHandlerClass = pc.getClass();
		BungeeCord.getInstance().broadcast("UUID: " + pp.getUniqueId());
		Field uuid;
		try
		{
			uuid = initialHandlerClass.getDeclaredField("uniqueId");
			uuid.setAccessible(true);
			uuid.set(pc, u);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		BungeeCord.getInstance().broadcast("UUID: " + pp.getUniqueId());
	}

}
