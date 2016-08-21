AntiAd
======
[![Build Status][buildSVG]][buildLink]
[![Download][bukkitSVG]][bukkitLink]
[![Download][spigotSVG]][spigotLink]

The goal of AntiAd is to help you maintain a server where people don't advertise, spam or write in all caps.  
When a player is sending advertisement or spam in the chat it shows a message to the people with the permission `antiad.see` the advertised/spammy message - The advertising/spammy player gets a warning and a message that spam/advertisement isn't allowed (the message can be changed in the config).  
We are doing this by giving everyone n (default 3) chances (resets with a server restart), when they used their 3 chances there a executed a command (the command can be set in the config).  

**but Isn't my own website blocked then?**  
 By default everything is blocked, you can however add items to the whitelist with the command `antiad add` website, it is there possible to use * to allow more items.
Let's say you have a forum on forum.website.com and you want everything on the forum to be whitelisted, then you run the command `antiad add forum.website.com/*` then everything is allowed on that website (where the links begins with forum.website.com).

**We have 2 different webpatterns:**  
* **Strict**: Blocks every TLD, it also sometimes blocks something there are shown not to be a domain
* **Simple**: Blocks 50 of the most common TLDs It can be activated by chainging `useSimpleWebPattern` to true in the config.


##Languages

The languages are community driven so if you want a language added to AntiAd, please make a PullRequest with the properties file.  
You can set the language in the `Language` part of the config.yml, the diffent languages are listed below.

Languages | ShortCode
--- | ---
 Simplified Chinese | `cn`      |
 Danish             | `da`      |
 German             | `de`      |
 Spanish            | `es`      |
 French             | `fr`      |
 Polish             | `pl`      |
 Russian            | `ru`      |
 Turkish            | `tr`      |
 Hungarian          | `hu`      | 

Thanks to Mayomi, kasperfranz, AlexMl, XxCoolgamesxX, MySt1k, metLuna, HunLux for the awesome translations


##Usage of AntiAd

We monitor the usage of the plugin with [MCStats][MCStatsLink] - this is to help us see which version of AntiAd and what version of Minecraft you  - all this is so we can make sure the plugin is working with your version of Spigot/Bukkit and Minecraft.  
![Usage statistics][stats]



[stats]: https://i.mcstats.org/AntiAd/Global+Statistics@2x.borderless.png
[buildSVG]: https://travis-ci.org/antiAD/AntiAd.svg
[buildLink]: https://travis-ci.org/antiAD/AntiAd
[bukkitSVG]: https://img.shields.io/badge/Download-Bukkit-blue.svg
[bukkitLink]: https://dev.bukkit.org/server-mods/antiad/
[spigotSVG]: https://img.shields.io/badge/Download-SpigotMC-orange.svg
[spigotLink]: https://www.spigotmc.org/resources/antiad.323/
[MCStatsLink]: http://mcstats.org/plugin/AntiAd
