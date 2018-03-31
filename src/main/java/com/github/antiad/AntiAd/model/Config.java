package com.github.antiad.AntiAd.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class Config {
    private boolean spamDetection, urlDetection, IPDetection, checkWordLenght, notifyMessage, log ;
    private int numbers, procentCapital;
    private ArrayList<String> whitelistLine, whitelistWildCardList;
    private String language;

    public Config(boolean spamDetection, boolean urlDetection, boolean IPDetection, boolean log, boolean checkWordLenght, boolean notifyMessage, int numbers, int procentCapital, String whitelistLineLocation, String language) {
        this.spamDetection = spamDetection;
        this.urlDetection = urlDetection;
        this.IPDetection = IPDetection;
        this.log = log;
        this.checkWordLenght = checkWordLenght;
        this.notifyMessage = notifyMessage;
        this.numbers = numbers;
        this.procentCapital = procentCapital;
        whitelistLine = new ArrayList<>();
        whitelistWildCardList = new ArrayList<>();
        this.language = language;
        Core.instance().setLanguage(getLanguage());
    }

    public boolean isSpamDetection() {
        return spamDetection;
    }

    public boolean isUrlDetection() {
        return urlDetection;
    }

    public boolean isIPDetection() {
        return IPDetection;
    }

    public boolean isCheckWordLenght() {
        return checkWordLenght;
    }

    public boolean isNotifyMessage() {
        return notifyMessage;
    }

    public int getNumbers() {
        return numbers;
    }

    public int getProcentCapital() {
        return procentCapital;
    }

    public ArrayList<String> getWhitelistLine() {
        return whitelistLine;
    }

    public ArrayList<String> getWhitelistWildCardList() {
        return whitelistWildCardList;
    }

    public String getLanguage() {
        return language;
    }


    public boolean getLog() {
        return log;
    }

    /**
     * Method to load the whitelist This is at start of if we add it with the
     * command (or reload)
     */
    public void loadWhitelist(String location) {

        try {
            BufferedReader read = new BufferedReader(new FileReader(location));
            String line;
            while ((line = read.readLine()) != null) {
                whitelistAdd(line);
            }

        } catch (IOException ex) {
            Core.instance().logMessage(
                Level.WARNING,
                Core.instance().getMessage("whitelistNotFound")
            );
        }
    }

    public void whitelistAdd(String line) {
        if (line.startsWith("*") || line.endsWith("*")) {
            whitelistWildCardList.add(line);
        } else {
            whitelistLine.add(line);
        }
    }

}

