package com.github.antiad.AntiAd;

import com.github.antiad.AntiAd.model.Config;
import com.github.antiad.AntiAd.model.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AdfinderTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // isSpam, isAdvertisement, isCaps, message
                {false, false, false, "Hello world"}, // Should be ok
                {false, true, false, "Join my server 127.0.0.1"}, // advertisement
                {true, false, false, "12121212121212121212121212121212121212"}, // spam
                {false, false, true, "ALL CAPS MESSAGE!"}, // caps
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
    private Core core;


    @Mock
    private FileConfiguration fileConfig;

    @Mock
    private Config config;

    private Adfinder finder;

    public AdfinderTest(Boolean isSpam, Boolean isAdvertisement, Boolean isCaps, String message) {
        this.isSpam = isSpam;
        this.isAdvertisement = isAdvertisement;
        this.isCaps = isCaps;
        this.message = message;
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(core.getPlugin()).thenReturn(plugin);
        Mockito.when(core.getConfig()).thenReturn(config);

        Mockito.when(plugin.getConfig()).thenReturn(fileConfig);
        Mockito.when(check.getMessage()).thenReturn(message);
        Mockito.when(config.isSpamDetection()).thenReturn(true);
        Mockito.when(config.isUrlDetection()).thenReturn(true);
        Mockito.when(config.isIPDetection()).thenReturn(true);
        Mockito.when(config.getNumbers()).thenReturn(20);
        Mockito.when(config.getProcentCapital()).thenReturn(80);
        Mockito.when(config.isCheckWordLenght()).thenReturn(true);

        ArrayList<String> list = new ArrayList<>();
        list.add("github.com");
        Mockito.when(config.getWhitelistLine()).thenReturn(list);
        finder = new Adfinder(core);

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
