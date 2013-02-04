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
    private Pattern ipPattern, webpattern;
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

        
        // ip pattern http://regexr.com?33l17
        ipPattern = Pattern.compile("((?<![0-9])(?:(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2})[ ]?[., ][ ]?(?:25[0-5]|2[0-4][0-9]|[0-1]?[0-9]{1,2}))(?![0-9]))");
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
    public boolean check(Player player, String message, int type, boolean checkForSpam) {
        boolean rtnbool = false;

        if (checkForAdvertising(player, message)) {
            sendWarning(player, message, 1, type);
            rtnbool = true;
        } else {
            //check for spam
            if (spamDetection && checkForSpam) {
                if (checkForSpam(player, message)) {
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
     * @param player the player executing the message
     * @param message the message to check for ads!
     * @return true or false depending on if it is spam or not!
     */
    private boolean checkForSpam(Player player, String message) {
        boolean spam = false;

        int number = plugin.getConfig().getInt("Spam-Number-Letters");
        int procentCapital = plugin.getConfig().getInt("Spam-Procent-Capital-Words");
        String[] words = message.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() >= number) {
                spam = true;
                i = words.length;
                player.sendMessage("1" + spam);
            } else if ( words[i].length() >= 4 && words[i].equals(words[i].toUpperCase()) && !isNumbers(words[i])) {
                spam = true;
                i = words.length;
                player.sendMessage("2" + spam);
            } else if(words[i].length() >= 4) {
                int upper = 0;
                char[] charArray = words[i].toCharArray();
                for (int j = 0; j < charArray.length; j++) {
                    String letter = charArray[j]+"";
                    if (letter.equals(letter.toUpperCase()) && !isNumbers(letter)) {
                        upper++;
                        player.sendMessage(letter);
                    }



                }

                if (upper * 100 / charArray.length * 100 >= procentCapital*100) {
                    spam = true;
                    i = words.length;
                    player.sendMessage("3" + spam + upper * 100 / charArray.length * 100 + " >=" + procentCapital);
                }
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
                command = plugin.getConfig().getString("Command-Ad").replaceAll(">reasonad>", typeToX(1, 3)).replaceAll("<time>", plugin.getConfig().getString("Time"));
                break;
            case 2:
                command = plugin.getConfig().getString("Command-Spam").replaceAll("<reasonspam>", typeToX(2, 3));;
                break;
            default:
                command = "";

                break;
        }

        String broadcastMessage = ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + player.getDisplayName() + ChatColor.DARK_GREEN + " has been " + getActionType(command) + " for " + typeToX(type, 2);
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

    private boolean isNumbers(String input) {
        boolean rtnbool = false;
        try {
            Integer.parseInt(input);
            rtnbool = true;
        } catch (NumberFormatException ex) {
            //We catch this but does nothing to it because we dont need to :)
        }

        return rtnbool;
    }
}
