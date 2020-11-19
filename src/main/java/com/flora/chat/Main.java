package com.flora.chat;

import com.flora.chat.command.CommandGroupChat;
import com.flora.chat.command.CommandGroupVillage;
import com.flora.chat.command.TabCompleterGroupVillage;
import com.flora.chat.event.EventChatManager;
import com.flora.chat.event.EventDataManager;
import com.flora.chat.event.EventInventoryClick;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        Reference.getVillageChatColor();
        Reference.getDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new EventDataManager(), this);
        Bukkit.getPluginManager().registerEvents(new EventChatManager(), this);

        Bukkit.getPluginManager().registerEvents(new EventInventoryClick(), this);

        Objects.requireNonNull(Bukkit.getPluginCommand("village")).setExecutor(new CommandGroupVillage());
        Objects.requireNonNull(Bukkit.getPluginCommand("village")).setTabCompleter(new TabCompleterGroupVillage());

        Objects.requireNonNull(Bukkit.getPluginCommand("chat")).setExecutor(new CommandGroupChat());

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(JavaPlugin.getPlugin(Main.class), new TimeScheduler(), 20L, 40L);
    }

    static class TimeScheduler implements Runnable
    {
        @Override
        public void run() {
            if (Reference.playerChatChannel != null) {
                for (Player p : Reference.playerChatChannel.keySet()) {
                    int channelCode = Reference.playerChatChannel.get(p);

                    switch (channelCode) {
                        case 1:
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6[ ! ]§f 마을 채팅이 활성화 되어있습니다"));
                            break;

                        case 2:
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§d[ ! ]§f 운영진 채팅이 활성화 되어있습니다"));
                            break;
                    } } }
        }
    }
}
