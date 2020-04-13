package com.github.antiad.AntiAd;

import com.github.antiad.AntiAd.model.Config;
import com.github.antiad.AntiAd.model.Core;
import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class CheckTest {

    public CheckTest() {
    }

    @Mock
    private AntiAd plugin;
    @Mock
    private Core core;
    @Mock
    private Config config;
    @Mock
    private Adfinder adfinder;
    @Mock
    private Player player;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(core.getConfig()).thenReturn(config);
        Mockito.when(core.getPlugin()).thenReturn(plugin);
        Mockito.when(plugin.getAdfinder()).thenReturn(adfinder);
        Mockito.when(config.isSpamDetection()).thenReturn(true);


    }

    @Test
    public void testAllAdfinderMethodsAreCalled() {

        Check check = new Check(core, player);

        check.check("Hello World", 1, true);

        Mockito.verify(adfinder).checkForAdvertising(check);
        Mockito.verify(adfinder).checkForCaps(check);
        Mockito.verify(adfinder).checkForSpam(check);
        Assert.assertFalse(check.isAdvertisement());
        Assert.assertFalse(check.isCaps());
        Assert.assertFalse(check.isSpam());
    }

    @Test
    public void testOnlyAdvertisementAdfinderMethodsAreCalled() {

        Mockito.when(core.getConfig().isSpamDetection()).thenReturn(false);

        Check check = new Check(core, player);

        check.check("Hello World", 1, true);

        Mockito.verify(adfinder).checkForAdvertising(check);
        Mockito.verify(adfinder, Mockito.never()).checkForCaps(check);
        Mockito.verify(adfinder, Mockito.never()).checkForSpam(check);
        Assert.assertFalse(check.isAdvertisement());
        Assert.assertFalse(check.isCaps());
        Assert.assertFalse(check.isSpam());
    }

    @Test
    public void testOtherChecksAreSkippedWhenAdvertisementsAreDetected() {
        Mockito.when(core.getConfig().isSpamDetection()).thenReturn(true);
        Mockito.when(adfinder.checkForAdvertising(Mockito.any(Check.class))).thenReturn(1);

        Check check = new Check(core, player);

        check.check("Hello World", 1, true);

        Mockito.verify(adfinder).checkForAdvertising(check);
        Mockito.verify(adfinder, Mockito.never()).checkForCaps(check);
        Mockito.verify(adfinder, Mockito.never()).checkForSpam(check);
        Assert.assertTrue(check.isAdvertisement());
        Assert.assertFalse(check.isCaps());
        Assert.assertFalse(check.isSpam());
    }

    @Ignore
    @Test
    public void testOtherChecksAreStillRunWhenWhitelistedAdvertisementsAreDetected() {
        Check check = new Check(core, player);
        check.check("Hello World", 1, true);

        Mockito.verify(adfinder).checkForAdvertising(check);
        Mockito.verify(adfinder).checkForCaps(check);
        Mockito.verify(adfinder).checkForSpam(check);
        Assert.assertFalse(check.isAdvertisement());
        Assert.assertFalse(check.isCaps());
        Assert.assertFalse(check.isSpam());
    }

}
