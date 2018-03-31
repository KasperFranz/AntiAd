/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.antiad.AntiAd;

import com.github.antiad.AntiAd.model.Core;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.Mockito;

/**
 *
 * @author Fernando
 */
public class CheckTest {

    public CheckTest() {
    }

    @Test
    public void testAllAdfinderMethodsAreCalled() {
        AntiAd pl = Mockito.mock(AntiAd.class);
        Player player = Mockito.mock(Player.class);
        Adfinder finder = Mockito.mock(Adfinder.class);
        Core core = Mockito.mock(Core.class);

        Mockito.when(pl.getAdfinder()).thenReturn(finder);
        Mockito.when(core.getConfig().isSpamDetection()).thenReturn(true);

        Check check = new Check(pl, player);

        check.check("Hello World", 1, true);

        Mockito.verify(finder).checkForAdvertising(check);
        Mockito.verify(finder).checkForCaps(check);
        Mockito.verify(finder).checkForSpam(check);
        Assert.assertEquals(false, check.isAdvertisement());
        Assert.assertEquals(false, check.isCaps());
        Assert.assertEquals(false, check.isSpam());
    }

    @Test
    public void testOnlyAdvertisementAdfinderMethodsAreCalled() {
        AntiAd pl = Mockito.mock(AntiAd.class);
        Player player = Mockito.mock(Player.class);
        Adfinder finder = Mockito.mock(Adfinder.class);
        Core core = Mockito.mock(Core.class);

        Mockito.when(pl.getAdfinder()).thenReturn(finder);
        Mockito.when(core.getConfig().isSpamDetection()).thenReturn(false);

        Check check = new Check(pl, player);

        check.check("Hello World", 1, true);

        Mockito.verify(finder).checkForAdvertising(check);
        Mockito.verify(finder, Mockito.never()).checkForCaps(check);
        Mockito.verify(finder, Mockito.never()).checkForSpam(check);
        Assert.assertEquals(false, check.isAdvertisement());
        Assert.assertEquals(false, check.isCaps());
        Assert.assertEquals(false, check.isSpam());
    }

    @Test
    public void testOtherChecksAreSkippedWhenAdvertisementsAreDetected() {
        AntiAd pl = Mockito.mock(AntiAd.class);
        Player player = Mockito.mock(Player.class);
        Core core = Mockito.mock(Core.class);
        Adfinder finder = Mockito.mock(Adfinder.class);

        Mockito.when(pl.getAdfinder()).thenReturn(finder);
        Mockito.when(core.getConfig().isSpamDetection()).thenReturn(true);
        Mockito.when(finder.checkForAdvertising(Mockito.any(Check.class))).thenReturn(1);

        Check check = new Check(pl, player);

        check.check("Hello World", 1, true);

        Mockito.verify(finder).checkForAdvertising(check);
        Mockito.verify(finder, Mockito.never()).checkForCaps(check);
        Mockito.verify(finder, Mockito.never()).checkForSpam(check);
        Assert.assertEquals(true, check.isAdvertisement());
        Assert.assertEquals(false, check.isCaps());
        Assert.assertEquals(false, check.isSpam());
    }

    @Ignore
    @Test
    public void testOtherChecksAreStillRunWhenWhitelistedAdvertisementsAreDetected() {
        AntiAd plugin = Mockito.mock(AntiAd.class);
        Player player = Mockito.mock(Player.class);
        Core core = Mockito.mock(Core.class);
        Adfinder finder = Mockito.mock(Adfinder.class);

        Mockito.when(plugin.getAdfinder()).thenReturn(finder);
        Mockito.when(core.getConfig().isSpamDetection()).thenReturn(true);
        Mockito.when(finder.checkForAdvertising(Mockito.any(Check.class))).thenReturn(2);

        Check check = new Check(plugin, player);

        check.check("Hello World", 1, true);

        Mockito.verify(finder).checkForAdvertising(check);
        Mockito.verify(finder).checkForCaps(check);
        Mockito.verify(finder).checkForSpam(check);
        Assert.assertEquals(false, check.isAdvertisement());
        Assert.assertEquals(false, check.isCaps());
        Assert.assertEquals(false, check.isSpam());
    }

}
