package com.github.antiad.AntiAd;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;

/**
 * This class is for execute commands (kick, ban tempban etc.) for when a player
 * has reaced the limit (current 3). but that is handled in the adfinder, this
 * is only executing the command as console!
 *
 * @since 2.0
 * @author KasperFranz
 * @version 2.2-DEV Changed: Made comments for this and some cleanup
 */
public class AdfinderAction implements Runnable {

    private final String executeCommand;
    private final AntiAd plugin;
    private final String broadcastmsg, player, reason;

    public AdfinderAction(String command, AntiAd plugin, String broadcastmsg, String reason, String player) {
        this.executeCommand = command;
        this.plugin = plugin;
        this.broadcastmsg = broadcastmsg;
        this.reason = reason;
        this.player = player;
    }

    @Override
    public void run() {
        //First we try to run the command and if we gets a error while doing it we broadcast our Error message
        String commandString = executeCommand.split(" ", 2)[0].toLowerCase();
        Command command = Bukkit.getServer().getPluginCommand(commandString);
        plugin.debug("ban".equals(commandString) ? "true" : "false");
        if (command == null && !("ban".equals(commandString) || "kick".equals(commandString))) {
            Set<OfflinePlayer> tempOps = Bukkit.getServer().getOperators();
            OfflinePlayer[] ops = tempOps.toArray(new OfflinePlayer[tempOps.size()]);
            for (OfflinePlayer op : ops) {
                if (op.isOnline()) {
                    op.getPlayer().sendMessage(plugin.getColorfullLanguageAndTag("commandNotExecuteable").replace("%COMMAND%", commandString));
                    op.getPlayer().sendMessage(plugin.getColorfullLanguageAndTag("commandNotExecuteableMore").replace("%PLAYER%", player).replace("%FOR%", reason));
                }
            }

            Bukkit.getServer().getLogger().warning(plugin.getFromLanguageAndTag("commandNotExecuteable").replace("%COMMAND%", commandString));
            Bukkit.getServer().getLogger().warning(plugin.getFromLanguageAndTag("commandNotExecuteableMore").replace("%PLAYER%", player).replace("%FOR%", reason));

        } else if (!plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), executeCommand)) {
            plugin.getServer().broadcastMessage(plugin.getColorfullLanguageAndTag("ErrorExecutingCommand"));

            // if we can execute it we gonna go into broadcasting a message about 
            // the player being kicked for advertising/spamming.
            // NB: This is only executed if the Notification-message is on!
        } else {

            if (plugin.getAdfinder().isNotifyMessage()) {
                plugin.getServer().broadcastMessage(broadcastmsg);
            }
        }
    }
}
