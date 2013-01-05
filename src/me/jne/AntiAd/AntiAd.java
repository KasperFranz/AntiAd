package me.jne.AntiAd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiAd extends JavaPlugin {

	public final Logger logger = Logger.getLogger("Minecraft");	
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " " + "Version" + " " + pdfFile.getVersion() + " is now Disabled");
	}
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " " + "Version" + " " + pdfFile.getVersion() + " is now Enabled");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new Adfinder(this),this);
		pm.registerEvents(new Join(this), this);
        final FileConfiguration config = this.getConfig();
        config.addDefault("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));
        config.addDefault("Stealth-Mode", "true");
		config.options().copyDefaults(true);
		saveConfig();
	        File pluginfile = new File("plugins/AntiAd/Whitelist.txt");
	        if (!pluginfile.exists()){
	            try {
	                pluginfile.createNewFile();
	            } catch (IOException e) {
	                e.printStackTrace();
	        File pluginFile = new File("plugin/AntiAd/Log.txt");
	        if(!pluginFile.exists()){
	        	try{
	        	pluginFile.createNewFile();
	        	}catch (IOException e1) {
	        		e.printStackTrace();
	        	}
	        }
	    }
	}
	        try {
	            MetricsLite metrics = new MetricsLite(this);
	            metrics.start();
	        } catch (IOException e) {
	        	
	        }

	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("AntiAd")){
			if(args.length == 0){
				return false;
			}else if(args[0].equalsIgnoreCase("reload")){
				if(sender.isOp() || sender.hasPermission("antiad.reload")){
					this.reloadConfig();
					sender.sendMessage(ChatColor.GREEN + "AntiAd" + ChatColor.YELLOW + " configuration file reloaded!");
				return true;
				}
			}else if(args[0].equalsIgnoreCase("add")){
				if(sender.isOp() || sender.hasPermission("antiad.whitelist")){
				if(args.length < 2){
					sender.sendMessage(ChatColor.RED + "You must specify an IP/URL!");
					return true;
				}else{
				String ip = args[1];
				try{
				BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Whitelist.txt", true));
				write.append(ip);
				write.newLine();
				write.flush();
				write.close();
				sender.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] The URL/IP added to Whitelist!");
				return true;
				}catch (IOException e){
					Bukkit.getServer().getLogger().info("File not found IOException!");
				}
			}
		}
	}else{
		return false;
	}
		}
		return false;
		
	}
}
