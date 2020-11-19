package com.flora.chat.event;

import com.flora.chat.Reference;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;

import java.time.LocalDate;

public class EventDataManager implements Listener
{
    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!(Reference.getDataFile(uuid).canRead()))
            onCreateNewData(player, uuid);

        Reference.updatePlayerData(player);
        Reference.updatePlayerChannel(player, 0);

        if (player.isOp()) {
            FileConfiguration config = Reference.getDataConfig(uuid);
            Reference.OpChatViewMod.put(player, config.getBoolean("viewMod"));
        }
    }

    @EventHandler
    private void onPlayerExitEvent(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        Reference.removePlayerData(player);
        Reference.removePlayerChannel(player);

        if (player.isOp())
            Reference.OpChatViewMod.remove(player);
    }



    @EventHandler
    private void onServerReloadEvent(ServerLoadEvent event)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            Reference.updatePlayerData(p.getUniqueId().toString());
            Reference.updatePlayerChannel(p, 0);

            if (p.isOp()) {
                FileConfiguration config = Reference.getDataConfig(p.getUniqueId().toString());
                Reference.OpChatViewMod.put(p, config.getBoolean("viewMod"));
            }
        }
    }

    private void onCreateNewData(Player player, String uuid)
    {
        FileConfiguration config = Reference.getDataConfig(uuid);

        config.set("name", player.getName());
        config.set("uuid", uuid);
        config.set("header", false);
        config.set("village", "");
        config.set("createCount", 0);
        config.set("date", LocalDate.now().toString());
        config.set("viewMod", false);

        Reference.saveDataFile(config, Reference.getDataFile(uuid));
    }
}
