package me.jne.AntiAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiAd extends JavaPlugin {

    private Adfinder adfinder;

   @Override
    public void onEnable() {
        adfinder = new Adfinder(this);


        //Setting op the plugin listener to listen on this :) 
        getServer().getPluginManager().registerEvents(new ADListener(this), this);

        //Setting up the command Executer (antiAD)
        getCommand("antiad").setExecutor(new ADCommand(this));

        checkConfig();
        final FileConfiguration config = getConfig();
        if(!config.contains("Detected-Commands")){
        config.addDefault("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));
        
        saveConfig();
        }
        
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
   
    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " " + "Version" + " " + getDescription().getVersion() + " is now Disabled");
    }

    public Adfinder getAdfinder() {
        return adfinder;
    }
    
    private void checkConfig() {
        String name = "config.yml";
        File actual = new File(getDataFolder(), name);
        if (!actual.exists()) {
            getDataFolder().mkdir();
            InputStream input = this.getClass().getResourceAsStream(
                    "/config.yml");
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[4096]; // [8192]?
                    int length;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    getLogger().info(" Loading the Default config: " + name);
                } catch (IOException e) {
                    getLogger().warning("Error while loading the file!!!!!!"+e.getMessage());
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {
                    }

                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}
