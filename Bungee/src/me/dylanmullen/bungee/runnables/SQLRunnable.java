package me.dylanmullen.bungee.runnables;

import java.sql.SQLException;

import me.dylanmullen.bungee.managers.SQLManager;

public class SQLRunnable implements Runnable
{

	@Override
	public void run()
	{
		try
		{
			if(SQLManager.getInstance().getConnection().isClosed())
			{
				SQLManager.getInstance().reopenConnection();
			}
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
}
