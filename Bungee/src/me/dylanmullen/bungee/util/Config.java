package me.dylanmullen.bungee.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Config
{
	private String name;
	private String path;
	private File configFile;
	private Configuration config;
	private ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

	public Config(String name, String path, boolean copy)
	{
		this.name = name;
		this.path = path;
		load(copy);
	}

	public Config(String path, boolean copy)
	{
		this.name = "";
		this.path = path;
		load(copy);
	}

	private void load(boolean copy)
	{
		configFile = new File(path);
		if (!configFile.getParentFile().exists())
			configFile.getParentFile().mkdirs();

		if (copy)
		{
			copyDefault(getClass().getClassLoader().getResourceAsStream(name));
		} else
		{
			if (!configFile.exists())
			{
				try
				{
					configFile.createNewFile();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		try
		{
			config = provider.load(configFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void copyDefault(InputStream in)
	{
		if (configFile.exists())
			return;
		try
		{
			OutputStream out = new FileOutputStream(configFile);
			byte[] buf = new byte[1024];
			int len;

			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public void saveConfig()
	{
		try
		{
			provider.save(config, configFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void reloadConfig()
	{
		try
		{
			provider.save(config, configFile);
			config = provider.load(configFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String getName()
	{
		return name;
	}

	public String getPath()
	{
		return path;
	}

	public File getConfigFile()
	{
		return configFile;
	}

	public Configuration getConfig()
	{
		return config;
	}

	public ConfigurationProvider getProvider()
	{
		return provider;
	}

}