package me.dylanmullen.spigot.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import fr.xephi.authme.api.v3.AuthMeApi;
import me.dylanmullen.spigot.managers.ConfigManager;

public class SpigotSocket implements Runnable
{

	private ServerSocket socket;
	private Thread thread;
	private boolean loop;

	public SpigotSocket(int port)
	{
		try
		{
			socket = new ServerSocket(port);
			start();
		} catch (IOException e)
		{
			Main.getInstance().getLogger().info("This port is in use");
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
				
				System.out.println("Has recieved something from Bungee");
				
				if (channel.equalsIgnoreCase("[BlockDrop]"))
				{
					String argument = in.readUTF();
					String data = in.readUTF();
					handleArguments(argument, data);
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void sendBungee(String argument, String data)
	{
		try
		{
			Socket client = new Socket(
					ConfigManager.getManager().getConfigFile().getConfiguration().getString("bungeecord-ip"), 1300);
			DataOutputStream ds = new DataOutputStream(client.getOutputStream());
			ds.writeUTF("[BlockDrop]");
			ds.writeUTF(argument);
			ds.writeUTF(data);
			ds.close();
			client.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void handleArguments(String argument, String data)
	{
		if (argument.equalsIgnoreCase("login"))
		{
			Main.getInstance().getAutoLogin().add(data);
		} else if (argument.equalsIgnoreCase("register"))
		{
			String[] s = data.split(":");
			UUID uuid = UUID.fromString(s[0]);
			String name = s[1];

			if (!AuthMeApi.getInstance().isRegistered(name))
			{
				AuthMeApi.getInstance().registerPlayer(name,
						ConfigManager.getManager().getConfigFile().getConfiguration().getString("password"));
				Main.getInstance().getAutoLogin().add(name);
				sendBungee("add", uuid.toString() + ":" + name);
				return;
			} else
			{
				Main.getInstance().getLogin().add(name);
				return;
			}
		}

	}

}
