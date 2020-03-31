package com.terminal29;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class MCord extends JavaPlugin{

    static boolean initSuccess = true;

    private boolean isBotEnabled(){
        return this.getConfig().isSet("enabled") && this.getConfig().getBoolean("enabled");
    }

    private boolean doPostStatusMessages(){
        return this.getConfig().isSet("postStatusMessages") && this.getConfig().getBoolean("postStatusMessages");
    }

    @Nullable
    private String getBotToken(){
        if(!this.getConfig().isSet("token") || this.getConfig().getString("token") == null || this.getConfig().getString("token").length() == 0){
            return null;
        }
        return this.getConfig().getString("token");
    }

    @Nullable Long getBotChannel(){
        if(!this.getConfig().isSet("channelID") || this.getConfig().getLong("channelID") == 0){
            return null;
        }
        return this.getConfig().getLong("channelID");
    }

    private boolean startBot() throws Exception {
        if (!isBotEnabled()) {
            return false;
        }
        getLogger().info("MCord starting up");

        String botToken = getBotToken();
        Long botChannelID = getBotChannel();
        boolean doPostStatusMessages = doPostStatusMessages();

        if (botToken == null) {
            getLogger().log(Level.SEVERE, "Cannot find valid bot token string. Please set 'token' field in config file");
            initSuccess = false;
        }
        if (botChannelID == null) {
            getLogger().log(Level.SEVERE, "Cannot find valid bot channelID number. Please set 'channelID' field in config file");
            initSuccess = false;
        }


        if (initSuccess) {
            DiscordBot.initBot(botToken, botChannelID, doPostStatusMessages);
            initSuccess = true;
        }

        return initSuccess;
    }

    private void stopBot(){
        if(!isBotEnabled()){
            return;
        }
        DiscordBot.cleanupBot();
    }

    @Override
    public void onEnable() {
        boolean result = false;
        try {
            result = startBot();
        } catch (Exception e){
            getLogger().log(Level.SEVERE, e.getMessage());
        }

        if(result){
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


        this.getCommand("mcord").setTabCompleter((sender, command, alias, args) -> {
            List<String> list = new ArrayList<>();
            if(args.length == 1){
                list.add("reload");
                list.add("set");
            }else if(args.length == 2){
                if(args[1].equalsIgnoreCase("set")){
                    list.add("token");
                    list.add("channelID");
                }
            }else if(args.length == 3){
                if(args[2].equalsIgnoreCase("token")){
                    list.add("<token>");
                }else if(args[2].equalsIgnoreCase("channelID")){
                    list.add("<channelID>");
                }
            }
            return list;
        });
        this.getCommand("mcord").setExecutor((sender, command, label, args) -> {
            if(args.length > 0){
                if(args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("mcord.reload")) {
                    sender.sendMessage("Reloading MCord...");
                    stopBot();
                    try {
                        boolean restartResult = startBot();
                        if(restartResult) {
                            sender.sendMessage("Reload complete");
                        }else{
                            sender.sendMessage("Failed to reload");
                            sender.sendMessage("See console for more info");
                        }
                    }catch(Exception e){
                        sender.sendMessage("Failed to reload");
                        sender.sendMessage(e.getMessage());
                    }
                }else if(args[0].equalsIgnoreCase("set") && sender.hasPermission("mcord.set")){
                    if(args.length == 3){
                        if(args[1].equalsIgnoreCase("token")){
                            this.getConfig().set("token", args[2]);
                            sender.sendMessage("Updated bot token");
                            sender.sendMessage("To apply changes, run /mcord reload");
                        }if(args[1].equalsIgnoreCase("channelID")){
                            try {
                                this.getConfig().set("channelID", Long.parseLong(args[2]));
                                sender.sendMessage("Updated bot channel id");
                                sender.sendMessage("To apply changes, run /mcord reload");
                            }catch(Exception e){
                                sender.sendMessage("Invalid channel id");
                                sender.sendMessage(e.getMessage());
                            }

                        }else{
                            sender.sendMessage("Invalid command");
                            sender.sendMessage("Please refer to the command list by running /mcord");
                        }
                    }else{
                        sender.sendMessage("Invalid command");
                        sender.sendMessage("Please refer to the command list by running /mcord");
                    }
                }
                else{
                    sender.sendMessage("You don't have permission to run this command");
                }
            }else{
                sender.sendMessage("/mcord - Shows this list of available commands");
                sender.sendMessage("/mcord set token <token> - Sets the token string the bot will use to connect (dont use quotes)");
                sender.sendMessage("/mcord set channelID <channelID> - Sets the channel is number the bot will use to connect");
                sender.sendMessage("/mcord reload - Reloads MCord");
            }
            return true;
        });
    }


    @Override
    public void onDisable() {
        stopBot();
        this.saveConfig();
    }

}
