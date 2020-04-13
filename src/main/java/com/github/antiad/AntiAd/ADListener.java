package com.github.antiad.AntiAd;

import com.github.antiad.AntiAd.model.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEditBookEvent;

import java.util.List;

/**
 *
 * @author Franz
 */
public class ADListener implements Listener {

    private final AntiAd plugin;
    private final Core core;

    public ADListener(Core core) {
        this.core = core;
        this.plugin = core.getPlugin();
    }

    /**
     * If the chat msg is spam we gonna cancel it. Edit: fixed a bug where if
     * the chat was canneled we worked with it anyway (this is now fixed so we
     * dont use ressources on it)
     *
     * @param chat
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerChat(AsyncPlayerChatEvent chat) {
        core.debug("chat gone?" + chat.isCancelled());
        Check check = new Check(core, chat.getPlayer());

        if (check.check(chat.getMessage(), 1, true)) {

            core.debug(" " + plugin.getConfig().getBoolean("replaceText.advertisement"));
            core.debug(" " + check.isAdvertisement());
            if (check.isSpam() && plugin.getConfig().getBoolean("replaceText.spam")) {

            } else if (
                    (check.isAdvertisement() && plugin.getConfig().getBoolean("replaceText.advertisement")) ||
                    (check.isCaps()&& plugin.getConfig().getBoolean("replaceText.caps"))
                    ) {
                chat.setMessage(check.getMessage());
            } else {
                chat.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignCreation(SignChangeEvent sign) {

        Check check = new Check(core, sign.getPlayer());
        for (int i = 0; i < sign.getLines().length; i++) {
            if (check.check(sign.getLine(i), 3, false)) {

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
            Check check = new Check(core, chat.getPlayer());
            if (check.check(chat.getMessage(), 2, true)) {
                chat.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEditBook(PlayerEditBookEvent bookEdit) {
        Check check = new Check(core, bookEdit.getPlayer());
        for (String page : bookEdit.getNewBookMeta().getPages()) {
            if (check.check(page, 4, true)) {
                bookEdit.setCancelled(true);
                break;
            }
        }
    }
}
