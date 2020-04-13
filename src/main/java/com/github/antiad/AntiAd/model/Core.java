package com.github.antiad.AntiAd.model;


import com.github.antiad.AntiAd.AntiAd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


public class Core {
    private Config config;
    private AntiAd plugin;
    private Logger logger;
    private Properties language;
    private boolean DEBUG = true;


    public Core(AntiAd plugin){
        this.plugin = plugin;
        logger = new Logger();
    }

    public Config getConfig(){
        return config;
    }

    public void createConfig(boolean spamDetection, boolean urlDetection, boolean IPDetection, boolean log, boolean checkWordLenght, boolean notifyMessage, int numbers, int procentCapital, String whitelistLineLocation, String language) {

        this.config = new Config(this, spamDetection, urlDetection, IPDetection, log, checkWordLenght, notifyMessage, numbers, procentCapital, whitelistLineLocation, language);
        setLanguage(config.getLanguage());

        config.loadWhitelist(whitelistLineLocation);
    }

    public AntiAd getPlugin() {
        return plugin;
    }

    /**
     * SHOULD ONLY BE CALLED FROM loadLANGUAGE()! Loads a language into the
     * language (properties)
     *
     * @param lang the language (needs to be checked against valid language before
     * it is going in here!
     */
    public void setLanguage(String lang) {
        language = new Properties();
        try {
            language.load(this.getClass().getClassLoader().getResourceAsStream(
                    "language/" + lang + ".properties"));
        } catch (FileNotFoundException ex) {
            plugin.getLogger().log(Level.INFO,"langugage File not found (check if you made it right) ");
        } catch (IOException ex) {
            plugin.getLogger().log(Level.INFO, "Error while setting the language", ex);
        }
    }

    public void logMessage(Level level, String message) {
        plugin.getLogger().log(level, message);
    }


    public String getMessage(String message){
        return plugin.getColorfullLanguage(message);
    }

    public void debug(String text) {
        if (DEBUG) {
            System.out.println("DEBUG"+text);
        }
    }


    public void setDebug(boolean debug) {
        this.DEBUG = debug;
    }

    public Logger getLogger() {
        return logger;
    }

    public Properties getLanguage() {
        return language;
    }
}
