# MCord
A super simple Discord &lt;-> Spigot/Paper chat relay plugin

# Summary

This is a plugin for Spigot/Paper that creates a Discord bot to act as a 2 way chat relay from your Discord server to your Minecraft server. Tested on Paper 1.15.2 #146.

To get the plugin working for the first time you need to create a discord bot and set the token and channel id by editing the `config.yml` or by using `/mcord set channelID <channel id>` and `/mcord set token <token>` in-game.

You can also use `/help` and `/online` from within the Discord channel to display the list of commands, and to list who is online on the MC server.

# Permissions
```yaml
mcord.list # list available commands
mcord.set # set & update token and channel id
mcord.reload # reload the bot
```

# Config
```yaml
token: string # bot token string, default ""
channelID: number # channel id number, default 0
enabled: bool # enable or disable the plugin, default true
showStatusMessages: bool # whether to post bot status messages to the discord server, default false
```
