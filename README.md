# AuthMeProxy
Brings AuthMe support to your BungeeCord **and** Velocity servers!

## Content
* [About](#about)
* [Support](#support)
* [Requirements](#requirements)
* Downloads (*Soon*)
* [Installation](#installation)

## About
This is a fork of [AuthMeBungee](https://github.com/AuthMe/AuthMeBungee) with added support for [Velocity](https://papermc.io/software/velocity) proxy software.

*Description copied from AuthMeBungee's README*
> This plugin acts like a bridge between your bukkit servers and your BungeeCord instance. To explain simple how it works, bukkit-side plugins send a message to bungee-side on user authentication. If bungee-side doesn't receive this message, the player won't be able to talk in chat and to perform commands, including BungeeCord commands.  
>
> **AutoLogin:** If you have AuthMe on multiple servers, you can enable autoLogin option that allow you to switch between your servers without having to perform login command again.

## Support
- [GitHub issue tracker](https://github.com/Defective4/AuthMeProxy/issues)
- Spigot page (*Soon*)
- ~~Discord~~ (None at the moment)
> [!CAUTION]
> Please do **not** ask for support with this fork on AuthMeReloaded Discord.

## Requirements

**BungeeCord** (and forks)
- Java 1.8+
- BungeeCord/Waterfall/Travertine 1.7+

**Velocity**
- Java 17+
- Velocity (tested on 3.3.0)

**Bukkit Bridge for Velocity**
- Spigot or any of its forks (Tested on Paper and Purpur 1.12+, any version above 1.7 should work)

## Installation

**BungeeCord**  
This part of is mostly the same as with original AuthMeBungee
1. Download AuthMeProxy-Bungee-`x.x`.jar package
2. Place AuthMeProxy-Bungee-`x.x`.jar into your BungeeCord's plugins folder
3. Restart everything
4. Configure the plugin **(don't forget to config authServers)**
5. Enable the **Hooks.bungeecord** option in your **AuthMeReloaded config file**
6. Enjoy!

**Velocity**  
Velocity installation requires installing the core plugin on your Velocity server and a bridge on all your auth servers.
1. Download *AuthMeProxy-Velocity-`x.x`.jar* package
2. Place *AuthMeProxy-Velocity-`x.x`.jar* in your Velocity's plugins folder
3. Download AuthMe-Velocity-Bridge-`x.x`.jar
4. Copy *AuthMe-Velocity-Bridge-`x.x`.jar* to all auth servers' plugins folder
5. Restart both Velocity and all auth servers
5. Configure the plugin **(don't forget to config authServers)**
6. Enable the **Hooks.bungeecord** option in your **AuthMeReloaded config file**
7. Enjoy!

**Please follow these steps and configure the plugin before saying it doesn't work!**
