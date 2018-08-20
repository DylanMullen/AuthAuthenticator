package me.dylanmullen.spigot.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.FailedLoginEvent;
import fr.xephi.authme.events.LoginEvent;
import me.dylanmullen.spigot.core.Main;
import me.dylanmullen.spigot.managers.ConfigManager;
import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener
{

	@EventHandler
	public void onLogin(LoginEvent e)
	{
		if (Main.getInstance().getLogin().contains(e.getPlayer().getName()))
		{
			Main.getInstance().getSocket().sendBungee("add",
					e.getPlayer().getUniqueId().toString() + ":" + e.getPlayer().getName());
			Main.getInstance().getLogin().remove(e.getPlayer().getName());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if (Main.getInstance().getAutoLogin().contains(e.getPlayer().getName()))
		{

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new BukkitRunnable()
			{
				public void run()
				{
					AuthMeApi.getInstance().forceLogin(e.getPlayer());
					Main.getInstance().getAutoLogin().remove(e.getPlayer().getName());

				}

			}, 10L);
		}
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e)
	{
		if(Main.getInstance().log.contains(e.getPlayer().getName()))
			return;
		Main.getInstance().log.add(e.getPlayer().getName());
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		if (Main.getInstance().getAutoLogin().contains(e.getPlayer().getName()))
		{
			Main.getInstance().getAutoLogin().remove(e.getPlayer().getName());
		}
		if (Main.getInstance().getLogin().contains(e.getPlayer().getName()))
		{
			Main.getInstance().getLogin().remove(e.getPlayer().getName());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFailure(FailedLoginEvent e)
	{
		if (Main.getInstance().getLogin().contains(e.getPlayer().getName()))
		{
			Main.getInstance().getLogin().remove(e.getPlayer().getName());
			e.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&',
					ConfigManager.getManager().getMessages().getConfiguration().getString("wrong-password")));
		}
	}

}
