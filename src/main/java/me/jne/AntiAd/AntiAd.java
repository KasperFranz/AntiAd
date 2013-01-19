package me.jne.AntiAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiAd extends JavaPlugin {

    private Adfinder adfinder;

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " " + "Version" + " " + getDescription().getVersion() + " is now Disabled");
    }

    @Override
    public void onEnable() {
        doConfigThings();
        adfinder = new Adfinder(this);


        //Setting op the plugin listener to listen on this :) 
        getServer().getPluginManager().registerEvents(new ADListener(this), this);

        //Setting up the command Executer (antiAD)
        getCommand("antiad").setExecutor(new ADCommand(this));





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
/**
 * Get the adfinder :)
 * @return  The adfiner in this plugin!
 */
    public Adfinder getAdfinder() {
        return adfinder;
    }
/**
 * This loads the config file and see if it misses any commands if it does it add them (also here we add new ones)!
 */
    private void doConfigThings() {
        //We checks if the config exsists
        File configFile;
        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }


        // We check if the config contains this new ones, if it dosn't we add them.
        if (!getConfig().contains("Detected-Commands")) {
            getConfig().set("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));
            System.out.println("we are saving!");
            saveConfig();
        }else{
            System.out.println("contained dected");
        }
        if (!getConfig().contains("Stealth-Mode")) {
            getConfig().set("Stealth-Mode", "true");
            System.out.println("we are saving!");
            saveConfig();
        }else{
            System.out.println("contained Stelath");
        }
    }
/**
 * copying one file to another!
 * @param resource the file you want to copy from
 * @param configFile the file you want to copy to.
 */
    private void copy(InputStream resource, File configFile) {
           if (resource != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(configFile);
                    byte[] buf = new byte[4096];
                    int length;
                    while ((length = resource.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    getLogger().info("[AntiAD] Loading the Default config: config.yml");
                }catch (IOException e) {
                   getLogger().warning("Error while loading the file!!!!!!"+e.getMessage());
                } 
           }
    }
}
