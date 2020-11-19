package com.flora.chat.command;

import com.flora.chat.Reference;
import com.flora.chat.gui.InventoryVillageOperatorGui;
import com.flora.chat.gui.InventoryVillageHeaderGui;
import com.flora.chat.gui.InventoryVillagerGui;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class CommandGroupVillage implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "help":
                case "도움말":
                    onCommandHelp(sender, label);
                    break;

                case "create":
                case "생성":
                    onCommandVillageCreate(sender, args);
                    break;

                case "remove":
                case "제거":
                    onCommandVillageRemove(sender, args);
                    break;

                case "list":
                case "목록":
                    onCommandVillageList(sender, args);
                    break;

                case "info":
                case "정보":
                    onCommandVillageInfo(sender, args);
                    break;

                case "count":
                case "횟수":
                    onCommandCount(sender, args);
                    break;

                case "accept":
                case "수락":
                    onCommandAccept(sender, args);
                    break;

                case "color":
                case "색상":
                    onCommandColor(sender, args);
                    break;
                    
                case "icon":
                case "아이콘":
                    onCommandIcon(sender, args);
                    break;

                default:
                    onCommandNull(sender, label);
            }
        } else
            onCommandNull(sender, label);

        return false;
    }

    private void onCommandNull(CommandSender sender, String label) {
        if (label.equals("마을"))
            sender.sendMessage("§6/" + label + " 도움말");
        else
            sender.sendMessage("§6/" + label + " help");
    }

    private void onCommandHelp(CommandSender sender, String label) {
        String[] arg;

        if (label.equals("마을"))
            arg = new String[]{"정보", "수락", "목록", "생성", "제거", "횟수", "색상", "아이콘"};
        else
            arg = new String[]{"info", "accept", "list", "create", "remove", "count", "color", "icon"};

        sender.sendMessage("");

        sender.sendMessage("§6/" + label + " §7" + arg[0] + " [마을 이름] §f: 마을의 정보를 봅니다");
        sender.sendMessage("§6/" + label + " §7" + arg[1] + " §f: 마을의 초대를 수락합니다 (거절은 자동으로 이루어집니다)");

        if (sender.isOp()) {
            sender.sendMessage("§6/" + label + " §7" + arg[2] + " §f: 마을 목록을 확인합니다");
            sender.sendMessage("§6/" + label + " §7" + arg[3] + " [마을 이름] §f: 마을을 생성합니다");
            sender.sendMessage("§6/" + label + " §7" + arg[4] + " [마을 이름] §f: 마을을 제거합니다");
            sender.sendMessage("§6/" + label + " §7" + arg[7] + " [마을 이름] §f: 마을의 아이콘을 변경합니다");
            sender.sendMessage("§6/" + label + " §7" + arg[6] + " [마을 이름] [색상코드] §f: 마을의 색상을 변경합니다");
            sender.sendMessage("§6§m/" + label + " §7§m" + arg[5] + " [플레이어] [숫자] §f§m: 플레이어에게 마을 생성 권환 횟수를 설정합니다");
        }

        sender.sendMessage("");
    }

    private void onCommandVillageList(CommandSender sender, String[] args) {
        if (!(sender.isOp())) return;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.openInventory(new InventoryVillageOperatorGui().inventory());
        } else {
            List<String> list = new ArrayList<>();

            for (File file : Reference.getVillageFiles()) {
                FileConfiguration config = Reference.getVillageConfig(file.getName().substring(0, file.getName().length() - 4));
                list.add(config.getString("display"));
            }

            sender.sendMessage("§e- - - - - §7[ §6마을 목록 §7] §e- - - - -§r\n" + list.toString());
        }
    }

    private void onCommandVillageCreate(CommandSender sender, String[] args) {
        if (!(sender.isOp())) return;

        if (args.length < 2) {
            sender.sendMessage(Reference.FAIL + "§c 마을 이름을 입력해주세요");
            return;
        }
        String villageName = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ").replaceAll(" ", "_");

        if (Reference.getVillageFile(villageName).canRead()) {
            sender.sendMessage(Reference.FAIL + "§c 같은 이름을 가진 마을이 있습니다");
            return;
        }

        ItemStack item = new ItemStack(Material.BRICKS);
        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.getInventory().getItemInMainHand();
            if (player.getInventory().getItemInMainHand().getType() != Material.AIR)
                item = player.getInventory().getItemInMainHand();
        }

        /* - - Data Access - - -*/
        FileConfiguration config = Reference.getVillageConfig(villageName);

        config.set("name", villageName);
        config.set("display", villageName.replaceAll("_", " "));
        config.set("icon", item);
        config.set("color", "&6");

        config.set("header", "");
        config.set("member", new ArrayList<String>());

        config.set("date", LocalDate.now().toString());

        Reference.saveDataFile(config, Reference.getVillageFile(villageName));
        /* - - - - - - */

        Reference.villageChatColor.put(villageName, "§6");
        sender.sendMessage(Reference.SUCCESS + "§e '§r" + villageName + "§e' 마을을 생성했습니다");
    }

    private void onCommandVillageRemove(CommandSender sender, String[] args) {
        if (!(sender.isOp())) return;

        if (args.length < 2) {
            sender.sendMessage(Reference.FAIL + "§c 마을 이름을 입력해주세요");
            return;
        }
        String villageName = args[1];

        File file = Reference.getVillageFile(villageName);

        if (file.canRead()) {
            FileConfiguration config = Reference.getVillageConfig(villageName);
            List<?> member = config.getList("member");

            assert member != null;
            if (!(member.isEmpty())) {
                for (Object b : member) {
                    FileConfiguration configuration = Reference.getDataConfig((String) b);

                    configuration.set("village", "");
                    configuration.set("header", false);
                    Reference.saveDataFile(configuration, Reference.getDataFile((String) b));

                    OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString((String) b));

                    if (p.isOnline()) {
                        Reference.updatePlayerData((String) b);
                        Reference.updatePlayerChannel(p.getPlayer(), 0);
                    }
                }
            }

            if (file.delete()) {
                Reference.LOG.info(Reference.SUCCESS + " " + villageName + "§e 마을이 정상적으로 삭제되었습니다");
                sender.sendMessage(Reference.SUCCESS + " " + villageName + "§e 마을이 정상적으로 삭제되었습니다");
            } else {
                sender.sendMessage(Reference.FAIL + "§c 마을을 삭제 할 수 없습니다");
            }
        } else {
            sender.sendMessage(Reference.FAIL + "§c 마을을 찾을 수 없습니다");
        }
    }

    private void onCommandVillageInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

                if (Objects.requireNonNull(config.getString("village")).isEmpty()) {
                    if (player.isOp())
                        sender.sendMessage(Reference.FAIL + "§c 소속된 마을이 없거나, 마을 이름을 입력해주세요");
                    else
                        sender.sendMessage(Reference.FAIL + "§c 소속된 마을이 없습니다");
                } else {
                    player.openInventory(new InventoryVillagerGui(config.getString("village"), 0, player).inventory());
                }
            } else {
                sender.sendMessage(Reference.FAIL + "§c 마을 이름을 입력해주세요");
            }
        } else {
            String villageName = args[1];

            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.isOp()) {

                    File file = Reference.getVillageFile(villageName);

                    if (file.canRead())
                        player.openInventory(new InventoryVillageHeaderGui(villageName, 0).inventory());
                    else
                        sender.sendMessage(Reference.FAIL + "§c 마을을 찾을 수 없습니다");
                } else {
                    FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

                    if (Objects.requireNonNull(config.getString("village")).isEmpty())
                        sender.sendMessage(Reference.FAIL + "§c 소속된 마을이 없습니다");
                    else
                        player.openInventory(new InventoryVillagerGui(config.getString("village"), 0, player).inventory());
                }
            } else {
                File file = Reference.getVillageFile(villageName);

                if (file.canRead()) {
                    FileConfiguration config = Reference.getVillageConfig(villageName);

                    String header = "없음";
                    List<String> member = new ArrayList<>();

                    if (!(Objects.requireNonNull(config.getString("header")).isEmpty())) {
                        FileConfiguration c = Reference.getDataConfig(config.getString("header"));
                        header = c.getString("name");
                    }

                    if (!(Objects.requireNonNull(config.getList("member")).isEmpty())) {
                        for (Object b : Objects.requireNonNull(config.getList("member"))) {
                            FileConfiguration c = Reference.getDataConfig((String) b);
                            member.add(c.getString("name"));
                        }
                    }

                    sender.sendMessage("§7[§r " + config.getString("display") + " §7마을의 정보 ]");
                    sender.sendMessage("§e 아이콘 : §f" + Objects.requireNonNull(config.getItemStack("icon")).getType().name());
                    sender.sendMessage("§e 컬러코드 : §f" + config.getString("color"));

                    sender.sendMessage("§e 이장 : §f" + header);
                    sender.sendMessage("§e 멤버 : §f" + member.toString());

                    sender.sendMessage("§e 창설일 : §f" + config.getString("date"));
                } else {
                    sender.sendMessage(Reference.FAIL + "§c 마을을 찾을 수 없습니다");
                }
            }
        }
    }

    private void onCommandCount(CommandSender sender, String[] args) {
        if (!(sender.isOp())) return;
        if (args.length < 2) { sender.sendMessage(Reference.FAIL + "§c 플레이어를 입력해주세요"); return; }
        if (args.length < 3) { sender.sendMessage(Reference.FAIL + "§c 숫자를 입력해주세요"); return; }

        try {
            int number = Integer.parseInt(args[2]);
            if (number < 0) {
                sender.sendMessage("§c값은 음수를 가질 수 없습니다");
                return;
            }

            FileConfiguration config = null;
            String uuid = null;

            for (File file : Reference.getDataFiles()) {
                FileConfiguration c = Reference.getDataConfig(file.getName().substring(0, file.getName().length() - 4));

                if (Objects.equals(c.getString("name"), args[1])) {
                    config = c;
                    uuid = file.getName().substring(0, file.getName().length() - 4);
                }
            }

            if (config == null) {
                sender.sendMessage(Reference.FAIL + "§c 플레이어를 찾을 수 없습니다");
                return;
            }

            int n = config.getInt("count");

            config.set("count", number);
            Reference.saveDataFile(config, Reference.getDataFile(uuid));

            sender.sendMessage("§f" + config.getString("name") + "§e의 마을 생성 횟수를 수정하였습니다 §f( " + n + " → " + number + " )");

        } catch (NumberFormatException exception) {
            sender.sendMessage("§c숫자외에 다른 문자는 입력할 수 없습니다");
        }
    }

    private void onCommandAccept(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage(Reference.FAIL + "§c 콘솔에서 사용할 수 없습니다"); return; }
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        String village = (String) Reference.playerList.get(player)[0];

        if (village.isEmpty()) {
            if (Reference.playerInviteList.get(uuid) == null) {
                player.sendMessage(Reference.FAIL + "§c 마을 초대장이 없습니다");
            } else {
                String inviteVillage = Reference.playerInviteList.get(uuid);
                FileConfiguration playerConfig = Reference.getDataConfig(uuid);
                FileConfiguration villageConfig = Reference.getVillageConfig(inviteVillage);

                List<String> member = (List<String>) villageConfig.getList("member");
                assert member != null;
                member.add(uuid);

                playerConfig.set("village", inviteVillage);
                villageConfig.set("member", member);

                Reference.saveDataFile(playerConfig, Reference.getDataFile(uuid));
                Reference.saveDataFile(villageConfig, Reference.getVillageFile(inviteVillage));

                Reference.playerInviteList.remove(uuid);

                for (Player p : Reference.playerList.keySet())
                {
                    Object[] b = Reference.playerList.get(p);
                    String villageName = (String) b[0];

                    if (villageName.equals(inviteVillage))
                        p.sendMessage(Reference.WARING + " " + player.getName() + "이(가) 마을에 합류하였습니다!");
                }

                Reference.updatePlayerData(uuid);

                player.sendMessage(Reference.SUCCESS + " 축하드립니다!! " + inviteVillage + " 마을에 합류하였습니다!");
            }
        } else {
            player.sendMessage(Reference.FAIL + "§c 마을에 소속되어있어 초대장을 받을 수 없습니다");
        }
    }

    private void onCommandColor(CommandSender sender, String[] args) {
        if (!(sender.isOp())) return;
        if (args.length < 2) { sender.sendMessage(Reference.FAIL + "§c 마을 이름을 입력해주세요"); return; }
        if (args.length < 3) { sender.sendMessage(Reference.FAIL + "§c 색상코드를 입력해주세요"); return; }
        if (!(Reference.getVillageFile(args[1]).canRead())) { sender.sendMessage(Reference.FAIL + "§c 마을을 찾을 수 없습니다"); return; }

        FileConfiguration config = Reference.getVillageConfig(args[1]);

        String color = getColorToString(getStringToColor(args[2]));

        config.set("color", color.replaceAll("§", "&"));

        Reference.saveDataFile(config, Reference.getVillageFile(args[1]));

        Reference.villageChatColor.put(args[1], color);
        sender.sendMessage(Reference.SUCCESS + " " + args[1] + " 마을의 색상을 변경하였습니다 ( " + color + "색상미리보기 §7)" );
    }
    
    private void onCommandIcon(CommandSender sender, String[] args) {
        if (!(sender.isOp())) return;
        if (!(sender instanceof Player)) { sender.sendMessage(Reference.FAIL + "§c 콘솔에서 사용할 수 없습니다"); return; }
        if (args.length < 2) { sender.sendMessage(Reference.FAIL + "§c 마을 이름을 입력해주세요"); return; }
        if (!(Reference.getVillageFile(args[1]).canRead())) { sender.sendMessage(Reference.FAIL + "§c 마을을 찾을 수 없습니다"); return; }
        Player player = (Player) sender;
        ItemStack icon = new ItemStack(Material.BRICKS);

        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            icon = player.getInventory().getItemInMainHand();
            icon.setAmount(1);
        }

        FileConfiguration config = Reference.getVillageConfig(args[1]);

        config.set("icon", icon);

        Reference.saveDataFile(config, Reference.getVillageFile(args[1]));

        sender.sendMessage(Reference.SUCCESS + " " + args[1] + " 마을의 아이콘이 변경되었습니다");
    }



    private Color getStringToColor(String colorText)
    {
        String a = colorText.replaceAll("&", "");

        switch (a.length()) {
            case 1:
                if (ChatColor.getByChar(a) != null) {
                    java.awt.Color c = Objects.requireNonNull(ChatColor.getByChar(a)).asBungee().getColor();

                    return Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue());
                }

            case 6:
                try {
                    int hex1 = Integer.parseInt(a, 16);

                    return Color.fromRGB(hex1);
                } catch (NumberFormatException | NullPointerException e) { return Color.ORANGE; }

            case 7:
                try {
                    String b = a.substring(1);
                    int hex2 = Integer.parseInt(b, 16);

                    return Color.fromRGB(hex2);
                } catch (NumberFormatException | NullPointerException e) { return Color.ORANGE; }
        }
        return Color.ORANGE;
    }

    private String getColorToString(Color color)
    {
        int hex = color.asRGB();
        StringBuilder result = new StringBuilder("§x");
        String hexString = Integer.toHexString(hex);

        for (int i = 0; i < hexString.length(); i++) {
            result.append("§").append(hexString.charAt(i));
        }

        return result.toString();
    }
}

