package me.jne.AntiAd;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import me.jne.antiad.ADCommand;
import org.bukkit.configuration.file.FileConfiguration;
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


        //Setting op the plugin listener to listen on this :) 
        getServer().getPluginManager().registerEvents(new ADListener(this), this);

        //Setting up the command Executer (antiAD)
        getCommand("antiad").setExecutor(new ADCommand(this));


        final FileConfiguration config = getConfig();
        config.addDefault("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));
        config.addDefault("Stealth-Mode", "true");
        config.options().copyDefaults(true);
        saveConfig();
        File whitelistFile = new File(getDataFolder(), "Whitelist.txt");
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, "Anti Ad error while making new whitelist file!");
            }
        }

        File logFile = new File(getDataFolder(), "Log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, "Anti Ad error while making new Log file file!");
            }
        }




        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException ex) {
            getLogger().log(Level.INFO, "Anti Ad error while Setting up metrics!");
        }

        getLogger().info(getDescription().getName() + " " + "Version" + " " + getDescription().getVersion() + " is now Enabled");

    }

    public Adfinder getAdfinder() {
        return adfinder;
    }
}
