package com.github.antiad.AntiAd;
/**
 * This class is for execute commands (kick, ban tempban etc.) for when a player has reaced the limit (current 3).
 * but that is handled in the adfinder, this is only executing the command as console!
 * @since 2.0
 * @author KasperFranz
 * @version 2.2-DEV
 *  Changed: Made comments for this and some cleanup
 */
public class AdfinderAction implements Runnable {

    private final String command;
    private final AntiAd plugin;
    private final String broadcastmsg;

    public AdfinderAction(String command, AntiAd plugin, String broadcastmsg) {
        this.command = command;
        this.plugin = plugin;
        this.broadcastmsg = broadcastmsg;
    }

    @Override
    public void run() {
        //First we try to run the command and if we gets a error while doing it we broadcast our Error message
        if (!plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command)) {
            plugin.getServer().broadcastMessage(plugin.getLanguage().getProperty("ErrorExecutingCommand"));
            
            // if we can execute it we gonna go into broadcasting a message about 
            // the player being kicked for advertising/spamming.
            // NB: This is only executed if the Notification-message is on!
        } else {
            
            if (plugin.getConfig().getBoolean("Notification-Message")) {
                plugin.getServer().broadcastMessage(broadcastmsg);
            }
        }
    }
}
