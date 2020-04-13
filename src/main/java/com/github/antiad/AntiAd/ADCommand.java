package com.github.antiad.AntiAd;

import com.github.antiad.AntiAd.model.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Franz
 */
public class ADCommand implements CommandExecutor {

    private final AntiAd plugin;
    private final Core core;

    public ADCommand(Core core) {
        this.core = core;
        this.plugin = core.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean rtnbool;

        if (args.length == 0) {
            sender.sendMessage(plugin.getColorfullLanguage("helpMessageHeader"));
            sender.sendMessage(plugin.getColorfullLanguage("helpMessageReload"));
            sender.sendMessage(plugin.getColorfullLanguage("helpMessageAdd"));
            rtnbool = true;
            
        } else if (args[0].equalsIgnoreCase("reload") && (sender.isOp() || sender.hasPermission("antiad.reload"))) {
            plugin.reload();
            sender.sendMessage(plugin.getColorfullLanguageAndTag("onCommandReloadMessage"));
            rtnbool = true;
        } else if (args[0].equalsIgnoreCase("add") && ((sender.isOp() || sender.hasPermission("antiad.whitelist")))) {
            rtnbool = true;
            if (args.length < 2) {
                core.getConfig().loadWhitelist("plugins/AntiAd/Whitelist.txt");
                sender.sendMessage(plugin.getColorfullLanguageAndTag("AddCommandNoIP"));    
            } else {
                String ip = args[1];
                try {
                    BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Whitelist.txt", true));
                    write.append(ip);
                    write.newLine();
                    write.flush();
                    write.close();
                    core.getConfig().whitelistAdd(ip);
                    sender.sendMessage(plugin.getColorfullLanguageAndTag("AddCommandAdded"));
                } catch (IOException ex) {
                    plugin.getLogger().info(plugin.getFromLanguage("whitelistNotFound") + ex.getMessage());
                    rtnbool = false;
                }
            }

        } else {
            rtnbool = false;
        }


        return rtnbool;

    }
}
