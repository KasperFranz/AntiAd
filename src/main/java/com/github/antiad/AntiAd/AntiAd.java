package com.github.antiad.AntiAd;;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

/**
 * The main class for this bukkit plugin :)
 * @author Franz
 */
public class AntiAd extends JavaPlugin {
    private boolean DEBUG = false;
    private Adfinder adfinder;
    private Properties language;

    /**
     * The enable method for the plugin.
     */
    @Override
    public void onEnable() {
        loadLanguage();
       


        //Setting op the plugin listener to listen on this :)
        getServer().getPluginManager().registerEvents(new ADListener(this), this);

        //Setting up the command Executer (antiAD)
        getCommand("antiad").setExecutor(new ADCommand(this));

        checkConfig();

        if (!getConfig().contains("Detected-Commands")) {
            getConfig().addDefault("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));

            saveConfig();
        }

        checkFile("Whitelist.txt", "ERRORWhitelistCreate");
        checkFile("Log.txt", "ERRORLogCreate");
        adfinder = new Adfinder(this);
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().info(getFromLanguage("ERRORMetrics"));
        }

        getLogger().info(getFromLanguage("enable").replaceAll("%PLUGIN%", getDescription().getName()).replaceAll("%VERSION%", getDescription().getVersion()));

    }

    /**
     * the disable method for the plugin
     */
    @Override
    public void onDisable() {
        getLogger().info(language.getProperty("disable").replaceAll("%PLUGIN%", getDescription().getName()).replaceAll("%VERSION%", getDescription().getVersion()));
    }

    /**
     *
     * @return the adfinder of the plugin (to check for spam, advertising and so
     * on)
     */
    public Adfinder getAdfinder() {
        return adfinder;
    }

    /**
     *
     * @return the properties file we are currently using (default is
     * en.properties)
     */
    public Properties getLanguage() {
        return language;
    }

    /**
     * Private method to check if the config is there if not we make it from the
     * default.
     */
    private void checkConfig() {
        final File actual = new File(getDataFolder(), "config.yml");
        if (!actual.exists()) {
            getDataFolder().mkdir();
            final InputStream input = this.getClass().getResourceAsStream(
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

        final ArrayList<String> validLanguage = validLanguage();
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
            getLogger().info("langugage File not found (check if you made it right) ");
        } catch (IOException ex) {
            getLogger().log(Level.INFO, "Error while setting the language", ex);
        }
    }

    /**
     * Method to get all the valid languages!
     *
     * @return the list of valid language!
     */
    private ArrayList<String> validLanguage() {
        ArrayList<String> validLanguage = new ArrayList<String>(1);
        validLanguage.add("en");
        validLanguage.add("pl");
        validLanguage.add("de");
        validLanguage.add("es");
        validLanguage.add("fr");
        validLanguage.add("da");
        validLanguage.add("ru");
        validLanguage.add("tr");
        validLanguage.add("cn");
        return validLanguage;
    }

    /**
     * Method for making the text colorfull by the
     *
     * @param text the text there should be made colorfull
     * @return the text with chatColor.
     */
    public String colorfull(String text) {
        try{
        text =  text.replaceAll("&([a-f0-9])", ChatColor.COLOR_CHAR + "$1");
        debug(text+ "colorfull :)");
        }catch(Exception ex){
        }
        return text;
    }
    
    public String uncolorfull(String text){
        try{
           text = text.replaceAll("&([a-f0-9])", "");
        }catch(Exception ex){
           
        }
        return text;
    }

    /**
     * SOUT debug if this is debug!
     *
     * @param text the debug text
     */
    public void debug(String text) {
        if (DEBUG) {
            System.out.println("DEBUG"+text);
        }
    }

    /**
     * *
     * Colorize the text from the language
     *
     * @param propeuy the property you want from the language.
     * @return the colorfull text :)
     */
    public String getColorfullLanguage(String property) {
        String text = getLanguage().getProperty(property);
        String returnstr = "";
        if(text != null){
            returnstr = colorfull(text);
        }
        return returnstr;
    }
    
    /**
     * Get the language without any colorfull etc.
     * @param property
     * @return 
     */
    public String getFromLanguage(String property){
        String text = getLanguage().getProperty(property);
        String returnstr = "";
         if(text != null){
            returnstr = uncolorfull(text);
        }
        return returnstr;
    }
    
    public String getFromLanguageAndTag(String property){
        return uncolorfull(getLanguage().getProperty("PluginTag") + getLanguage().getProperty(property));
    }

    /**
     * Returns the colorfull message included with a pluginTag at the begining
     *
     * @param property
     * @return
     */
    String getColorfullLanguageAndTag(String property) {
        
        return colorfull(getLanguage().getProperty("PluginTag") + getLanguage().getProperty(property));
    }


    /**
     *
     * @param fileName the filename you wanna check
     * @param errorMessage the error message you wanna send if there was a
     * error. (the name from the language file)
     */
    private void checkFile(String fileName, String errorMessage) {
        final File fileToCheck = new File(getDataFolder(), fileName);
        if (!fileToCheck.exists()) {
            try {
                fileToCheck.createNewFile();
            } catch (IOException ex) {
                getLogger().warning(getFromLanguage(errorMessage)+ " "+ex);
            }
        }
    }
}
