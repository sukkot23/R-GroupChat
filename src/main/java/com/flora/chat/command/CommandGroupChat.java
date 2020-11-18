package com.flora.chat.command;

import com.flora.chat.Reference;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

        int channelCode = Reference.playerChatChannel.get(uuid);

        switch (channelCode) {
            case 0:
                Object[] ob = Reference.playerList.get(player);
                
                if (((String) ob[0]).isEmpty()) {
                    if (player.isOp()) {
                        Reference.updatePlayerChannel(uuid, 2);
                        player.sendMessage(channel_2);
                        break;
                    }

                    player.sendMessage(Reference.FAIL + " 소속된 마을이 없어 채팅모드를 변경할 수 없습니다");
                    break;
                }
                
                Reference.updatePlayerChannel(uuid, 1);
                player.sendMessage(channel_1);
                break;
                
            case 1:
                if (player.isOp()) {
                    Reference.updatePlayerChannel(uuid, 2);
                    player.sendMessage(channel_2);
                    break;
                }
                
                Reference.updatePlayerChannel(uuid, 0);
                player.sendMessage(channel_0);
                break;

            case 2:
                Reference.updatePlayerChannel(uuid, 0);
                player.sendMessage(channel_0);
                break;
        }
        
        return false;
    }
}
