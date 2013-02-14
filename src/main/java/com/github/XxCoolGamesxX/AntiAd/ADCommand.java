/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.jne.AntiAd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import me.jne.AntiAd.AntiAd;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Franz
 */
public class ADCommand implements CommandExecutor {

    private AntiAd plugin;

    public ADCommand(AntiAd plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean rtnbool;

        if (args.length == 0) {
            rtnbool = false;
        } else if (args[0].equalsIgnoreCase("reload") && (sender.isOp() || sender.hasPermission("antiad.reload"))) {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "AntiAd" + ChatColor.YELLOW + " configuration file reloaded!");
            rtnbool = true;
        } else if (args[0].equalsIgnoreCase("add") && ((sender.isOp() || sender.hasPermission("antiad.whitelist")))) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "You must specify an IP/URL!");
                rtnbool = true;
                plugin.getAdfinder().loadWhitelist();
            } else {
                String ip = args[1];
                try {
                    BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Whitelist.txt", true));
                    write.append(ip);
                    write.newLine();
                    write.flush();
                    write.close();
                    sender.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] The URL/IP added to Whitelist!");
                    rtnbool = true;
                } catch (IOException e) {
                    plugin.getLogger().info("AntiAid Whitelist file not found IOException!" + e.getMessage());
                    rtnbool = false;
                }
            }

        } else {
            rtnbool = false;
        }


        return rtnbool;

    }
}
