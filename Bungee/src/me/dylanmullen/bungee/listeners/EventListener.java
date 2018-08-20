package me.dylanmullen.bungee.listeners;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.UUID;

import me.dylanmullen.bungee.core.Main;
import me.dylanmullen.bungee.managers.MessageManager;
import me.dylanmullen.bungee.managers.SQLManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements Listener
{

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPreLogin(PreLoginEvent e)
	{
		if(!BungeeCord.getInstance().getConfig().isOnlineMode())
		{
			try
			{
				if(SQLManager.getInstance().getConnection().isClosed())
				{
					e.setCancelled(true);
					e.setCancelReason(ChatColor.RED + "An Error has occured: Error 182. Check Console");
					Main.getInstance().getLogger().info("Error has occured: Cannot connect to MySQL DB");
					return;
				}
			} catch (SQLException e1)
			{
				e1.printStackTrace();
			}
			
			if(!SQLManager.getInstance().isConnected())
			{
				e.setCancelled(true);
				e.setCancelReason(ChatColor.RED + "An Error has occured: Error 182. Check Console");
				Main.getInstance().getLogger().info("Error has occured: Cannot connect to MySQL DB");
				return;
			}
			
			if(SQLManager.getInstance().isUsedName(e.getConnection().getName()))
			{
				e.setCancelled(true);
				e.setCancelReason(MessageManager.LOGO + MessageManager.FAILED_CONNECTION);
			}
		}
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent e)
	{
		ProxiedPlayer pp = e.getPlayer();
		
		if(BungeeCord.getInstance().getConfig().isOnlineMode())
		{
			System.out.println("Online mode");
			if(SQLManager.getInstance().isInDB(pp.getUniqueId()))
			{
				if(!SQLManager.getInstance().getName(pp.getUniqueId()).equalsIgnoreCase(pp.getName()))
				{
					System.out.println("Register");
					Main.getInstance().getSocket().sendBukkit("register", pp.getUniqueId().toString() + ":" + pp.getName());
					return;
				}
				changeUUID(pp);
				System.out.println("Login");
				Main.getInstance().getSocket().sendBukkit("login", pp.getName());
				return;
			}
			
			System.out.println("Yeot");
			Main.getInstance().getSocket().sendBukkit("register", pp.getUniqueId().toString() + ":" + pp.getName());
			
		}
	}

	private void changeUUID(ProxiedPlayer pp)
	{
		InitialHandler init = (InitialHandler)pp.getPendingConnection();
		UUID u = pp.getUniqueId();
		try
		{
			Field f = InitialHandler.class.getDeclaredField("uniqueId");
			f.setAccessible(true);
			f.set(init, SQLManager.getInstance().getOUUID(u));
			f.setAccessible(false);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
