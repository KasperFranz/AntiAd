package me.jne.AntiAd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for this bukkit plugin :)
 * @author Franz
 */
public class AntiAd extends JavaPlugin {

    private Adfinder adfinder;
    private Properties language;

    
    /**
     *The enable method for the plugin.
     */
    @Override
    public void onEnable() {
        loadLanguage();
        adfinder = new Adfinder(this);


        //Setting op the plugin listener to listen on this :) 
        getServer().getPluginManager().registerEvents(new ADListener(this), this);

        //Setting up the command Executer (antiAD)
        getCommand("antiad").setExecutor(new ADCommand(this));

        checkConfig();
        final FileConfiguration config = getConfig();
        if (!config.contains("Detected-Commands")) {
            config.addDefault("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));

            saveConfig();
        }

        File whitelistFile = new File(getDataFolder(), "Whitelist.txt");
        if (!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException ex) {
                getLogger().warning(language.getProperty("ERRORWhitelist"));
            }
        }

        File logFile = new File(getDataFolder(), "Log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                getLogger().warning(language.getProperty("ERRORLog"));
            }
        }




        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException ex) {
            getLogger().info(language.getProperty("ERRORMetrics"));
        }

        getLogger().info(language.getProperty("enable").replaceAll("%PLUGIN%", getDescription().getName()).replaceAll("%VERSION%", getDescription().getVersion()));

    }

    /**
     *the disable method for the plugin
     */
    @Override
    public void onDisable() {
        getLogger().info(language.getProperty("disable").replaceAll("%PLUGIN%", getDescription().getName()).replaceAll("%VERSION%", getDescription().getVersion()));
    }

    /**
     *
     * @return the adfinder of the plugin (to check for spam, advertising and so on)
     */
    public Adfinder getAdfinder() {
        return adfinder;
    }
    /**
     *
     * @return the properties file we are currently using (default is en.properties)
     */
    public Properties getLanguage() {
        return language;
    }
    /**
     * Private method to check if the config is there if not we make it from the default.
     */
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
                    byte[] buf = new byte[4096];
                    int length;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    getLogger().info(language.getProperty("usingDefaultConfig"));
                } catch (IOException e) {
                    getLogger().warning(language.getProperty("ERRORLoadingDeafultConfig"));
                }
            }
        }
    }
/**
 * Method to load the language (properties file) from the config 
 */
    private void loadLanguage() {

        ArrayList<String> validLanguage = validLanguage();
        if (getConfig().contains("Language")) {
            String tempLang = getConfig().getString("Language");
            if (validLanguage.contains(tempLang)) {
                setLanguage(tempLang);
            } else {
                setLanguage("en");
                getLogger().warning(language.getProperty("WrongLangugage"));
            }

        } else {

            setLanguage("en");
        }
    }

    /**
     * SHOULD ONLY BE CALLED FROM loadLANGUAGE()! Loads a langugage into the
     * language (properties)
     *
     * @param lang the languge (needs to be checked agains valid language before
     * it is going in here!
     */
    private void setLanguage(String lang) {
        language = new Properties();
        try {

            language.load(this.getClass().getClassLoader().getResourceAsStream(
                    "language/" + lang + ".properties"));
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    /**
     * Method to get all the valid languages!
     *
     * @return the list of valid language!
     */
    private ArrayList<String> validLanguage() {
        ArrayList<String> validLanguage = new ArrayList<String>();
        validLanguage.add("en");
        return validLanguage;
    }
    /**
     * Method for making the text colorfull by the 
     * @param text the text there should be made colorfull
     * @return the text with chatColor.
     */
    public String colorfull(String text){
        return text.replaceAll("&([a-f0-9])", ChatColor.COLOR_CHAR + "$1");
    }
}
