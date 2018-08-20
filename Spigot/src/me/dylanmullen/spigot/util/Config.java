package me.dylanmullen.spigot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{

	private String name;
	private File f;
	private FileConfiguration cfg;

	public Config(String name, String path, boolean copy)
	{
		this.name = name;
		this.f = new File(path + "/" + name);

		load(copy);
	}

	public Config(File f2, boolean copy)
	{
		f = f2;
		name = f.getName();
		load(copy);
	}

	private void load(boolean copy)
	{
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();

		if(copy)
		{
			copyDefault(getClass().getClassLoader().getResourceAsStream(name));
		}
		else
		{
			if (!f.exists())
			{
				try
				{
					f.createNewFile();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		cfg = YamlConfiguration.loadConfiguration(f);
	}

	private void copyDefault(InputStream in)
	{
		if(f.exists())
			return;
		
		try
		{
			OutputStream out = new FileOutputStream(getFile());
			byte[] buf = new byte[1024];
			int len;
			
			while((len=in.read(buf))>0)
			{
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void save()
	{
		try
		{
			cfg.save(f);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void reload()
	{
		save();
		cfg = YamlConfiguration.loadConfiguration(f);
	}

	public String getName()
	{
		return name;
	}

	public File getFile()
	{
		return f;
	}

	public FileConfiguration getConfiguration()
	{
		return cfg;
	}

}