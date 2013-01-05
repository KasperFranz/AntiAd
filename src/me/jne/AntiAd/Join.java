package me.jne.AntiAd;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {
	AntiAd plugin;
	public Join(AntiAd instance){
	plugin = instance;
	}
	
	@EventHandler
   public void onPlayerJoin(PlayerJoinEvent join){
        String JoinMsg = plugin.getConfig().getString("JoinMsg-On");
        if(JoinMsg.equalsIgnoreCase("true")){
    	Player player = join.getPlayer();
    	player.sendMessage(ChatColor.YELLOW + "Running" + ChatColor.GREEN + " AntiAd"  + ChatColor.YELLOW + ", Author By 08jne01 and Developers By XxCoolGamesxX and franzmedia.");
        }
    }
}
