# Changelog
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/), and following the format from [keep a CHANGELOG](http://keepachangelog.com/)

## [Unreleased][unreleased]
### Added

### Changed

### Fixed


## [2.3.2] - 2016-03-17
### Added
 - Added a check for new version
 - New build message on join (permission node for this is added)

### Changed
 - No need to be specific in the pom.
 
### Fixed
 - Now possible to run with java 7 after implementing the version class.

## [2.3.1b] - 2016-03-05
### Changed
 - Update to 1.9.

### Fixed
 - Fix error of Metrics.


## [2.3.1a] - 2016-03-01
### Added
 - moved my debug into a config option
 - Added a simple URL detection.
 
### Changed
 - Cleanup in the languages setup
 
### Fixed
 - Warn admin now works

## [2.3.1] - 2016-02-27
### Added
 - Added default values to the new variables, should be added on startup.
 
### Changed
 - Removed the join messages

### Fixed
 - Removed debug message.

## [2.3.0] - 2016-02-15
### Added
 - Start aliases is now possible, see #49 for more information
 - Checking if the command exists before trying to execute it.
 - We now check if the books contains spam, links etc.

### Fixed
 - Whitelist accepted "this is my advertisement for xx.yy on this server server.com" if you had server.com on the whitelist.
 - If the word length is set to 0 it isn't possible to use the chat system.

 
## [2.2.5] -  2014-02-23 
 - Fixed the bugs.
 - Added language (CN) Chinese Language. Thank to Mayomi.
 - Added language (TR) Turkish Language. Thank to MrBaklava. 


## [2.2.1] -  2014-01-26
 - Fixed the bug of the hi... or ...
 - Added language (RU) Russian. Thank to MySt1k. 


## [2.2.0] -  2013-10-05
 - Fixed the advertising bugs
 - made some changes to the spam patterns (now it shouldn't make 3:15 3,15 etc to spam (but 3.1, 3.2 is triggered as Ip advertising)
 - Made changes to the Ip & Web pattern so it shouldn't pick up wierd things.
 - Made the pattern loo at nearly everything
 - Added language  (DE,DA,FR,PL)

## [2.1.1] -  2013-04-20
 - Fixed faction chat bug. - Fixed the bug where if Faction cancelled the chat message and we worked on it after then we re-enabled it if it wasn't spam/advertising. 


## [2.1.0] - 2013-03-13 
 - At some point when ppl type a long link it got outgoing as spam (if the link was a whitelisted link) ex.
 -  - http://dev.bukkit.org/server-mods/antiad/ This should work now :)
 -  - http://dev.bukkit.org and dev.bukkit.org should now be handled as thesame :)
 -  - http://dev.bukkit.org/server-mods/antiad/ and http://dev.bukkit.org/server-mods are both whitelisted if you whitelist dev.bukkit.org.
 - Better web pattern


## [2.0.0] - 2013-02-14
 - Spam pattern fixed
 - Advertising pattern fixed
 - And a lot of rewrite on the codes.
 - Stop IP/URL on Sign. Idea of red0fireus.
