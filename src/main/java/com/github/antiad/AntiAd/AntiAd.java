package com.github.antiad.AntiAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import com.github.antiad.AntiAd.model.Core;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class for this bukkit plugin :)
 * @author Franz
 */
public class AntiAd extends JavaPlugin {
    private Adfinder adfinder;
    private Core core;

    /**
     * This enables this plugin :) 
     */
    @Override
    public void onEnable() {
        core = new Core(this);
        createConfigAndAttact();




        adfinder = new Adfinder(this);
        //Setting op the plugin listener to listen on this :)
        getServer().getPluginManager().registerEvents(new ADListener(this), this);

        //Setting up the command Executer (antiAD)
        getCommand("antiad").setExecutor(new ADCommand(this));

        checkConfig();

        if (!getConfig().contains("Detected-Commands")) {
            getConfig().addDefault("Detected-Commands", Arrays.asList("/msg", "/message", "/tell"));
        
            saveConfig();
        }
		
		
        if (!getConfig().contains("replaceText.caps")) {
            getConfig().addDefault("replaceText.caps", false);
            saveConfig();
        }
		
		
        if (!getConfig().contains("replaceText.advertisement")) {
            getConfig().addDefault("replaceText.advertisement", false);
            saveConfig();
        }
        
         if (getConfig().contains("debug")) {
            this.core.setDebug(this.getConfig().getBoolean("debug"));
            saveConfig();
        }

        checkFile("Whitelist.txt", "ERRORWhitelistCreate");
        checkFile("Log.txt", "ERRORLogCreate");


        getLogger().info(getFromLanguage("enable").replaceAll("%PLUGIN%", getDescription().getName()).replaceAll("%VERSION%", getDescription().getVersion()));
        Update update = new Update(this,52014);
    }

    /**
     * the disable method for the plugin
     */
    @Override
    public void onDisable() {
        getLogger().info(Core.instance().getLanguage().getProperty("disable").replaceAll("%PLUGIN%", getDescription().getName()).replaceAll("%VERSION%", getDescription().getVersion()));
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
                    getLogger().info(Core.instance().getLanguage().getProperty("usingDefaultConfig"));
                } catch (IOException e) {
                    getLogger().warning(Core.instance().getLanguage().getProperty("ERRORLoadingDeafultConfig"));
                }
            }
        }
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
        }catch(Exception ex){}
        
        return text;
    }
    
    public String uncolorfull(String text){
        try{
           text = text.replaceAll("&([a-f0-9])", "");
        }catch(Exception ex){}
        
        return text;
    }


    /**
     * *
     * Colorize the text from the language
     *
     * @param property the property you want from the language.
     * @return the colorfull text :)
     */
    public String getColorfullLanguage(String property) {
        String text = Core.instance().getLanguage().getProperty(property);
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
        String text = Core.instance().getLanguage().getProperty(property);
        String returnstr = "";
         if(text != null){
            returnstr = uncolorfull(text);
        }
        return returnstr;
    }
    
    public String getFromLanguageAndTag(String property){
        Properties language = Core.instance().getLanguage();
        return uncolorfull(language.getProperty("PluginTag") + language.getProperty(property));
    }

    /**
     * Returns the colorfull message included with a pluginTag at the begining
     *
     * @param property
     * @return
     */
    public String getColorfullLanguageAndTag(String property) {
        Properties language = Core.instance().getLanguage();
        return colorfull(language.getProperty("PluginTag") + language.getProperty(property));
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

    public void createConfigAndAttact(){
        String language = getConfig().getString("language","en");
        boolean spamDetection = getConfig().getBoolean("Spam-Detection");
        boolean urlDetection = getConfig().getBoolean("URL-Detection");
        boolean IPDetection = getConfig().getBoolean("IP-Detection");
        boolean checkWordLenght = getConfig().getBoolean("Spam-Number-Letters-check");
        boolean log = getConfig().getBoolean("log");
        boolean notifyMessage = getConfig().getBoolean("Notification-Message");
        int numbers = getConfig().getInt("Spam-Number-Letters");
        int procentCapital = getConfig().getInt("Spam-Procent-Capital-Words");
        core.createConfig(spamDetection, urlDetection, IPDetection, log, checkWordLenght, notifyMessage, numbers, procentCapital, "plugins/Core/Whitelist.txt", language);
    }

    public Core getCore(){
        return core;
    }

    void reload() {
        this.reloadConfig();
        this.adfinder.startUp();
    }
}
