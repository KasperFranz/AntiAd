package com.github.antiad.AntiAd;

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author Franz
 */
public class ADListener implements Listener {

    private final AntiAd plugin;

    public ADListener(AntiAd plugin) {
        this.plugin = plugin;
        
    }

    /**
     * If the chat msg is spam we gonna cannel it. Edit: fixed a bug where if
     * the chat was canneled we worked with it anyway (this is now fixed so we
     * dont use ressources on it)
     *
     * @param chat
     */
    @EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = false)
    public void onPlayerChat(AsyncPlayerChatEvent chat) {
        plugin.debug("chat gone?"+chat.isCancelled());

            if (plugin.getAdfinder().check(chat.getPlayer(), chat.getMessage(), 1, true)) {
                chat.setCancelled(true);
            }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent join) {
        if (plugin.getConfig().getBoolean("JoinMsg-On")) {
            join.getPlayer().sendMessage(plugin.getColorfullLanguageAndTag("JoinMsg"));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignCreation(SignChangeEvent sign) {

        for (int i = 0; i < sign.getLines().length; i++) {

            if (plugin.getAdfinder().check(sign.getPlayer(), sign.getLine(i), 3, false)) {
                i = sign.getLines().length;
                sign.setCancelled(true);
                sign.getBlock().breakNaturally();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandSent(PlayerCommandPreprocessEvent chat) {


        String CL = chat.getMessage().split("\\s+")[0];
        List<String> Commands = plugin.getConfig().getStringList("Detected-Commands");
        if (Commands.contains(CL)) {
            if (plugin.getAdfinder().check(chat.getPlayer(), chat.getMessage(), 2, true)) {
                chat.setCancelled(true);
            }
        }
    }
}
