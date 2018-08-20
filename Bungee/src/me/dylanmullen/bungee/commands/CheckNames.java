package me.dylanmullen.bungee.commands;

import java.util.ArrayList;
import java.util.UUID;

import me.dylanmullen.bungee.managers.SQLManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CheckNames extends BlockCommand
{

	public CheckNames(String name, String permission, String usage, String... aliases)
	{
		super(name, permission, usage, aliases);
	}

	// /checknames <name|uuid> [-s]
	/*
	 * 
	 */
	
	private String pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	
	@Override
	public void run(String[] args)
	{
		if(args.length == 0)
		{
			sendUsage();
			return;
		}
		String arg = args[0];
		
		if(arg.matches(pattern))
		{
			UUID u = UUID.fromString(arg);
			String s = SQLManager.getInstance().getCurrentName(u);
			if(s == null)
			{
				sendMessage("Cannot find an account with that UUID");
				return;
			}
			
			sendUnformattedMessage(ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + ChatColor.BOLD +  "-------------------------");
			sendUnformattedMessage(ChatColor.YELLOW + "Current Name: " + ChatColor.WHITE + s);
			sendUnformattedMessage(ChatColor.YELLOW + "UUID: " + ChatColor.WHITE + u.toString());
			String prev = getPreviousNames(u);
			if(prev == null)
			{
				sendUnformattedMessage(ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + ChatColor.BOLD +  "-------------------------");
				return;
			}
			sendUnformattedMessage(ChatColor.YELLOW + "Previous Names: ");
			sendUnformattedMessage(prev);
			sendUnformattedMessage(ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + ChatColor.BOLD +  "-------------------------");
			return;
		}
		else	
		{
			boolean inUse = isInUse(arg);
			sendUnformattedMessage(ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + ChatColor.BOLD +  "-------------------------");
			sendUnformattedMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + arg);
			sendUnformattedMessage(ChatColor.YELLOW + "Name in Use: " + ChatColor.WHITE + (inUse ? ChatColor.GREEN +"True" : ChatColor.RED + "False"));
			if(inUse)
			{
				if(isPlayer())
					getPlayer().sendMessage(getMessage(arg));
				else
					sendUnformattedMessage(ChatColor.YELLOW + "Current UUID: " + SQLManager.getInstance().getOUUID(arg));
			}
			getPreviousUUIDs(arg, isPlayer());
			sendUnformattedMessage(ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + ChatColor.BOLD +  "-------------------------");
		}
		
	}
	
	public String getPreviousNames(UUID u)
	{
		String s = SQLManager.getInstance().getPreviousNames(u);
		
		if(s == null)
		{
			System.out.println("Default null");
			return null;
		}
		
		if(s.length() == 0)
		{
			System.out.println("Length");
			return null;
		}
		
		String[] list = s.split(";");
		
		StringBuilder bb = new StringBuilder();
		
		for(int i = 0; i < list.length; i++)
		{
			bb.append(ChatColor.AQUA + list[i] + (i == (list.length-1) ? ChatColor.YELLOW +"." : ChatColor.YELLOW +", "));
		}
		return bb.toString();
	}

	public boolean isInUse(String name)
	{
		return SQLManager.getInstance().isNameInUse(name);
	}
	
	@SuppressWarnings("deprecation")
	public void getPreviousUUIDs(String name, boolean player)
	{
		ArrayList<String> names = SQLManager.getInstance().getPreviousUUIDs(name);
		
		if(names.size() == 0)
			return;

		sendUnformattedMessage(ChatColor.YELLOW + "Previous UUIDs:");
		
		if(player)
		{
			ComponentBuilder b = new ComponentBuilder("");
			for(int i = 0; i < names.size(); i++)
			{
				ComponentBuilder tc = new ComponentBuilder(ChatColor.WHITE + names.get(i) + (names.size() - 1 == i ? ChatColor.YELLOW + "." : ChatColor.YELLOW +", "));
				tc.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/checkname " + names.get(i)));
				tc.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Click to see " + ChatColor.WHITE + names.get(i) + ChatColor.YELLOW + " profile.").create()));
				b.append(tc.create());
			}
			getPlayer().sendMessage(b.create());
		}
		else
		{
			StringBuilder bb = new StringBuilder();
			for(int i = 0; i < names.size(); i++)
			{
				bb.append(ChatColor.AQUA + names.get(i) + (i == (names.size()-1) ? ChatColor.YELLOW +"." : ChatColor.YELLOW +", "));
			}
			getCommandSender().sendMessage(bb.toString());
		}
	}
	
	private TextComponent getMessage(String name)
	{
		UUID ou = SQLManager.getInstance().getOUUID(name);
		TextComponent tc = new TextComponent(ChatColor.YELLOW + "Current UUID: " + ChatColor.WHITE + ou.toString());
		tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/checkname " + ou.toString()));
		tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.YELLOW + "Click to view this UUID").create()));
		return tc;
	}
	
}
