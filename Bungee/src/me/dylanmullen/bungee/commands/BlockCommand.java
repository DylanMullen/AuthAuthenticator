package me.dylanmullen.bungee.commands;

import me.dylanmullen.bungee.managers.MessageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class BlockCommand extends Command
{

	private CommandSender cs;
	private String usage;
	
	protected String WEAK = ChatColor.YELLOW.toString(), BOLD = ChatColor.BOLD + "";
	
	public BlockCommand(String name, String permission, String usage, String... aliases)
	{
		super(name, permission, aliases);
		this.usage = usage;
	}
	
	public abstract void run(String[] args);
	
	@Override
	public void execute(CommandSender cs, String[] args)
	{
		this.cs = cs;
		run(args);
	}

	public void sendUsage()
	{
		sendMessage("Usage: " + usage);
	}
	
	public boolean hasPermission()
	{
		return cs.hasPermission(getPermission());
	}
	
	@SuppressWarnings("deprecation")
	public void sendMessage(String mes)
	{
		cs.sendMessage(MessageManager.LOGO + mes);
	}
	
	@SuppressWarnings("deprecation")
	public void sendUnformattedMessage(String mes)
	{
		cs.sendMessage(mes);
	}
	
	public void sendTextComponent(TextComponent tc)
	{
		cs.sendMessage(tc);
	}
	
	public boolean isPlayer()
	{
		return (cs instanceof ProxiedPlayer);
	}
	
	public ProxiedPlayer getPlayer()
	{
		return (ProxiedPlayer)cs;
	}
	
	public CommandSender getCommandSender()
	{
		return cs;
	}
	
}
