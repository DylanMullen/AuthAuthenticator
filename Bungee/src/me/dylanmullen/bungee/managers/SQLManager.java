package me.dylanmullen.bungee.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class SQLManager
{

	private static SQLManager m = new SQLManager();

	private Connection connection;
	private String host;
	private int port;
	private String database;
	private String username;
	private String password;
	private String table = "premium";

	private boolean connected;

	public static SQLManager getInstance()
	{
		return m;
	}

	public void load()
	{
		host = ConfigManager.getManager().getConfigFile().getConfig().getString("host");
		port = ConfigManager.getManager().getConfigFile().getConfig().getInt("sql-port");
		database = ConfigManager.getManager().getConfigFile().getConfig().getString("database");
		username = ConfigManager.getManager().getConfigFile().getConfig().getString("username");
		password = ConfigManager.getManager().getConfigFile().getConfig().getString("password");

		try
		{
			openConnection();
		} catch (ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
			connected = false;
			return;
		}
		generateUUIDTable();
		generateNameTable();
	}

	public void openConnection() throws SQLException, ClassNotFoundException
	{
		if (connection != null && !connection.isClosed())
			return;
		synchronized (this)
		{
			if (connection != null && !connection.isClosed())
				return;

			connected = true;
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username,
					password);
		}
	}

	public void reopenConnection() throws SQLException, ClassNotFoundException
	{
		if (!connection.isClosed())
			return;
		synchronized (this)
		{
			if (connection != null)
			{
				connection = null;
			}
			connected = true;
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username,
					password);
		}
	}

	public Connection getConnection()
	{
		return connection;
	}

	public void generateUUIDTable()
	{
		String generate = "CREATE TABLE IF NOT EXISTS " + table + " (puuid text, ouuid text, name text, prev text);";
		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(generate);
			p.executeUpdate();
			return;
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void generateNameTable()
	{
		String generate = "CREATE TABLE IF NOT EXISTS names (name text);";
		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(generate);
			p.executeUpdate();
			return;
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	// Insert
	public void insert(UUID puuid, UUID uuid, String name)
	{
		String insert = "INSERT INTO " + table + " VALUES(?, ?,?,?);";
		PreparedStatement p = null;

		try
		{
			p = connection.prepareStatement(insert);
			p.setString(1, puuid.toString());
			p.setString(2, uuid.toString());
			p.setString(3, name);
			p.setString(4, "");
			p.executeUpdate();
		} catch (SQLException e)
		{
			e.printStackTrace();
			return;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void update(UUID puuid, String name, String previous)
	{
		String insert = "UPDATE " + table + " SET name = ? prev = ? where puuid=?;";
		PreparedStatement p = null;
		String prev = getPreviousName(puuid);
		try
		{
			p = connection.prepareStatement(insert);
			p.setString(1, name);
			p.setString(2, (prev == null ? previous : prev + ";" + previous));
			p.setString(3, puuid.toString());
			p.executeUpdate();
		} catch (SQLException e)
		{
			e.printStackTrace();
			return;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isInDB(UUID uuid)
	{
		String insert = "SELECT * FROM " + table + " WHERE puuid=?;";
		PreparedStatement p = null;

		try
		{
			p = connection.prepareStatement(insert);
			p.setString(1, uuid.toString());

			ResultSet res = p.executeQuery();
			return res.next();

		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public String getName(UUID uuid)
	{
		String get = "SELECT * FROM " + table + " WHERE puuid=?;";

		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			p.setString(1, uuid.toString());
			ResultSet res = p.executeQuery();

			while (res.next())
			{
				return res.getString("name");
			}
			return null;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isNameInUse(String name)
	{
		String get = "SELECT * FROM " + table + " WHERE name=?";

		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			p.setString(1, name);
			ResultSet res = p.executeQuery();

			return res.next();
		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public String getPreviousNames(UUID uuid)
	{
		String get = "SELECT * FROM " + table + " WHERE ouuid=?;";

		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			p.setString(1, uuid.toString());
			ResultSet res = p.executeQuery();

			while (res.next())
			{
				return res.getString("prev");
			}
			return null;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public String getPreviousName(UUID uuid)
	{
		String get = "SELECT * FROM " + table + " WHERE puuid=?;";

		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			p.setString(1, uuid.toString());
			ResultSet res = p.executeQuery();

			while (res.next())
			{
				return res.getString("prev");
			}
			return null;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void closeConnection()
	{
		try
		{
			connection.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public UUID getOUUID(UUID u)
	{
		String get = "SELECT * FROM " + table + " WHERE puuid=?;";

		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			p.setString(1, u.toString());
			ResultSet res = p.executeQuery();

			while (res.next())
			{
				return UUID.fromString(res.getString("ouuid"));
			}
			return null;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public UUID getOUUID(String name)
	{
		String get = "SELECT * FROM " + table + " WHERE name=?;";

		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			p.setString(1, name);
			ResultSet res = p.executeQuery();

			while (res.next())
			{
				return UUID.fromString(res.getString("ouuid"));
			}
			return null;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<String> getPreviousUUIDs(String name)
	{
		String get = "SELECT * FROM " + table;
		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			ResultSet res = p.executeQuery();

			ArrayList<String> list = new ArrayList<>();
			while (res.next())
			{
				String s = res.getString("prev");
				String[] j = s.split(";");
				for (String x : j)
				{
					if (x.equalsIgnoreCase(name))
					{
						list.add(res.getString("ouuid"));
					}
				}
			}

			return list;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public String getCurrentName(UUID ou)
	{
		String select = "SELECT * FROM " + table + " WHERE ouuid=?";
		PreparedStatement p = null;

		try
		{
			p = this.connection.prepareStatement(select);
			p.setString(1, ou.toString());
			ResultSet res = p.executeQuery();

			while (res.next())
			{
				return res.getString("name");
			}
			return null;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void insertUsedName(String name)
	{
		String insert = "INSERT INTO names VALUES(?);";
		PreparedStatement p = null;

		try
		{
			p = connection.prepareStatement(insert);
			p.setString(1, name.toString());
			p.executeUpdate();
		} catch (SQLException e)
		{
			e.printStackTrace();
			return;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isUsedName(String name)
	{
		String get = "SELECT * FROM names WHERE name =?";

		PreparedStatement p = null;
		try
		{
			p = this.connection.prepareStatement(get);
			p.setString(1, name);
			ResultSet res = p.executeQuery();

			while (res.next())
			{
				return name.equalsIgnoreCase(res.getString("name"));
			}
			return false;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		} finally
		{
			if (p != null)
			{
				try
				{
					p.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isConnected()
	{
		return connected;
	}
}
