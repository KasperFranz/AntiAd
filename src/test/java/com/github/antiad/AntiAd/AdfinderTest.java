/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.antiad.AntiAd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Fernando
 */
@RunWith(Parameterized.class)
public class AdfinderTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            // isSpam, isAdvertisement, isCaps, message
            {false, false, false, "Hello world"}, // Should be ok
            {false, true, false, "Join my server 127.0.0.1"}, // advertisement
            {true, false, false, "12121212121212121212121212121212121212"}, // spam
            {false, false, true, "TROLLOLOOLOOLOLLOL"}, // caps
            {true, false, true, "T1R1O1L1L1O1L1O1O1L1O1O1L1O1L1L1O1L"}, // spam, caps
            {false, true, false, "Join my server play.minecraft.net"}, // advertisement
            {false, true, true, "JOIN MY SERVER PLAY.MINECRAFT.NET"}, // caps, advertisement
            {false, false, false, "Join my server github.com"}, // Whitelisted ip
            {false, false, false, "gg"}, // Should be ok
            {false, false, false, "I like how you build the floor from grass"}, // Should be ok
        });
    }
    
    @Rule
    public TestName name = new TestName();

    private final Boolean isSpam;

    private final Boolean isAdvertisement;

    private final Boolean isCaps;

    private final String message;
    
    @Mock 
    private Check check;
    
    @Mock 
    private AntiAd plugin;
    
    @Mock 
    private PluginLogger logger;
    
    @Mock 
    private FileConfiguration config;
    
    private Adfinder finder;

    public AdfinderTest(Boolean isSpam, Boolean isAdvertisement, Boolean isCaps, String message) {
        this.isSpam = isSpam;
        this.isAdvertisement = isAdvertisement;
        this.isCaps = isCaps;
        this.message = message;
    }
    
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        Mockito.when(plugin.getConfig()).thenReturn(config);
        Field f1 = JavaPlugin.class.getDeclaredField("logger");
        f1.setAccessible(true);
        f1.set(plugin, logger);
        Mockito.when(check.getMessage()).thenReturn(message);
        Mockito.when(config.getBoolean("Spam-Detection")).thenReturn(true);
        Mockito.when(config.getBoolean("URL-Detection")).thenReturn(true);
        Mockito.when(config.getBoolean("IP-Detection")).thenReturn(true);
        Mockito.when(config.getInt("Spam-Number-Letters")).thenReturn(20);
        Mockito.when(config.getInt("Spam-Procent-Capital-Words")).thenReturn(80);
        Mockito.when(config.getBoolean("Spam-Number-Letters-check")).thenReturn(true);
        
        finder = new Adfinder(plugin);
        
        // Required because plguin fails loding properly when in mocked context (whitelist file missing)
        Field f2 = Adfinder.class.getDeclaredField("whitelistLine");
        f2.setAccessible(true);
        f2.set(finder, new ArrayList<>());
        Field f3 = Adfinder.class.getDeclaredField("whitelistWildCardList");
        f3.setAccessible(true);
        f3.set(finder, new ArrayList<>());

        plugin.getCore().getConfig().whitelistAdd("github.com");
    }

    @Test
    public void checkSpam() {
        Assume.assumeNotNull(isSpam);
        Assert.assertEquals(name.getMethodName() + " failed for " + message,
                isSpam, finder.checkForSpam(check));
    }
    
    @Test
    public void checkAdvertisement() {
        Assume.assumeNotNull(isAdvertisement);
        Assert.assertEquals(name.getMethodName() + " failed for " + message,
                isAdvertisement, finder.checkForAdvertising(check) == 1);
    }
    
    @Test
    public void checkCaps() {
        Assume.assumeNotNull(isCaps);
        Assert.assertEquals(name.getMethodName() + " failed for " + message,
                isCaps, finder.checkForCaps(check));
    }

}
