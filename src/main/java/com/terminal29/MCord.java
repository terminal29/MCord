package com.terminal29;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class MCord extends JavaPlugin implements Listener {

    static boolean initSuccess = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MCord starting up");

        if(!this.getConfig().isSet("token") || this.getConfig().getString("token") == null){
            getLogger().log(Level.SEVERE, "Cannot find bot token string. Add 'token' field to config file");
            this.saveDefaultConfig();
            initSuccess = false;
        }if(!this.getConfig().isSet("channelID")){
            getLogger().log(Level.SEVERE, "Cannot find channelID number. Add 'channelID' field to config file");
            this.saveDefaultConfig();
            initSuccess = false;
        }else{
            initSuccess = true;
        }

        if(initSuccess){
            try{
                DiscordBot.setToken(this.getConfig().getString("token"));
                DiscordBot.setChannel(this.getConfig().getLong("channelID"));
                DiscordBot.initBot();
                initSuccess = true;
            }catch(Exception e){
                getLogger().log(Level.SEVERE, "Bot failed to init");
                getLogger().log(Level.SEVERE, e.getMessage());
                initSuccess = false;
            }
        }

        if(initSuccess){
            getLogger().info("Bot is now online");
            getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onChat(AsyncPlayerChatEvent ev) {
                    Bukkit.getScheduler().runTask(MCord.this, () -> {
                        getLogger().info(String.format("[%s] %s", ev.getPlayer().getDisplayName(), ev.getMessage()));
                        DiscordBot.onMCUserMessage(ev.getPlayer().getDisplayName(), ev.getMessage());
                    });
                }
            }, this);
        }
    }

    @Override
    public void onDisable() {
        DiscordBot.cleanupBot();
    }


}
