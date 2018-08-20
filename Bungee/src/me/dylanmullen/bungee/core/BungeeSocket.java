package me.dylanmullen.bungee.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import me.dylanmullen.bungee.managers.ConfigManager;
import me.dylanmullen.bungee.managers.SQLManager;
import net.md_5.bungee.BungeeCord;

public class BungeeSocket implements Runnable
{

	private ServerSocket socket;
	private Thread thread;
	private boolean loop;

	public BungeeSocket(int port)
	{
		try
		{
			socket = new ServerSocket(port);
			start();
		} catch (IOException e)
		{
			if (port != 1300)
				BungeeCord.getInstance().broadcast("This port is in use");
		}
	}

	public void start()
	{
		loop = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run()
	{
		while (loop)
		{
			try
			{
				Socket client = socket.accept();

				DataInputStream in = new DataInputStream(client.getInputStream());
				String channel = in.readUTF();

				if (channel.equalsIgnoreCase("[BlockDrop]"))
				{
					String argument = in.readUTF();
					String data = in.readUTF();
					handleArguments(argument, data);
					continue;
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void sendBukkit(String argument, String data)
	{
		for (int x : Main.getInstance().ports)
		{
			try
			{
				Socket client = new Socket(ConfigManager.getManager().getConfigFile().getConfig().getString("bukkit-ip"), x);
				DataOutputStream ds = new DataOutputStream(client.getOutputStream());
				ds.writeUTF("[BlockDrop]");
				ds.writeUTF(argument);
				ds.writeUTF(data);
				ds.close();
				client.close();
			} catch (Exception e)
			{
				return;
			}
		}
	}

	public void handleArguments(String argument, String data)
	{
		if (argument.equalsIgnoreCase("add"))
		{
			String[] s = data.split(":");
			UUID pu = UUID.fromString(s[0]);
			String name = s[1];
			
			if(SQLManager.getInstance().isInDB(pu))
			{
				SQLManager.getInstance().update(pu, name, SQLManager.getInstance().getName(pu));
				if(!SQLManager.getInstance().isUsedName(name))
					SQLManager.getInstance().insertUsedName(name);
				Main.getInstance().changeUUID(name, SQLManager.getInstance().getOUUID(BungeeCord.getInstance().getPlayer(name).getUniqueId()));
				return;
			}
			else
			{
				UUID ou = Main.getInstance().getOfflineUUID(name);
				
				SQLManager.getInstance().insert(pu, ou, name);
				if(!SQLManager.getInstance().isUsedName(name))
					SQLManager.getInstance().insertUsedName(name);
				Main.getInstance().changeUUID(BungeeCord.getInstance().getPlayer(pu));
				return;
			}
		}

	}

}
