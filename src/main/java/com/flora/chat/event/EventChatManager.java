package com.flora.chat.event;

import com.flora.chat.Reference;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventChatManager implements Listener
{
    @EventHandler
    private void onChatEvent(AsyncPlayerChatEvent event)
    {
        event.setCancelled(true);

        onSendMessage(event.getPlayer(), event.getMessage());
    }

    private void onSendMessage(Player player, String message)
    {
        int channelCode = Reference.playerChatChannel.get(player.getUniqueId().toString());

        switch (channelCode) {
            case 0:
                Object[] o = Reference.playerList.get(player);
                String village = (String) o[0];

                if (village.isEmpty())
                    village = Reference.defaultVillage;

                assert village != null;
                String type = Reference.defaultChatType.replaceAll("%p%", player.getDisplayName()).replaceAll("%v%", village).replaceAll("&", "§");

                String messageA = type + " §r" + message;

                Reference.LOG.info("<" + player.getName() + "> " + message);
                DiscordSRV.getPlugin().getMainTextChannel().sendMessage(player.getName() + " >> " + message).queue();

                for (Player p : Bukkit.getOnlinePlayers())
                    p.sendMessage(messageA);
                break;

            case 1:
                String v = (String) Reference.playerList.get(player)[0];
                String color =  Reference.villageChatColor.get(v);

                String messageB = color + "<마을채팅> §r" + player.getDisplayName() + " : " + color + message.replaceAll("&", "§");


                LOG("<" + player.getName() + "> " + message, v);
                Reference.LOG.info("[VillageChat] <" + player.getName() + "> " + message);

                for (Player p : Reference.playerList.keySet()) {
                    if (Reference.playerList.get(p)[0].equals(v))
                        p.sendMessage(messageB);
                }
                break;

            case 2:
                String messageC = "§d<GM> §r" + player.getDisplayName() + " : §d" + message.replaceAll("&", "§");

                Reference.LOG.info("[GM] <" + player.getName() + "> " + message);

                for (Player p : Bukkit.getOnlinePlayers())
                    if (p.isOp())
                        p.sendMessage(messageC);
                break;
        }
    }


    private void LOG(String message, String village)
    {
        /*
        * Thank you for TheGamingGrunts
        * https://www.spigotmc.org/threads/logging.130416/#post-1384664
        */

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        String path = Reference.PLUGIN.getDataFolder().getPath() + "\\chat_Log" + "\\" + village;
        String date = form.format(new Date(System.currentTimeMillis()));

        File file = new File(path, date + ".log");

        if (!file.exists()) {
            file.mkdirs();

            if (!(file.isFile()))
                file.delete();
        }

        write(message, file);
    }

    private void write(String message, File file)
    {
        try {
            SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss");
            PrintWriter w = new PrintWriter(new FileWriter(file, true));

            w.write("[" + form.format(new Date()) + "] " + message);
            w.println();
            w.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}