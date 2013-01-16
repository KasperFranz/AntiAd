package me.jne.AntiAd;

import org.bukkit.ChatColor;

/**
 *
 * @author Franz
 */
public class AdfinderAction implements Runnable {

    private String command;
    private AntiAd plugin;
    private String broadcastmsg;

    public AdfinderAction(String command, AntiAd plugin, String broadcastmsg) {
        this.command = command;
        this.plugin = plugin;
        this.broadcastmsg = broadcastmsg;
    }

    @Override
    public void run() {



        if (!plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command)) {
            plugin.getServer().broadcastMessage("error while trying to execute cmd!");
        } else {

            if (plugin.getConfig().getBoolean("Notification-Message")) {
                plugin.getServer().broadcastMessage(broadcastmsg);
                plugin.getServer().broadcastMessage(command);
            }
        }
    }
}
