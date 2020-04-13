/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.antiad.AntiAd;

import com.github.antiad.AntiAd.model.Core;
import org.bukkit.entity.Player;

import java.text.Normalizer;

/**
 * @author Franz
 */
public class Check {

    private final Adfinder adfinder;
    private final Core core;
    private final Player player;
    private boolean spam, advertisement, caps;
    private String message;

    public Check(Core core, Player player) {
        this.adfinder = core.getPlugin().getAdfinder();
        this.core = core;
        this.player = player;
    }


    boolean check(String message, int type, boolean checkForSpam) {
        message = Normalizer.normalize(message, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        this.message = message;
        boolean rtnbool = false;
        int ad = 0;
        core.debug("We are testing player " + player.getName() + " Msg: " + message + "  type: " + type + "    checkSpam" + checkForSpam);
        // if the player hasn't permission the bypass advertising then we check if for advertising.
        if (!player.hasPermission("antiad.bypass.ad")) {
            core.debug("Checking for advertising.");
            ad = adfinder.checkForAdvertising(this);
        }

        if (ad == 1) {
            adfinder.sendWarning(player, message, 1, type);
            advertisement = true;
            rtnbool = true;
        } else if (ad == 0) {
            //if it's not advertising then check for spam
            if (core.getConfig().isSpamDetection() && checkForSpam && !player.hasPermission("antiad.bypass.spam")) {
                spam = adfinder.checkForSpam(this);
                if (spam) {
                    adfinder.sendWarning(player, message, 2, type);
                    rtnbool = true;
                }
                caps = adfinder.checkForCaps(this);
                if (caps && !rtnbool) {
                    adfinder.sendWarning(player, message, 2, type);
                    rtnbool = true;
                }

            }
        }
        return rtnbool;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSpam() {
        return spam;
    }

    public boolean isAdvertisement() {
        return advertisement;
    }

    public boolean isCaps() {
        return caps;
    }
}
