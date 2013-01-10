/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.jne.AntiAd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Securator
 */
public class AntiAdTest {
    
    public AntiAdTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of onDisable method, of class AntiAd.
     */
    @Test
    public void testOnDisable() {
        System.out.println("onDisable");
        AntiAd instance = new AntiAd();
        instance.onDisable();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onEnable method, of class AntiAd.
     */
    @Test
    public void testOnEnable() {
        System.out.println("onEnable");
        AntiAd instance = new AntiAd();
        instance.onEnable();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onCommand method, of class AntiAd.
     */
    @Test
    public void testOnCommand() {
        System.out.println("onCommand");
        CommandSender sender = null;
        Command cmd = null;
        String commandLabel = "";
        String[] args = null;
        AntiAd instance = new AntiAd();
        boolean expResult = false;
        boolean result = instance.onCommand(sender, cmd, commandLabel, args);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
