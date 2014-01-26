package com.github.antiad.AntiAd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Adfinder {

    private AntiAd plugin;
    // ip pattern http://regexr.com?33l17
    private final Pattern ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[.,-:; ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
    // web pattern http://regexr.com?36elv
    private final Pattern webpattern = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+~#?&//=]*)?");
    private HashMap<Player, Integer> warn;
    private boolean urlDetection, spamDetection, IPDetection, checkWordLenght;
    private int numbers, procentCapital;
    private ArrayList<String> whitelistLine;

    public Adfinder(AntiAd instance) {
        plugin = instance;
        loadWhitelist();
        startUp();
    }

    /**
     * Command for checking if it is spam or advertising.
     *
     * @param player the player there issued this.
     * @param message the message the user sent!
     * @param type The type of where it is written (1 chat, 2 msg, 3 sign!
     * @return true if it is spam/advertising and else false.
     */
    public boolean check(Player player, String message, int type, boolean checkForSpam) {
        message = Normalizer.normalize(message, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        boolean rtnbool = false;
        int ad = 0;
        plugin.debug("We are testing player"+player.getName() + "Msg: "+message + "type:"+ type + "checkSpam"+ checkForSpam);
        // if the player hasn't permission the bypass advertising then we check if for advertising.
        if (!player.hasPermission("antiad.bypass.ad")) {
            plugin.debug("Checking for advertising.");
            ad = checkForAdvertising(message);

        }

        if (ad == 1) {
            sendWarning(player, message, 1, type);
            rtnbool = true;
        } else if (ad == 0) {
            //if it's not advertising then check for spam
            if (spamDetection && checkForSpam && !player.hasPermission("antiad.bypass.spam")) {
                if (checkForSpam(message)) {
                    sendWarning(player, message, 2, type);
                    rtnbool = true;
                }
            }
        }
        return rtnbool;
    }

    /**
     * Check if the mesage is a spam or not!
     *
     * @param message the message to check for ads!
     * @return true or false depending on if it is spam or not!
     */
    private boolean checkForSpam(String message) {
        boolean spam = false;

        String[] words = message.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (checkWordLenght && words[i].length() >= numbers) {
                //Checks if the message is longer than the max allowed, it only does this if the config allows it.
                plugin.debug("this is marked as spam because " + words[i].length() + ">=" + numbers);
                spam = true;
                i = words.length; // we sets the i to max so it doesn't run more.
                // if the word is 4 or under && it
            } else if (words[i].length() >= 4 && words[i].equals(words[i].toUpperCase()) && !isNumbers(words[i]) && procentCapital != 0) {
                plugin.debug("else if 1");
                spam = true;
                i = words.length;
                //if the words is longer than or 4 long
            } else if (words[i].length() >= 4 &&  procentCapital != 0) {

                int upper = 0;
                char[] charArray = words[i].toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    String letter = charArray[j] + "";
                    if (letter.equals(letter.toUpperCase()) && !isNumbers(letter)) {
                        upper++;
                    }



                }

                if (upper * 100 / charArray.length * 100 >= procentCapital * 100) {
                    plugin.debug("else if 2");
                    spam = true;
                    i = words.length;
                }
            }

        }


        return spam;
    }

    /**
     * a Method to check if it's advertising, this methods call
     * checkForIPPattern and checkForWebPattern but only if it's allowed in the
     * config.
     *
     * This uses int because if it's adverting there are on the whitelist then
     * it shouldn't check for spam.
     *
     * @param message the message you want to check for
     * @return 1 if advertising, and 2 if it's advertingsing there are on the
     * whitelist.
     */
    private int checkForAdvertising(String message) {
        int advertising = 0;
        // CHECK FOR IP PATTERN if it's turned on.
        if (IPDetection) {
            advertising = checkForIPPattern(message);
            plugin.debug("Checking for IP");
        }
        //if it marks it as advertising on the IP pattern, we don't want to check if for the web pattern
        if (advertising == 0 && urlDetection) {
            plugin.debug("Checking for web");
            advertising = checkForWebPattern(message);
        }

        return advertising;
    }

    /**
     * Saves to the log!
     *
     * @param message
     */
    public void log(String message) {
        plugin.debug("Begin to log:"+message);
        try {
            BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Log.txt", true));
            write.append(message);
            write.newLine();
            write.flush();
            write.close();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, plugin.getFromLanguage("ERRORLogSave").replace("%MESSAGE%", ex.getMessage()));
        }
    }

    /**
     *
     * @param message the message the player has send!
     * @param player the player there sent the message!
     * @param type 1 for AD 2 for spam. (gets logged)
     */
    private void sendWarning(Player player, String message, int type, int where) {
        plugin.debug("SENDING WARNING!!!!");
        //First we gonna warn the admins (ops) about the player and what he chatted. 
        if (type == 1 && plugin.getConfig().getBoolean("AdWarnAdmins") || (type == 2 && plugin.getConfig().getBoolean("SpamWarnAdmins"))) {
            Set<OfflinePlayer> tempOps = Bukkit.getServer().getOperators();
            OfflinePlayer[] ops = tempOps.toArray(new OfflinePlayer[tempOps.size()]);
            for (int i = 0; i < ops.length; i++) {
                if (ops[i].isOnline()) {
                     ops[i].getPlayer().sendMessage(plugin.getColorfullLanguageAndTag("logWarning").replace("%PLAYER%", player.getDisplayName()).replace("%TYPE%", typeToX(type, 1)).replace("%WHERE%", whereToTXT(where)).replace("%MESSAGE%", message));
                }
            }
        }

        // Start logging and sending the warning
        log(now("MMM dd,yyyy HH:mm ") + plugin.getFromLanguage("privatLogWarning")
                .replace("%PLAYER%", player.getDisplayName())
                .replace("%TYPE%", typeToX(type, 1))
                .replace("%MESSAGE%", message)
                .replace("%WHERE%", whereToTXT(where)));
            
        Bukkit.getServer().getLogger().info(plugin.getFromLanguageAndTag("logWarning").replace("%PLAYER%", player.getDisplayName()).replace("%TYPE%", typeToX(type, 2)).replace("%WHERE%", whereToTXT(where)).replace("%MESSAGE%", message));
        //adding a warning to the player.
        int warnings = 1;
        // if we know the player we gonna remove him and count the warnings up.
        if(warn.containsKey(player)){
            warnings = warn.get(player) + 1;
            warn.remove(player);
        }
        warn.put(player,warnings);
        
        int maxWarnings = plugin.getConfig().getInt("warnings");
        
        // begin sending the warning to the player
        if(warn.get(player) >= maxWarnings){
     // if he is at max Warnings we gonna take action
            takeAction(player, type);
        } else {
            player.sendMessage(plugin.getColorfullLanguageAndTag("chancesLeft").replace("%WARNINGS%", warn.get(player) + "").replace("%CHANCES%", maxWarnings+""));
            player.sendMessage(plugin.getColorfullLanguageAndTag("PlayerWarningFor").replace("%REASON%", typeToX(type, 3)));
        }

        // if the player got permission to see what happend we gonna msg them the things.
        for (Player players : Bukkit.getServer().getOnlinePlayers()) {
            if (players.hasPermission("antiad.see")) {
                players.sendMessage(plugin.getColorfullLanguageAndTag("publicMessage").replace("%PLAYER%", player.getDisplayName()).replace("%TYPE%", typeToX(type, 4)).replace("%MESSAGE%", message));
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
                command = plugin.getConfig().getString("Command-Ad").replaceAll("<reasonad>", typeToX(1, 3)).replaceAll("<time>", plugin.getConfig().getString("Time"));
                break;
            case 2:
                command = plugin.getConfig().getString("Command-Spam").replaceAll("<reasonspam>", typeToX(2, 3));
                ;
                break;
            default:
                command = "";

                break;
        }
        String broadcastMessage = plugin.getColorfullLanguageAndTag("PlayerActionTaken").replace("%PLAYER%", player.getDisplayName()).replace("%ACTION%", getActionType(command)).replace("%FOR%", typeToX(type, 2));
        command = command.replaceAll("<player>", player.getName()).replaceAll("<time>", plugin.getConfig().getString("Time"));
        warn.remove(player);
        plugin.getServer().getScheduler().runTask(plugin, new AdfinderAction(command, plugin, broadcastMessage));



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
        } else if (type == 2) {
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

        } else {
            rtnString = "unknow";
        }
        
        
        return rtnString;
    }

    /**
     * Method to load the whitelist This is at start of if we add it with the
     * command (or reload)
     */
    public void loadWhitelist() {

        try {
            BufferedReader read = new BufferedReader(new FileReader("plugins/AntiAd/Whitelist.txt"));
            whitelistLine = new ArrayList<String>();
            String line;
            while ((line = read.readLine()) != null) {
                whitelistLine.add(line);
            }

        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, plugin.getColorfullLanguage("whitelistNotFound"));
        }
    }
    
    public void whitelistAdd(String url){
         whitelistLine.add(url);
    }

    /**
     * A method to change the where int to a String.
     *
     * @param where The type of where it is written (1 chat, 2 msg, 3 sign!
     * @return returns where it was executed, if found (1-3 is allowed values
     * atm.)
     */
    private String whereToTXT(int where) {
        String returnString = "Unknown";
        switch (where) {
            case 1:
                returnString = "chat";
                break;
            case 2:
                returnString = "command";
                break;
            case 3:
                returnString = "sign";
                break;
        }
        return returnString;

    }

    /**
     * A Help method to get the current time-
     *
     * @param dateFormat the number format you want back ex. MMM dd,yyyy HH:mm
     * @return the String of the calendar in the dateFormat you wanted.
     */
    public static String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());


    }

    /**
     * A help Method to check if the input are numbers.
     *
     * @param input the text/int you want to check if is numbers.
     * @return true/false depending on if it's numbers or not.
     */
    private boolean isNumbers(String input) {
        boolean rtnbool = false;
        try {
           input = input.replaceAll("\\,","")
                   .replaceAll("\\.","")
                   .replaceAll("\\?", "")
                   .replaceAll("\\:","")
                   .replaceAll("\\;", "")
                   .replaceAll("\\/","")
                   .replaceAll("\\-", "")
                   .replaceAll("\\!", "")
                   .replaceAll("\\(", "")
                   .replaceAll("\\)", "")
                   .replaceAll("\\\"", "")
                   
                   ;
          
          
            Double.parseDouble(input);
            
            rtnbool = true;
        } catch (NumberFormatException ex) {
            //We catch this but does nothing to it because we dont need to :)
            //Because if the Double.ParseDouble throws the exception then if can't parse it.
        }
        plugin.debug("isNumbers:"+rtnbool);
        return rtnbool;
    }

    /**
     * Checks if the message contains the IP Pattern
     *
     * @param message the message you want to check on.
     * @return true if the message is in the IP pattern and not on the
     * whitelist.
     */
    private int checkForIPPattern(String message) {
        int advertising = 0;
        Matcher regexMatcher = ipPattern.matcher(message);

        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                if (ipPattern.matcher(message).find()) {
                    if (!whitelistLine.contains(regexMatcher.group().trim())) {
                        advertising = 1;
                    } else {
                        advertising = 2;
                    }
                }
            }
        }
        return advertising;
    }

    /**
     * Method to check if it is in the webpattern!
     *
     * @param message the message you want to get checked.
     * @return true if it's in the webpattern and false if it isn't
     */
    private int checkForWebPattern(String message) {
        int advertising = 0;
        Matcher regexMatcherurl = webpattern.matcher(message);
        plugin.debug("Message: "+message);

        while (regexMatcherurl.find()) {
            String text = regexMatcherurl.group().trim().replaceAll("www.", "").replaceAll("http://", "").replaceAll("https://", "");
            plugin.debug(text+"g" + "reg:" +regexMatcherurl.group().length() + " group lenght"+regexMatcherurl.group().length());
            if (regexMatcherurl.group().length() != 0 && text.length() != 0) {
                plugin.debug(regexMatcherurl.group().trim() + " + test");
                if (webpattern.matcher(message).find()) {
                    if (!whitelistLine.contains(text)) {
                        plugin.debug("for this" + text);
                        advertising = 1;
                    } else {
                        advertising = 2;
                    }
                }
            }
        }
        return advertising;
    }

    /**
     * method to Reload/load the config Options in the adfinder.
     */
    public void startUp() {
        spamDetection = plugin.getConfig().getBoolean("Spam-Detection");
        urlDetection = plugin.getConfig().getBoolean("URL-Detection");
        IPDetection = plugin.getConfig().getBoolean("IP-Detection");
        numbers = plugin.getConfig().getInt("Spam-Number-Letters");
        procentCapital = plugin.getConfig().getInt("Spam-Procent-Capital-Words");
        checkWordLenght = plugin.getConfig().getBoolean("Spam-Number-Letters-check");
        warn = new HashMap<Player, Integer>();
    }
}
