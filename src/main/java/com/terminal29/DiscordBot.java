package com.terminal29;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Collection;

public class DiscordBot implements EventListener {
    static JDA _bot = null;
    static String _token = null;
    static long _channelID = -1;
    static boolean _botReady = false;
    static boolean _showStatusMessages = false;

    static void initBot(String token, long channelID, boolean showStatusMessages) throws Exception {
        if(_bot == null){
            _token = token;
            _channelID = channelID;
            _showStatusMessages = showStatusMessages;

            JDABuilder builder = new JDABuilder(_token);
            builder.setActivity(Activity.playing("Minecraft"));
            builder.addEventListeners(new DiscordBot());
            _bot = builder.build();
        }
    }

    static boolean isBotReady(){
        return _botReady;
    }

    static void cleanupBot(){
        if(_bot != null){
            TextChannel channel = _bot.getTextChannelById(_channelID);
            if(channel != null && _showStatusMessages)
                channel.sendMessage("MCord going offline o/").queue();
            _bot.shutdown();
            _bot = null;
        }
    }

    static void onMCUserMessage(String user, String message){
        if(_bot != null) {
            TextChannel channel = _bot.getTextChannelById(_channelID);
            if (channel != null)
                channel.sendMessage(String.format("> **%s:** %s", user, message)).queue();
        }
    }

    static void onDiscordMessage(String user, String message){
        Bukkit.broadcastMessage(String.format("\u00A79<%s>\u00A7f %s", user, message));
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {

            TextChannel channel = _bot.getTextChannelById(_channelID);
            if(channel != null && _showStatusMessages)
                channel.sendMessage("MCord online \\o").queue();
            _botReady = true;
        }
        if(event instanceof MessageReceivedEvent){
            MessageReceivedEvent evt = (MessageReceivedEvent)event;
            if(!evt.getAuthor().isBot() && evt.getChannel().getIdLong() == _channelID){
                TextChannel channel = evt.getTextChannel();
                Member member  = evt.getMember();
                String message = evt.getMessage().getContentStripped();
                if(message.startsWith("/online")){
                    Collection<? extends Player> players =  Bukkit.getServer().getOnlinePlayers();
                    ArrayList<String> messages = new ArrayList<>();
                    messages.add("**Currently online:**");
                    for(Player player : players){
                        messages.add(player.getDisplayName());
                    }
                    if(players.size() == 0){
                        messages.add("*crickets...*");
                    }
                    channel.sendMessage(String.join("\n", messages)).queue();
                }else if(message.startsWith("/help")){
                    ArrayList<String> messages = new ArrayList<>();
                    messages.add("**Commands:**");
                    messages.add("/help - shows this help message");
                    messages.add("/online - shows who is online on the server");

                    channel.sendMessage(String.join("\n", messages)).queue();
                }else{
                    onDiscordMessage( (member!=null)?member.getEffectiveName():"Unknown User", message);
                }
            }
        }
    }
}
