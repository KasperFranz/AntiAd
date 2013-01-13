package me.jne.AntiAd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Adfinder implements Listener {

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
        webpattern = Pattern.compile("(http://)|(https://)?(www)?\\S{2,}((\\.com)|(\\.net)|(\\.org)|(\\.co\\.uk)|(\\.tk)|(\\.info)|(\\.es)|(\\.de)|(\\.arpa)|(\\.edu)|(\\.firm)|(\\.int)|(\\.mil)|(\\.mobi)|(\\.nato)|(\\.to)|(\\.fr)|(\\.ms)|(\\.vu)|(\\.eu)|(\\.nl))|(\\.us)|(\\.mobi)");
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onCommandSent(PlayerCommandPreprocessEvent chat) {
//        String[] arr2 = chat.getMessage().split("\\s+");
//        String CL = arr2[0];
//        List<String> Commands = plugin.getConfig().getStringList("Detected-Commands");
//        if (Commands.contains(CL)) {
//            ipurl = false;
//            spam = false;
////            String BanTypead = plugin.getConfig().getString("Command-Ad");
////            String BanTypespam = plugin.getConfig().getString("Command-Spam");
////            String AdMsg = plugin.getConfig().getString("Ad_Message");
////            String SpamMsg = plugin.getConfig().getString("Spam_Message");
////            String NMsg = plugin.getConfig().getString("Notification-Message");
////            String Spam = plugin.getConfig().getString("Spam-Detection-On");
////            String BanSpam = plugin.getConfig().getString("Reason-Spam");
////            String[] arr = BanTypead.split("\\s+");
////            String[] arr1 = BanTypespam.split("\\s+");
////            String BanTAd = arr[0];
////            String BanTSpam = arr1[0];
//            String BanAd = plugin.getConfig().getString("Reason-Ad");
//            String Url = plugin.getConfig().getString("URL-Detection");
//            Player player = chat.getPlayer();
//            String Time = plugin.getConfig().getString("Time");
//            String StealthMode = plugin.getConfig().getString("Stealth-Mode");
//
//            String Ending = "ed";
//            String Ending1 = "ed";
//            final String PlayerName = player.getName();
//            final String DisplayName = player.getServer().getPlayer(PlayerName).getDisplayName();
//
//
//
//            if (warn.containsKey(player)) {
//            } else {
//                if (warn != null) {
//                    warn.put(player, 0);
//                }
//            }
//            BufferedReader read = null;
//            try {
//                read = new BufferedReader(new FileReader("plugins/AntiAd/Whitelist.txt"));
//            } catch (FileNotFoundException e1) {
//                e1.printStackTrace();
//            }
//            List<String> lines = new ArrayList<String>();
//            String line = null;
//            try {
//                while ((line = read.readLine()) != null) {
//                    lines.add(line);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Matcher regexMatcher = ipPattern.matcher(message);
//            while (regexMatcher.find()) {
//                if (regexMatcher.group().length() != 0) {
//                    String ip = regexMatcher.group().trim();
//                    final String command = BanTypead;
//
//                    if (lines.contains(ip)) {
//                    } else {
//                        if (ipPattern.matcher(message).find()) {
//                            ipurl = true;
//                            if (!player.hasPermission("antiad.bypass.ad")) {
//
//                                log(DateTime.now("MMM dd,yyyy HH:mm ") + DisplayName + " has advertised: " + message + ", through the command " + CL);
//                                Bukkit.getServer().getLogger().info("[AntiAd] " + DisplayName + " was logged for Advertising through a command.");
//                                if (warn.get(player) == 2) {
//                                    warn.put(player, 0);
//                                    if (NMsg.equalsIgnoreCase("true")) {
//                                        Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has been " + BanTAd + Ending + " for Advertising");
//                                    }
//                                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
//                                    chat.setCancelled(true);
//                                } else {
//                                    warn.put(player, warn.get(player) + 1);
//                                    if (StealthMode.equalsIgnoreCase("true")) {
//                                        chat.setCancelled(true);
//                                        player.sendMessage(ChatColor.RED + "An internal error has occurred while attempting to perform this command");
//                                    } else {
//                                        chat.setCancelled(true);
//                                        player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!");
//                                        player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + AdMsg);
//                                    }
//                                }
//                                for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
//                                    if (player1.hasPermission("antiad.see")) {
//                                        player1.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has advertised" + " " + message);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            Matcher regexMatcherurl = webpattern.matcher(message);
//            while (regexMatcherurl.find()) {
//                if (regexMatcherurl.group().length() != 0) {
//                    String url = regexMatcherurl.group().trim();
//                    final String command = BanTypead;
//                    if (lines.contains(url)) {
//                    } else {
//                        if (webpattern.matcher(message).find()) {
//                            if (Url.contains("true")) {
//                                if (ipurl == true) {
//
//                                    spam = true;
//
//                                } else {
//                                    if (!player.hasPermission("antiad.bypass.ad")) {
//
//                                        log(DateTime.now("MMM dd,yyyy HH:mm ") + DisplayName + " has advertised: " + message + ", through the command " + CL);
//                                        Bukkit.getServer().getLogger().info("[AntiAd] " + DisplayName + " was logged for Advertising through a command.");
//                                        if (warn.get(player) == 2) {
//                                            warn.put(player, 0);
//                                            if (NMsg.equalsIgnoreCase("true")) {
//                                                Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has been " + BanTAd + Ending + " for Advertising");
//                                            }
//                                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command + " " + BanAd + " " + Time);
//                                            chat.setCancelled(true);
//                                        } else {
//                                            warn.put(player, warn.get(player) + 1);
//                                            if (StealthMode.equalsIgnoreCase("true")) {
//                                                player.sendMessage(ChatColor.RED + "An internal error has occurred while attempting to perform this command");
//                                            } else {
//                                                chat.setCancelled(true);
//                                                player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!");
//                                                player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + AdMsg);
//                                            }
//                                        }
//                                        for (Player player1 : Bukkit.getServer().getOnlinePlayers()) {
//                                            if (player1.hasPermission("antiad.see")) {
//                                                player1.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has advertised" + " " + message);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    /**
     * Command for checking if it is spam or advertising.
     *
     * @param player the player there issued this.
     * @param message the message the user sent!
     * @return true if it is spam/advertising and else false.
     */
    public boolean check(Player player, String message) {
        boolean rtnbool = false;

        boolean ipurl = false;
        boolean spam = false;


        Matcher regexMatcher = ipPattern.matcher(message);
        while (regexMatcher.find()) {
            if (regexMatcher.group().length() != 0) {
                String ip = regexMatcher.group().trim();


                if (!lines.contains(ip)) {

                    if (ipPattern.matcher(message).find()) {
                        ipurl = true;
                        if (!player.hasPermission("antiad.bypass.ad")) {
                            sendWarning(player, message, 1);
                            rtnbool = true;
                        }
                    }
                }
            }
        }
        Matcher regexMatcherurl = webpattern.matcher(message);
        if (!ipurl) {
            while (regexMatcherurl.find()) {
                if (regexMatcherurl.group().length() != 0) {
                    String url = regexMatcherurl.group().trim();

                    if (!lines.contains(url)) {
                        if (webpattern.matcher(message).find()) {
                            if (urlDetection && !player.hasPermission("antiad.bypass.ad")) {

                                rtnbool = true;
                                sendWarning(player, message, 1);

                                for (Player players : Bukkit.getServer().getOnlinePlayers()) {
                                    if (players.hasPermission("antiad.see")) {
                                        players.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + player.getDisplayName() + ChatColor.DARK_GREEN + " has advertised" + " " + message);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!(spam == true || ipurl == true)) {

            if (spamDetection) {
                String spamnum = plugin.getConfig().getString("Spam-Number-Letters");
                String spamletterword = plugin.getConfig().getString("Spam-Number-Letters-Word");
                String spamnumberword = plugin.getConfig().getString("Spam-Number-Words");
                //Pattern spamPattern = Pattern.compile("(\\S{" + Pattern.quote(spamnum) + ",}) || (([A-Z]{" + Pattern.quote(spamletterword) + ",}\\s){" + Pattern.quote(spamnumberword) + ",})");
                final Pattern spamPatterns = Pattern.compile("((\\S{" + Pattern.quote(spamnum) + ",})|([A-Z]{" + Pattern.quote(spamletterword) + ",}\\s){" + Pattern.quote(spamnumberword) + ",})");
                if (spamPattern.matcher(message).find() && !ipPattern.matcher(message).find() && !webpattern.matcher(message).find()) {
                    if (!player.hasPermission("antiad.bypass.spam")) {
                        sendWarning(player, message, 2);
                        rtnbool = true;
                    }
                }
            }
        }
        return rtnbool;

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
    private void sendWarning(Player player, String message, int type) {

        log(DateTime.now("MMM dd,yyyy HH:mm ") + player.getDisplayName() + " has +" + typeToX(type, 1) + ": " + message + ", in chat.");
        Bukkit.getServer().getLogger().info("[AntiAd] " + player.getDisplayName() + " was logged for " + typeToX(type, 2) + " in chat.");
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
       if(!player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command)){
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
     * @param to 1 spammed, 2 spamming 3 msg from config!
     * @return
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
                String line = "";
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
}
