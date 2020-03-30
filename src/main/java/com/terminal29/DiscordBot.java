package com.terminal29;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class DiscordBot implements EventListener {
    static JDA _bot = null;
    static String _token = null;
    static long _channelID = -1;
    static boolean _botReady = false;

    static void setToken(String token){
        _token = token;
    }

    static void setChannel(long channel){
        _channelID = channel;
    }

    static void initBot() throws Exception {
        if(_bot == null){
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
            channel.sendMessage("Bot shutting down").queue();
        }
    }

    static void onMCUserMessage(String user, String message){
        TextChannel channel = _bot.getTextChannelById(_channelID);
        channel.sendMessage(String.format("[%s] %s", user, message)).queue();
    }

    static void onDiscordMessage(String user, String message){
        Bukkit.broadcastMessage(String.format("[Discord: %s]: %s", user, message));
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {

            TextChannel channel = _bot.getTextChannelById(_channelID);
            channel.sendMessage("Bot starting up").queue();
            _botReady = true;
        }
        if(event instanceof MessageReceivedEvent){
            MessageReceivedEvent evt = (MessageReceivedEvent)event;
            if(!evt.getAuthor().isBot() && evt.getChannel().getIdLong() == _channelID){
                onDiscordMessage(evt.getMember().getEffectiveName(), evt.getMessage().getContentDisplay());
            }
        }
    }
}
