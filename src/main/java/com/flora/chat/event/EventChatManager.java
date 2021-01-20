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
        Player player = event.getPlayer();
        String message = event.getMessage();

        int channelCode = Reference.playerChatChannel.get(player);

        switch (channelCode) {
            case 0:
                Object[] o = Reference.playerList.get(player);
                String village = (String) o[0];

                if (village.isEmpty()) {
                    village = Reference.defaultVillage;
                }
                else {
                    String vColor = Reference.villageChatColor.get(village);
                    village = vColor + village;
                }

                assert village != null;
                String type = Reference.defaultChatType
                        .replaceAll("%v%", village)
                        .replaceAll("%p%", player.getDisplayName())
                        .replaceAll("&", "§");

                String messageA = type + " §r" + message;

                Reference.LOG.info("<" + player.getName() + "> " + message);

                /* Discord SRV Connect */
                try { DiscordSRV.getPlugin().getMainTextChannel().sendMessage(player.getName() + " >> " + message).queue(); }
                catch (NullPointerException exception) { System.out.println("'DiscordSRV' 에 문제가 발견되었습니다"); }

                event.setFormat(type + " §r%2$s");
                break;

            case 1:
                event.setCancelled(true);

                String v = (String) Reference.playerList.get(player)[0];
                String color =  Reference.villageChatColor.get(v);

                String messageB = color + "<마을채팅> §r" + player.getDisplayName() + " : " + color + message;


                LOG("<" + player.getName() + "> " + message, v);
                Reference.LOG.info("[VillageChat] <" + player.getName() + "> " + message);

                for (Player p : Reference.playerList.keySet()) {
                    if (Reference.playerList.get(p)[0].equals(v))
                        p.sendMessage(messageB);
                }

                for (Player op : Reference.OpChatViewMod.keySet()) {
                    if (Reference.OpChatViewMod.get(op))
                        op.sendMessage(color + "<" + v + " 마을> §r" + player.getDisplayName() + " : " + color + message);
                }

                break;

            case 2:
                event.setCancelled(true);

                String messageC = "§d<GM> §r" + player.getDisplayName() + " : §d" + message.replaceAll("&", "§");

                Reference.LOG.info("[GM] <" + player.getName() + "> " + message);

                for (Player p : Bukkit.getOnlinePlayers())
                    if (p.isOp())
                        p.sendMessage(messageC);
                break;
        }
    }

    @Deprecated
    private void onSendMessage(Player player, String message)
    {
        int channelCode = Reference.playerChatChannel.get(player);

        switch (channelCode) {
            case 0:
                Object[] o = Reference.playerList.get(player);
                String village = (String) o[0];

                if (village.isEmpty()) {
                    village = Reference.defaultVillage;
                }
                else {
                    String vColor = Reference.villageChatColor.get(village);
                    village = vColor + village;
                }

                assert village != null;
                String type = Reference.defaultChatType
                                .replaceAll("%v%", village)
                                .replaceAll("%p%", player.getDisplayName())
                                .replaceAll("&", "§");

                String messageA = type + " §r" + message;

                Reference.LOG.info("<" + player.getName() + "> " + message);

                /* Discord SRV Connect */
                try { DiscordSRV.getPlugin().getMainTextChannel().sendMessage(player.getName() + " >> " + message).queue(); }
                catch (NullPointerException exception) { System.out.println("'DiscordSRV' 에 문제가 발견되었습니다"); }




//                for (Player p : Bukkit.getOnlinePlayers())
//                    p.sendMessage(messageA);

                break;

            case 1:
                String v = (String) Reference.playerList.get(player)[0];
                String color =  Reference.villageChatColor.get(v);

                String messageB = color + "<마을채팅> §r" + player.getDisplayName() + " : " + color + message;


                LOG("<" + player.getName() + "> " + message, v);
                Reference.LOG.info("[VillageChat] <" + player.getName() + "> " + message);

                for (Player p : Reference.playerList.keySet()) {
                    if (Reference.playerList.get(p)[0].equals(v))
                        p.sendMessage(messageB);
                }

                for (Player op : Reference.OpChatViewMod.keySet()) {
                    if (Reference.OpChatViewMod.get(op))
                        op.sendMessage(color + "<" + v + " 마을> §r" + player.getDisplayName() + " : " + color + message);
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
