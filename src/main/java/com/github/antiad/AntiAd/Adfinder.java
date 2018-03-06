package com.github.antiad.AntiAd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Adfinder {

    private final AntiAd plugin;
    // ip pattern NEW PATTERN: https://regexr.com/3lpie OLD PATTERN: http://regexr.com?33l17
    private final Pattern ipPattern = Pattern.compile("(?:\\d{1,3}[.,\\-:;\\/()=?}+ ]{1,4}){3}\\d{1,3}");
    // web pattern http://regexr.com?36elv
    private Pattern webpattern;
    // web pattern http://regexr.com?36elv
    private final String webpatternAdvanced = "[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+~#?&//=]*)?";
    // Simple pattern http://regexr.com/3cu3l
    private final String webpatternSimple = "[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.(com|ru|net|org|de|jp|uk|br|pl|in|it|fr|au|info|nl|cn|ir|es|cz|biz|ca|kr|eu|ua|za|co|gr|ro|se|tw|vn|mx|ch|tr|at|be|hu|dk|tv|me|ar|us|no|sk|fi|id|cl|nz|by|pt)\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?";
    private HashMap<Player, Integer> warn;
    private boolean spamDetection;
    public boolean  urlDetection, IPDetection, checkWordLenght;
    public int numbers, procentCapital;
    private ArrayList<String> whitelistLine, whitelistWildCardList;

    public Adfinder(AntiAd instance) {
        plugin = instance;
        startUp();
    }

    /**
     * Check if the mesage is a spam or not!
     *
     * @param check
     * @return true or false depending on if it is spam or not!
     */
    public boolean checkForSpam(Check check) {
        boolean spam = false;

        for (String word : check.getMessage().split("\\s+")) {
            if (checkWordLenght && word.length() >= numbers && numbers != 0) {
                //Checks if the message is longer than the max allowed, it only does this if the config allows it.
                plugin.debug("this is marked as spam because " + word.length() + ">=" + numbers);
                spam = true;
                break;
                // if the word is 4 or under && it
            }
        }

        return spam;
    }

    public boolean checkForCaps(Check check) {
        boolean caps = false;
        String[] words = check.getMessage().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.length() >= 4 && word.equals(word.toUpperCase()) && !isNumbers(word) && procentCapital != 0) {
                plugin.debug("else if 1");
                word = word.toLowerCase();
                words[i] = word;
                caps = true;
                break;
                //if the words is longer than or 4 long
            } else if (word.length() >= 4 && procentCapital != 0) {

                int upper = 0;
                char[] charArray = word.toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    String letter = charArray[j] + "";
                    if (letter.equals(letter.toUpperCase()) && !isNumbers(letter)) {
                        upper++;
                    }

                }

                if (upper * 100 / charArray.length * 100 >= procentCapital * 100) {
                    plugin.debug("else if 2");
                    word = word.toLowerCase();
                    words[i] = word;
                    caps = true;
                    break;
                }
            }
        }
        check.setMessage(StringUtils.join(words, " "));
        return caps;
    }

    /**
     * a Method to check if it's advertising, this methods call
     * checkForIPPattern and checkForWebPattern but only if it's allowed in the
     * config.
     *
     * This uses int because if it's adverting there are on the whitelist then
     * it shouldn't check for spam.
     *
     * @param check Check where we can get the message
     * @return 1 if advertising, and 2 if it's advertingsing there are on the
     * whitelist.
     */
    public int checkForAdvertising(Check check) {
        int advertising = 0;
        // CHECK FOR IP PATTERN if it's turned on.
        if (IPDetection) {
            advertising = checkForIPPattern(check);
            plugin.debug("Checking for IP");
        }
        //if it marks it as advertising on the IP pattern, we don't want to check if for the web pattern
        if (advertising == 0 && urlDetection) {
            plugin.debug("Checking for web");
            advertising = checkForWebPattern(check);
        }

        return advertising;
    }

    /**
     * Does this adfinder protect against spam and caps?
     * @return return true if it protects against spam and caps
     */
    public boolean isSpamDetection() {
        return spamDetection;
    }

