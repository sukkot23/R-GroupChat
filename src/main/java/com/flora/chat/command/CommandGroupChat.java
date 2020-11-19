package com.flora.chat.command;

import com.flora.chat.Reference;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandGroupChat implements CommandExecutor
{
    String channel_0 = "§e 채팅모드 §f: 전체";
    String channel_1 = "§e 채팅모드 §f: §b마을";
    String channel_2 = "§e 채팅모드 §f: §dGM";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!(sender instanceof Player)) { sender.sendMessage(Reference.FAIL + "§c 콘솔에서 사용할 수 없습니다"); return false; }
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();

        if (args.length == 1) {
            if (player.isOp())
                if (args[0].equals("v") || args[0].equals("view"))
                {
                    FileConfiguration config = Reference.getDataConfig(uuid);

                    if (Reference.OpChatViewMod.get(player)) {
                        Reference.OpChatViewMod.put(player, false);
                        config.set("viewMod", false);
                        player.sendMessage(Reference.SUCCESS + " 마을 채팅 보기모드가§c 비활성화 §7되었습니다");
                    } else {
                        Reference.OpChatViewMod.put(player, true);
                        config.set("viewMod", true);
                        player.sendMessage(Reference.SUCCESS + " 마을 채팅 보기모드가§a 활성화 §7되었습니다");
                    }

                    Reference.saveDataFile(config, Reference.getDataFile(uuid));

                    return false;
                }
        }

        int channelCode = Reference.playerChatChannel.get(player);

        switch (channelCode) {
            case 0:
                Object[] ob = Reference.playerList.get(player);
                
                if (((String) ob[0]).isEmpty()) {
                    if (player.isOp()) {
                        Reference.updatePlayerChannel(player, 2);
                        player.sendMessage(channel_2);
                        break;
                    }

                    player.sendMessage(Reference.FAIL + " 소속된 마을이 없어 채팅모드를 변경할 수 없습니다");
                    break;
                }
                
                Reference.updatePlayerChannel(player, 1);
                player.sendMessage(channel_1);
                break;
                
            case 1:
                if (player.isOp()) {
                    Reference.updatePlayerChannel(player, 2);
                    player.sendMessage(channel_2);
                    break;
                }
                
                Reference.updatePlayerChannel(player, 0);
                player.sendMessage(channel_0);
                break;

            case 2:
                if (player.isOp()) {
                    Reference.updatePlayerChannel(player, 0);
                    player.sendMessage(channel_0);
                }
                break;
        }
        
        return false;
    }
}
