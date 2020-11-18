package com.flora.chat.event;

import com.flora.chat.Reference;
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
        Reference.updatePlayerChannel(uuid, 0);
    }

    @EventHandler
    private void onPlayerExitEvent(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        Reference.removePlayerData(player);
        Reference.removePlayerChannel(uuid);
    }



    @EventHandler
    private void onServerReloadEvent(ServerLoadEvent event)
    {
        Reference.updateAllPlayerData();
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

        Reference.saveDataFile(config, Reference.getDataFile(uuid));
    }
}
