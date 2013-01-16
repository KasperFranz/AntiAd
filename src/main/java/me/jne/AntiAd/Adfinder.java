package me.jne.AntiAd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Adfinder {

    private AntiAd plugin;
    private Pattern ipPattern, webpattern, spamPattern;
    private HashMap<Player, Integer> warn;
    private boolean urlDetection, spamDetection;
    private ArrayList<String> lines;

    public Adfinder(AntiAd instance) {
        lines = new ArrayList<String>();
        plugin = instance;
        loadWhitelist();
        spamDetection = plugin.getConfig().getBoolean("Spam-Detection");
        urlDetection = plugin.getConfig().getBoolean("URL-Detection");
        warn = new HashMap<Player, Integer>();

        spamPattern = Pattern.compile("((\\S{20,})|([A-Z]{3,}\\s){3,})");
        ipPattern = Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
        webpattern = Pattern.compile("(http://)|(https://)?(www)?\\S{2,}((\\.com)|(\\.net)|(\\.org)|(\\.co\\.uk)|(\\.tk)|(\\.info)|(\\.es)|(\\.de)|(\\.arpa)|(\\.edu)|(\\.firm)|(\\.int)|(\\.mil)|(\\.mobi)|(\\.nato)|(\\.to)|(\\.fr)|(\\.ms)|(\\.vu)|(\\.eu)|(\\.nl)|(\\.us)|(\\.dk))");
    }

    /**
     * Command for checking if it is spam or advertising.
     *
     * @param player the player there issued this.
     * @param message the message the user sent!
     * @param type The type of where it is written (1 chat, 2 msg, 3 sign!
     * @return true if it is spam/advertising and else false.
     */
    public boolean check(Player player, String message, int type) {
        boolean rtnbool = false;

        if (checkForAdvertising(player, message)) {
            sendWarning(player, message, 1, type);
            rtnbool = true;
        } else {
            //check for spam
            if (spamDetection) {
                if (checkForSpam(player, message)) {
                    sendWarning(player, message, 1, type);
                    rtnbool = true;
                }
            }
        }


        return rtnbool;

    }

    /**
     * Check if the mesage is a spam or not!
     *
     * @param player the player executing the message
     * @param message the message to check for ads!
     * @return true or false depending on if it is spam or not!
     */
    private boolean checkForSpam(Player player, String message) {
        boolean spam = false;
        String spamnum = plugin.getConfig().getString("Spam-Number-Letters");
        String spamletterword = plugin.getConfig().getString("Spam-Number-Letters-Word");
        String spamnumberword = plugin.getConfig().getString("Spam-Number-Words");
        //Pattern spamPattern = Pattern.compile("(\\S{" + Pattern.quote(spamnum) + ",}) || (([A-Z]{" + Pattern.quote(spamletterword) + ",}\\s){" + Pattern.quote(spamnumberword) + ",})");
        final Pattern spamPatterns = Pattern.compile("((\\S{" + Pattern.quote(spamnum) + ",})|([A-Z]{" + Pattern.quote(spamletterword) + ",}\\s){" + Pattern.quote(spamnumberword) + ",})");
        if (spamPatterns.matcher(message).find()) {
            if (!player.hasPermission("antiad.bypass.spam")) {
                spam = true;
            }
        }
        return spam;
    }

    private boolean checkForAdvertising(Player player, String message) {
        boolean advertising = false;


        // CHECK FOR IP PATTERN
        Matcher regexMatcher = ipPattern.matcher(message);
        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                
                if (!lines.contains(regexMatcher.group().trim())) {
                    if (ipPattern.matcher(message).find() && !player.hasPermission("antiad.bypass.ad")) {
                        advertising = true;
                    }
                }
            }
        }

        if (!advertising) {
            Matcher regexMatcherurl = webpattern.matcher(message);

            while (regexMatcherurl.find()) {
                if (regexMatcherurl.group().length() != 0) {
                    for (int i = 0; i < lines.size(); i++) {
                        System.out.println(lines.get(i) +" this is what we found "+ regexMatcherurl.group().trim()+ "EX: "+regexMatcherurl.group());
                        
                    }
                    if (!lines.contains(regexMatcherurl.group().trim())) {
                        if (webpattern.matcher(message).find()) {
                            if (urlDetection && !player.hasPermission("antiad.bypass.ad")) {
                                advertising = true;
                            }
                        }

                    }
                }
            }
        }

        return advertising;
    }

    public void log(String message) {
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Log.txt", true));
            write.append(message);
            write.newLine();
            write.flush();
            write.close();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "AntiAD error while saving message on the log file msg:" + ex.getMessage());
        }
    }

    /**
     *
     * @param message the message the player has send!
     * @param player the player there sent the message!
     * @param type 1 for AD 2 for spam. (gets logged)
     */
    private void sendWarning(Player player, String message, int type, int where) {

        log(now("MMM dd,yyyy HH:mm ") + player.getDisplayName() + " has +" + typeToX(type, 1) + ": " + message + ", in " + whereToTXT(where) + ".");
        Bukkit.getServer().getLogger().info("[AntiAd] " + player.getDisplayName() + " was logged for " + typeToX(type, 2) + " in " + whereToTXT(where) + ".");
        if (!warn.containsKey(player)) {
            warn.put(player, 0);
        }
        if (warn.get(player) < 2) {
            warn.put(player, warn.get(player) + 1);
            player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!");
            player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + typeToX(type, 3));
        } else {
            takeAction(player, type);
        }

        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            if (players.hasPermission("antiad.see")) {
                players.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + player.getDisplayName() + ChatColor.DARK_GREEN + " has " + typeToX(type, 4) + message);
            }
        }
    }

    /**
     * When the player hits the 3 warning!
     *
     * @param player the player the action is going agenst!
     * @param type what type 1 = ad 2 = spam
     */
    private void takeAction(Player player, int type) {
        String command;

        switch (type) {
            case 1:
                command = plugin.getConfig().getString("Command-Ad");
                break;
            case 2:
                command = plugin.getConfig().getString("Command-Spam");
                break;
            default:
                command = "";

                break;
        }
        command = command.replaceAll("<player>", player.getDisplayName()).replaceAll("<time>", plugin.getConfig().getString("Time"));
        warn.remove(player);
        if (!player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command)) {
            plugin.getServer().broadcastMessage("error while trying to execute cmd!");
        }

        if (plugin.getConfig().getBoolean("Notification-Message")) {
            plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + player.getDisplayName() + ChatColor.DARK_GREEN + " has been " + getActionType(command) + " for " + typeToX(type, 2));
            plugin.getServer().broadcastMessage(command);
        }


    }

    /**
     * This is a help method for TakeAction to say why the user have been
     * kicked/tempban and so on.
     *
     * @param command the command to execute
     * @return the nice edition of the type.
     */
    private String getActionType(String command) {
        String actionType;
        String[] strings = command.split("\\s+");
        if (strings[0].endsWith("n")) {
            actionType = strings[0] + "ned";
        } else if (strings[0].endsWith("e")) {
            actionType = strings[0] + "d";

        } else {
            actionType = strings[0] + "ed";
        }
        return actionType;
    }

    /**
     *
     * @param type the type of thing (1 advertising, 2 spamming)
     * @param to 1 spammed, 2 spamming 3 msg from config!, 4 spammed
     * @return the text :)
     */
    private String typeToX(int type, int to) {
        String rtnString;

        if (type == 1) {

            switch (to) {
                case 1:
                    rtnString = "advertised";
                    break;
                case 2:
                    rtnString = "advertising";
                    break;
                case 3:
                    rtnString = plugin.getConfig().getString("Ad_Message");
                    break;
                case 4:
                    rtnString = "advertised";
                    break;
                default:
                    rtnString = " ";
                    break;
            }
        } else {
            switch (to) {
                case 1:
                    rtnString = "spammed";
                    break;
                case 2:
                    rtnString = "spamming";
                    break;
                case 3:
                    rtnString = plugin.getConfig().getString("Spam_Message");
                    break;
                case 4:
                    rtnString = "spammed";
                    break;
                default:
                    rtnString = " ";
                    break;
            }

        }
        return rtnString;
    }

    /**
     * Method to load the whitelist in again! (if changed)
     */
    public void loadWhitelist() {

        try {
            BufferedReader read = new BufferedReader(new FileReader("plugins/AntiAd/Whitelist.txt"));

            lines = new ArrayList<String>();

            try {
                String line;
                while ((line = read.readLine()) != null) {
                    lines.add(line);

                }
            } catch (IOException ex) {
                plugin.getLogger().log(Level.WARNING, "error while loading whittelist " + ex.getMessage());
            }
        } catch (FileNotFoundException ex) {
            plugin.getLogger().log(Level.WARNING, "error while loading file " + ex.getMessage());
        }
    }

    /**
     *
     * @param where The type of where it is written (1 chat, 2 msg, 3 sign!
     * @return returns where it was executed.
     */
    private String whereToTXT(int where) {
        String wheres = "Unknown";
        switch (where) {
            case 1:
                wheres = "chat";
                break;
            case 2:
                wheres = "command";
                break;
            case 3:
                wheres = "sign";
                break;
        }
        return wheres;

    }

    public static String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());


    }
}