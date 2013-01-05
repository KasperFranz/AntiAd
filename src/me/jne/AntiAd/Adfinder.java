package me.jne.AntiAd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Adfinder implements Listener {
	AntiAd plugin;
	public Adfinder(AntiAd instance){
	plugin = instance;
	}
	private final static Pattern ipPattern = Pattern.compile("([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
	private final static Pattern webpattern = Pattern.compile("(http://)?(www)?\\S{2,}((\\.com)|(\\.net)|(\\.org)|(\\.co\\.uk)|(\\.tk)|(\\.info)|(\\.es)|(\\.de)|(\\.arpa)|(\\.edu)|(\\.firm)|(\\.int)|(\\.mil)|(\\.mobi)|(\\.nato)|(\\.to)|(\\.fr)|(\\.ms)|(\\.vu)|(\\.eu)|(\\.nl))");
	private final static Pattern spamPattern = Pattern.compile("((\\S{20,})|([A-Z]{3,}\\s){3,})");
	public HashMap<Player, Integer> warn = new HashMap<Player, Integer>();
	public boolean ipurl = false;
	public boolean spam = false;
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent chat) {
		ipurl = false;
		spam = false;
		String BanTypead = plugin.getConfig().getString("Command-Ad");
		String BanTypespam = plugin.getConfig().getString("Command-Spam");
		String AdMsg = plugin.getConfig().getString("Ad_Message");
		String SpamMsg = plugin.getConfig().getString("Spam_Message");
		String NMsg = plugin.getConfig().getString("Notification-Message");
		String Spam = plugin.getConfig().getString("Spam-Detection-On");
		String BanSpam = plugin.getConfig().getString("Reason-Spam");
		String BanAd = plugin.getConfig().getString("Reason-Ad");
		String Url = plugin.getConfig().getString("URL-Detection");
		String StealthMode = plugin.getConfig().getString("Stealth-Mode");
		Player player = chat.getPlayer();
		String Time = plugin.getConfig().getString("Time");
		String[] arr = BanTypead.split("\\s+");
		String[] arr1 = BanTypespam.split("\\s+");
		String BanTAd = arr[0];
		String BanTSpam = arr1[0];
		String Ending = "ed";
		String Ending1 = "ed";
		final String PlayerName = player.getName();
		final String DisplayName = player.getServer().getPlayer(PlayerName).getDisplayName();
		try{
		BanTypead = BanTypead.replace("<reasonad>", BanAd); 
		}catch(NullPointerException n){	
		}
		try{
		BanTypead = BanTypead.replace("<time>", Time) ;
		}catch(NullPointerException n){	
		}
		try{
		BanTypead = BanTypead.replace("<player>", PlayerName);
		}catch(NullPointerException n){	
		}
		String message = chat.getMessage();
		
		if(BanTAd.endsWith("e")){
		Ending = "d";	
		}
		if(BanTSpam.endsWith("e")){
		Ending1 = "d";
		}
		
		
		
		
		if(warn.containsKey(player)){
		}else{
			if(warn != null){
			warn.put(player, 0);
			}
		}
		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader("plugins/AntiAd/Whitelist.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		List<String> lines = new ArrayList<String>();
		String line = null;
		try {
			while((line = read.readLine())!=null){lines.add(line);}
		} catch (IOException e) {
			 e.printStackTrace();
		}
		Matcher regexMatcher = ipPattern.matcher(message);
		while(regexMatcher.find()){
		 if(regexMatcher.group().length() != 0) {
		  String ip = regexMatcher.group().trim();	
		final String command = BanTypead;
				
  if(lines.contains(ip)){
	  
  }else{
		if(ipPattern.matcher(message).find())  {
			ipurl = true;
			if(player.hasPermission("antiad.bypass.ad")) {
				}else{
						log(DateTime.now("MMM dd,yyyy HH:mm ") + DisplayName + " has advertised: " + message + ", in chat.");
						Bukkit.getServer().getLogger().info("[AntiAd] " + DisplayName + " was logged for Advertising in chat.");
						if(warn.get(player) == 2){
							warn.put(player, 0);
					    if(NMsg.equalsIgnoreCase("true")){
					    Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has been " + BanTAd + Ending + " for Advertising");
						}
					    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
					    chat.setCancelled(true);
						}else{
						warn.put(player, warn.get(player) + 1);
						if(StealthMode.equalsIgnoreCase("true")){
							for(Player player1 : Bukkit.getServer().getOnlinePlayers()){
							 if(player != player1){
							  chat.getRecipients().remove(player1);
							 }
							}
						}else{
						chat.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!" );
						player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + AdMsg);
						}
						}
		for(Player player1: Bukkit.getServer().getOnlinePlayers()) {
			if(player1.hasPermission("antiad.see")) {
		    player1.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has advertised" + " " + message);
		           }
		           }
				  }
				 }
		        }
		       }
		      } 
		Matcher regexMatcherurl = webpattern.matcher(message);
		while(regexMatcherurl.find()){
		 if(regexMatcherurl.group().length() !=0) {
		  String url = regexMatcherurl.group().trim();	
		  final String command = BanTypead;
  if(lines.contains(url)){
	  
  }else{
		if(webpattern.matcher(message).find())  {
			if(Url.contains("true")){
			if(ipurl == true){
				
				spam = true;
				
			}else{
			if(player.hasPermission("antiad.bypass.ad")) {
				}else{
						log(DateTime.now("MMM dd,yyyy HH:mm ") + DisplayName + " has advertised: " + message + ", in chat.");
						Bukkit.getServer().getLogger().info("[AntiAd] " + DisplayName + " was logged for Advertising in chat.");
						if(warn.get(player) == 2){
							warn.put(player, 0);
					    if(NMsg.equalsIgnoreCase("true")){
					    Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has been " + BanTAd + Ending + " for Advertising");
						}
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command + " " + BanAd  + " " + Time);	
						chat.setCancelled(true);
						}else{
						warn.put(player, warn.get(player) + 1);
						if(StealthMode.equalsIgnoreCase("true")){
							for(Player player1 : Bukkit.getServer().getOnlinePlayers()){
							 if(player != player1){
							  chat.getRecipients().remove(player1);
							 }
							}
						}else{
						chat.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!" );
						player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + AdMsg);
						}
						}
		for(Player player1: Bukkit.getServer().getOnlinePlayers()) {
			if(player1.hasPermission("antiad.see")) {
		    player1.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has advertised" + " " + message);
					}
		           }
		           }
				  }
				 }
		        }
		      }
		    }
		}
		
		try{
		BanTypespam = BanTypespam.replace("<reasonspam>", BanSpam); 
		}catch(NullPointerException n){	
		}
		try{
		BanTypespam = BanTypespam.replace("<time>", Time) ;
		}catch(NullPointerException n){	
		}
		try{
		BanTypespam = BanTypespam.replace("<player>", PlayerName);
		}catch(NullPointerException n){	
		}
		
		if(spam == true || ipurl == true){
		}else{
	if(Spam.equalsIgnoreCase("true")){
	 String spamnum = plugin.getConfig().getString("Spam-Number-Letters");
     String spamletterword = plugin.getConfig().getString("Spam-Number-Letters-Word");
	 String spamnumberword = plugin.getConfig().getString("Spam-Number-Words");
	 //Pattern spamPattern = Pattern.compile("(\\S{" + Pattern.quote(spamnum) + ",}) || (([A-Z]{" + Pattern.quote(spamletterword) + ",}\\s){" + Pattern.quote(spamnumberword) + ",})");
     final Pattern spamPattern = Pattern.compile("((\\S{" + Pattern.quote(spamnum) + ",})|([A-Z]{" + Pattern.quote(spamletterword) + ",}\\s){" + Pattern.quote(spamnumberword) + ",})");
	 if(spamPattern.matcher(message).find() && !ipPattern.matcher(message).find() && !webpattern.matcher(message).find()){
		 if(player.hasPermission("antiad.bypass.spam")){
			 
		 }else{
	    final String command = BanTypespam;
		chat.setCancelled(true);
		log(DateTime.now("MMM dd,yyyy HH:mm ") + DisplayName + " has Spammed: " + message + ", in chat.");
	    Bukkit.getServer().getLogger().info("[AntiAd] " + DisplayName + " was logged for Spamming in chat.");
	    if(warn.get(player) == 2){
	    	warn.put(player, 0);
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		if(NMsg.equalsIgnoreCase("true")){
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has been " + BanTSpam + Ending1 + " for Spamming");
		}
		}else{
		warn.put(player, warn.get(player) + 1);		
		player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!" );
		player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + SpamMsg);
	    }
		}
	    }
	   }
	 }
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onCommandSent(PlayerCommandPreprocessEvent chat){
		String[] arr2 = chat.getMessage().split("\\s+");
		String CL = arr2[0];
		List<String> Commands = plugin.getConfig().getStringList("Detected-Commands");
		if(Commands.contains(CL)){
		ipurl = false;
		spam = false;
		String BanTypead = plugin.getConfig().getString("Command-Ad");
		String BanTypespam = plugin.getConfig().getString("Command-Spam");
		String AdMsg = plugin.getConfig().getString("Ad_Message");
		String SpamMsg = plugin.getConfig().getString("Spam_Message");
		String NMsg = plugin.getConfig().getString("Notification-Message");
		String Spam = plugin.getConfig().getString("Spam-Detection-On");
		String BanSpam = plugin.getConfig().getString("Reason-Spam");
		String BanAd = plugin.getConfig().getString("Reason-Ad");
		String Url = plugin.getConfig().getString("URL-Detection");
		Player player = chat.getPlayer();
		String Time = plugin.getConfig().getString("Time");
		String StealthMode = plugin.getConfig().getString("Stealth-Mode");
		String[] arr = BanTypead.split("\\s+");
		String[] arr1 = BanTypespam.split("\\s+");
		String BanTAd = arr[0];
		String BanTSpam = arr1[0];
		String Ending = "ed";
		String Ending1 = "ed";
		final String PlayerName = player.getName();
		final String DisplayName = player.getServer().getPlayer(PlayerName).getDisplayName();
		try{
		BanTypead = BanTypead.replace("<reasonad>", BanAd); 
		}catch(NullPointerException n){	
		}
		try{
		BanTypead = BanTypead.replace("<time>", Time) ;
		}catch(NullPointerException n){	
		}
		try{
		BanTypead = BanTypead.replace("<player>", PlayerName);
		}catch(NullPointerException n){	
		}
		String message = chat.getMessage();
		
		if(BanTAd.endsWith("e")){
		Ending = "d";	
		}
		if(BanTSpam.endsWith("e")){
		Ending1 = "d";
		}
		
		
		
		
		if(warn.containsKey(player)){
		}else{
			if(warn != null){
			warn.put(player, 0);
			}
		}
		BufferedReader read = null;
		try {
			read = new BufferedReader(new FileReader("plugins/AntiAd/Whitelist.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		List<String> lines = new ArrayList<String>();
		String line = null;
		try {
			while((line = read.readLine())!=null){lines.add(line);}
		} catch (IOException e) {
			 e.printStackTrace();
		}
		Matcher regexMatcher = ipPattern.matcher(message);
		while(regexMatcher.find()){
		 if(regexMatcher.group().length() != 0) {
		  String ip = regexMatcher.group().trim();	
		final String command = BanTypead;
				
  if(lines.contains(ip)){
	  
  }else{
		if(ipPattern.matcher(message).find())  {
			ipurl = true;
			if(player.hasPermission("antiad.bypass.ad")) {
				}else{
						log(DateTime.now("MMM dd,yyyy HH:mm ") + DisplayName + " has advertised: " + message + ", through the command " + CL);
						Bukkit.getServer().getLogger().info("[AntiAd] " + DisplayName + " was logged for Advertising through a command.");
						if(warn.get(player) == 2){
							warn.put(player, 0);
					    if(NMsg.equalsIgnoreCase("true")){
					    Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has been " + BanTAd + Ending + " for Advertising");
						}
					    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
					    chat.setCancelled(true);
						}else{
						warn.put(player, warn.get(player) + 1);
						if(StealthMode.equalsIgnoreCase("true")){
							chat.setCancelled(true);
							player.sendMessage(ChatColor.RED + "An internal error has occurred while attempting to perform this command");
							}else{
						chat.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!" );
						player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + AdMsg);
						}
						}
		for(Player player1: Bukkit.getServer().getOnlinePlayers()) {
			if(player1.hasPermission("antiad.see")) {
		    player1.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has advertised" + " " + message);
		           }
		           }
				  }
				 }
		        }
		       }
		      } 
		Matcher regexMatcherurl = webpattern.matcher(message);
		while(regexMatcherurl.find()){
		 if(regexMatcherurl.group().length() !=0) {
		  String url = regexMatcherurl.group().trim();	
		  final String command = BanTypead;
  if(lines.contains(url)){
	  
  }else{
		if(webpattern.matcher(message).find())  {
			if(Url.contains("true")){
			if(ipurl == true){
				
				spam = true;
				
			}else{
			if(player.hasPermission("antiad.bypass.ad")) {
				}else{
						log(DateTime.now("MMM dd,yyyy HH:mm ") + DisplayName + " has advertised: " + message + ", through the command " + CL);
						Bukkit.getServer().getLogger().info("[AntiAd] " + DisplayName + " was logged for Advertising through a command.");
						if(warn.get(player) == 2){
							warn.put(player, 0);
					    if(NMsg.equalsIgnoreCase("true")){
					    Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has been " + BanTAd + Ending + " for Advertising");
						}
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command + " " + BanAd  + " " + Time);	
						chat.setCancelled(true);
						}else{
						warn.put(player, warn.get(player) + 1);
						if(StealthMode.equalsIgnoreCase("true")){
							player.sendMessage(ChatColor.RED + "An internal error has occurred while attempting to perform this command");
							}else{
						chat.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You have " + warn.get(player) + "/3 chances left!" );
						player.sendMessage(ChatColor.DARK_GREEN + "[AntiAd]" + " " + ChatColor.RED + AdMsg);
						}
						}
		for(Player player1: Bukkit.getServer().getOnlinePlayers()) {
			if(player1.hasPermission("antiad.see")) {
		    player1.sendMessage(ChatColor.DARK_GREEN + "[AntiAd] " + ChatColor.RED + DisplayName + ChatColor.DARK_GREEN + " has advertised" + " " + message);
					}
		           }
		           }
				  }
				 }
		        }
		      }
		    }
		   }
		  }
	     }
		
	public void log(String message){
		try{
			BufferedWriter write = new BufferedWriter (new FileWriter("plugins/AntiAd/Log.txt", true));
			write.append(message);
			write.newLine();
			write.flush();
			write.close();
			}catch (IOException e){
				e.printStackTrace();
		}
	}
}
