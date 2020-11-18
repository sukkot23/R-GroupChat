package com.flora.chat;

import com.flora.chat.command.CommandGroupChat;
import com.flora.chat.command.CommandGroupVillage;
import com.flora.chat.command.TabCompleterGroupVillage;
import com.flora.chat.event.EventChatManager;
import com.flora.chat.event.EventDataManager;
import com.flora.chat.event.EventInventoryClick;
import org.bukkit.Bukkit;
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

    }
}