/**
 * 
 * Save the message to the log if the server have that in the config, by default it does this.
 * 
 * @param player the playername of the player
 * @param type the type of the messsage (advertisement, spam etc.)
 * @param message The message the player is spamming/ advertisement)
 * @param where Where was it spammed, so we can see that for future problems.
 */
    public void log(String player,String type,String message,String where) {
        plugin.debug("Begin to log:" + message);
        if(plugin.getConfig().getBoolean("log",true)){
            try {
                try (BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Log.txt", true))) {
                    write.append(now("[yyyy-MM-dd HH:mm:ss]")+" - "+player+" - "+where + " - "+message);
                    write.newLine();
                    write.flush();
                }
            } catch (IOException ex) {
                plugin.getLogger().log(Level.WARNING, plugin.getFromLanguage("ERRORLogSave").replace("%MESSAGE%", ex.getMessage()));
            }
        }
    }

    /**
     *
     * @param message the message the player has send!
     * @param player the player there sent the message!
     * @param type 1 for AD 2 for spam. (gets logged)
     * @param where
     */
    public void sendWarning(Player player, String message, int type, int where) {
        plugin.debug("SENDING WARNING!!!!");
        //First we gonna warn the admins (ops) about the player and what he chatted. 
        if ((type == 1 && plugin.getConfig().getBoolean("AdWarnAdmins")) || (type == 2 && plugin.getConfig().getBoolean("SpamWarnAdmins"))) {
            Set<OfflinePlayer> tempOps = Bukkit.getServer().getOperators();
            OfflinePlayer[] ops = tempOps.toArray(new OfflinePlayer[tempOps.size()]);
            for (OfflinePlayer op : ops) {
                if (op.isOnline()) {
                    op.getPlayer().sendMessage(plugin.getColorfullLanguageAndTag("logWarning").replace("%PLAYER%", player.getDisplayName()).replace("%TYPE%", typeToX(type, 1)).replace("%WHERE%", whereToTXT(where)).replace("%MESSAGE%", message));
                }
            }
        }

        // Start logging and sending the warning
        log(player.getDisplayName(),typeToX(type, 1),message,whereToTXT(where));

        Bukkit.getServer().getLogger().info(plugin.getFromLanguageAndTag("logWarning").replace("%PLAYER%", player.getDisplayName()).replace("%TYPE%", typeToX(type, 2)).replace("%WHERE%", whereToTXT(where)).replace("%MESSAGE%", message));
        //adding a warning to the player.
        int warnings = 1;
        // if we know the player we gonna remove him and count the warnings up.
        if (warn.containsKey(player)) {
            warnings = warn.get(player) + 1;
            warn.remove(player);
        }
        warn.put(player, warnings);

        int maxWarnings = plugin.getConfig().getInt("warnings");

        // begin sending the warning to the player
        if (warn.get(player) >= maxWarnings) {
            // if he is at max Warnings we gonna take action
            takeAction(player, type);
        } else {
            player.sendMessage(plugin.getColorfullLanguageAndTag("chancesLeft").replace("%WARNINGS%", warn.get(player) + "").replace("%CHANCES%", maxWarnings + ""));
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
        plugin.getServer().getScheduler().runTask(plugin, new AdfinderAction(command, plugin, broadcastMessage, typeToX(type, 2), player.getDisplayName()));

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
            whitelistWildCardList = new ArrayList<String>();
            String line;
            while ((line = read.readLine()) != null) {
                whitelistAdd(line);
            }

        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, plugin.getColorfullLanguage("whitelistNotFound"));
        }
    }

    public void whitelistAdd(String line) {
        if (line.startsWith("*") || line.endsWith("*")) {
            whitelistWildCardList.add(line);
        } else {
            whitelistLine.add(line);
        }
    }

    /**
     * A method to change the where int to a String.
     *
     * @param where The type of where it is written (1 chat, 2 msg, 3 sign, 4
     * book!
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
            case 4:
                returnString = "book";
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
            input = input.replaceAll("[^A-Za-z0-9]", "");
            Double.parseDouble(input);
            rtnbool = true;
        } catch (NumberFormatException ex) {
            //We catch this but does nothing to it because we dont need to :)
            //Because if the Double.ParseDouble throws the exception then it can't parse it.
        }
        plugin.debug("isNumbers: " + rtnbool);
        return rtnbool;
    }

    /**
     * Checks if the message contains the IP Pattern
     *
     * @param message the message you want to check on.
     * @return true if the message is in the IP pattern and not on the
     * whitelist.
     */
    private int checkForIPPattern(Check check) {
        int advertising = 0;
        String message = check.getMessage();
        Matcher regexMatcher = ipPattern.matcher(message);

        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                if (ipPattern.matcher(message).find()) {
                    String advertisement = regexMatcher.group().trim();
                    plugin.debug(regexMatcher.group());
                    if (!whitelistLine.contains(advertisement)) {
                        advertising = 1;
                        plugin.debug("found in ip pattern!");
                        message = message.replace(advertisement, "Advertisement");
                        check.setMessage(message);
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
    private int checkForWebPattern(Check check) {
        int advertising = 0;
        String message = check.getMessage().toLowerCase();
        Matcher regexMatcherurl = webpattern.matcher(message);
        plugin.debug("Message: " + message);

        while (regexMatcherurl.find()) {
            String advertisement = regexMatcherurl.group().trim().replaceAll("www.", "").replaceAll("http://", "").replaceAll("https://", "");

            plugin.debug(advertisement + "g" + "reg:" + regexMatcherurl.group().length() + " group lenght" + regexMatcherurl.group().length());
            if (regexMatcherurl.group().length() != 0 && advertisement.length() != 0) {
                plugin.debug(regexMatcherurl.group().trim() + " + test");
                if (webpattern.matcher(message).find()) {
                    if (checkInWhitelist(advertisement)) {
                        plugin.debug("for this" + advertisement);
                        message = message.replace(advertisement, "Advertisement");
                        check.setMessage(message);
                        advertising = 1;
                        break;
                    } else {
                        advertising = 2;
                    }
                }
            }
        }
        return advertising;
    }

    public boolean checkInWhitelist(String text) {
        boolean advertised = true;
        if (whitelistLine.contains(text)) {
            advertised = false;
        } else {
            if (whitelistWildCardList.size() > 0) {
                for (String whitelistItem : whitelistWildCardList) {
                    plugin.debug("looking at " + whitelistItem + (advertised ? " true" : " false"));
                    if (whitelistItem.startsWith("*") && whitelistItem.endsWith("*")) {
                        advertised = !text.contains(whitelistItem.replace("*", ""));
                    } else if (whitelistItem.startsWith("*")) {
                        advertised = !text.endsWith(whitelistItem.replace("*", ""));
                    } else if (whitelistItem.endsWith("*")) {
                        advertised = !text.startsWith(whitelistItem.replace("*", ""));
                        plugin.debug(advertised ? "true" : "false");
                    }
                    if (!advertised) {
                        plugin.debug("Found it!");
                        break;
                    }
                }
            }
        }
        return advertised;
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
        if (plugin.getConfig().getBoolean("useSimpleWebPattern")) {
            webpattern = Pattern.compile(webpatternSimple);
            plugin.debug("Simple pattern loaded");
        } else {
            webpattern = Pattern.compile(webpatternAdvanced);
            plugin.debug("Advanced pattern loaded");
        }

        loadWhitelist();
    }
}
