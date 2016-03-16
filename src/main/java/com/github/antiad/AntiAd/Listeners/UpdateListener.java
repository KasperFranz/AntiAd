/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.antiad.AntiAd.Listeners;

import com.github.antiad.AntiAd.AntiAd;
import com.github.antiad.AntiAd.Update;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author KasperFranz
 */
public class UpdateListener implements Listener {

    private final Update update;
    private final AntiAd plugin;

    public UpdateListener(Update update) {
        this.update = update;
        this.plugin = update.getPlugin();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("antiad.notify.update")) {
            return;
        }
        event.getPlayer().sendMessage(plugin.getColorfullLanguageAndTag("updateAvalible").replaceAll("%LINK%", update.getLinkToDev()).replaceAll("%VERSION%", update.getVersionName()));

    }
}
