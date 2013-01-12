package me.jne.AntiAd;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent chat) {
            chat.setCancelled(plugin.getAdfinder().check(chat.getPlayer(),chat.getMessage()));
    }
    
    
    public void onPlayerJoin(PlayerJoinEvent join){
        if(plugin.getConfig().getBoolean("JoinMsg-On")){
        	join.getPlayer().sendMessage(ChatColor.YELLOW + "Running" + ChatColor.GREEN + " AntiAd"  + ChatColor.YELLOW + ", Author By 08jne01 and Developers By XxCoolGamesxX and FranzMedia.");
        }
    }
}