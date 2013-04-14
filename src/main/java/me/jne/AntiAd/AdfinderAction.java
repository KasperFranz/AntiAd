package me.jne.AntiAd;

/**
 *
 * @author Franz
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
