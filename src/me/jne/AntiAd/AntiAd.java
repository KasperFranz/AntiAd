package me.jne.AntiAd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiAd extends JavaPlugin {

    private Adfinder adfinder;

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " " + "Version" + " " + getDescription().getVersion() + " is now Disabled");
    }

    @Override
    public void onEnable() {
        adfinder = new Adfinder(this);
        getLogger().info(getDescription().getName() + " " + "Version" + " " + getDescription().getVersion() + " is now Enabled");
        PluginManager pm = getServer().getPluginManager();
        
        //Setting op the plugin listener to listen on this :) 
        this.getServer().getPluginManager()
                .registerEvents(new ADListener(this), this);
        final FileConfiguration config = getConfig();
        config.addDefault("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));
        config.addDefault("Stealth-Mode", "true");
        config.options().copyDefaults(true);
        saveConfig();

        File whitelistFile = new File("plugins/AntiAd/Whitelist.txt");
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, "Anti Ad error while making new whitelist file!");
            }
        }

        File logFile = new File("plugin/AntiAd/Log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e1) {
                getLogger().log(Level.WARNING, "Anti Ad error while making new whitelist file!");
            }
        }




        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException ex) {
            getLogger().log(Level.INFO, "Anti Ad error while Setting up metrics!");
        }



    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        boolean rtnbool = false;

        if (commandLabel.equalsIgnoreCase("AntiAd")) {
            if (args.length == 0) {
                rtnbool = false;
            } else if (args[0].equalsIgnoreCase("reload") && (sender.isOp() || sender.hasPermission("antiad.reload"))) {
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "AntiAd" + ChatColor.YELLOW + " configuration file reloaded!");
                rtnbool = true;
            } else if (args[0].equalsIgnoreCase("add") && ((sender.isOp() || sender.hasPermission("antiad.whitelist")))) {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "You must specify an IP/URL!");
                    rtnbool = true;
                    adfinder.loadWhitelist();
                } else {
                    String ip = args[1];
                    try {
                        BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Whitelist.txt", true));
                        write.append(ip);
                        write.newLine();
                        write.flush();
                        write.close();
                        sender.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] The URL/IP added to Whitelist!");
                        rtnbool = true;
                    } catch (IOException e) {
                        getLogger().info("AntiAid Whitelist file not found IOException!" + e.getMessage());
                        rtnbool = false;
                    }
                }

            } else {
                rtnbool = false;
            }
        }

        return rtnbool;

    }

    public Adfinder getAdfinder() {
        return adfinder;
    }
}
