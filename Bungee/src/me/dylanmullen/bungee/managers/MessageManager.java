package me.dylanmullen.bungee.managers;

import net.md_5.bungee.api.ChatColor;

public class MessageManager
{

	public static String LOGO = ChatColor.translateAlternateColorCodes('&',
			ConfigManager.getManager().getMessagesConfig().getConfig().getString("logo")) + ChatColor.RESET;
	public static String FAILED_CONNECTION = ChatColor.translateAlternateColorCodes('&',
			ConfigManager.getManager().getMessagesConfig().getConfig().getString("premium-service"));
}
