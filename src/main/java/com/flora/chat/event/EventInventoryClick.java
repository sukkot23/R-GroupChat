package com.flora.chat.event;

import com.flora.chat.Reference;
import com.flora.chat.gui.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventInventoryClick implements Listener
{
    /* Village List Inventory Click Event */
    @EventHandler
    private void onInventoryEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();

        /* Village List */
        if (isListInventory(event)) {
            event.setCancelled(true);
            onClickVillageListGUI(event, player);
        }

        /* Villager GUI */
        if (isVillagerInventory(event)) {
            event.setCancelled(true);
            onClickVillagerGUI(event, player);
        }

        /* VillagerHeader GUI */
        if (isVillagerHeaderInventory(event)) {
            event.setCancelled(true);
            onClickVillagerHeaderGUI(event, player);
        }

        /* Village Remove */
        if (isRemoveInventory(event)) {
            event.setCancelled(true);
            onClickRemoveGUI(event, player);
        }

        /* Village Leave */
        if (isLeaveInventory((event))) {
            event.setCancelled(true);
            onClickLeaveGUI(event, player);
        }

        /* Village Invite */
        if (isInviteInventory(event)) {
            event.setCancelled(true);
            onClickInviteGUI(event, player);
        }

        /* Villager Kick */
        if (isKickInventory(event)) {
            event.setCancelled(true);
            onClickKickGUI(event, player);
        }

        /* Village Edit */
        if (isEditInventory(event)) {
            event.setCancelled(true);
            onClickEditGUI(event, player);
        }
    }

    /* List Event */
    private void onClickVillageListGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case ITEM_FRAME:
                onChangePage(event, player);
                break;

            case PURPLE_STAINED_GLASS_PANE:
                onSkipClickBlock(event, player);
                break;

            case NETHER_STAR:
                onSkipClickStar(event, player);
                break;

            default:
                if (event.getClick().isShiftClick())
                    onOpenRemoveVillageGUI(event, player);
                else
                    onOpenVillageGUI(event, player);
        }
    }

    private void onChangePage(InventoryClickEvent event, Player player)
    {
        int pageNumber = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2)) - 1;
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();

        if (display.equals("§f◀"))
            player.openInventory(new InventoryVillageOperatorGui(pageNumber - 1).inventory());
        else if (display.equals("§f▶"))
            player.openInventory(new InventoryVillageOperatorGui(pageNumber + 1).inventory());
        else
            onOpenVillageGUI(event, player);
    }

    private void onSkipClickBlock(InventoryClickEvent event, Player player)
    {
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();

        if (!(display.equals(" "))) {
            onOpenVillageGUI(event, player);
        }
    }

    private void onSkipClickStar(InventoryClickEvent event, Player player)
    {
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();

        Pattern pa = Pattern.compile("§b(\\d*?)");
        Matcher ma = pa.matcher(display);

        if (!(ma.find()))
            onOpenVillageGUI(event, player);
    }

    private void onOpenVillageGUI(InventoryClickEvent event, Player player)
    {
        String villageName = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();

        player.openInventory(new InventoryVillageHeaderGui(villageName, 0).inventory());
    }

    private void onOpenRemoveVillageGUI(InventoryClickEvent event, Player player)
    {
        String villageName = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();

        player.openInventory(new InventoryQueryRemove(villageName).inventory());
    }

    private boolean isListInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;
        return event.getView().getTitle().equals("§8ㆍ [ §6마을 목록 §8] ㆍ");
    }



    /* Village Header Event */
    private void onClickVillagerHeaderGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }
        String villageName = event.getView().getTitle().substring(4, event.getView().getTitle().length() - 9);

        if (event.getSlot() == 4) {
            player.openInventory(new InventoryVillageEdit(villageName).inventory());
            return;
        }

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case PLAYER_HEAD:
                onVillagerEvent(event, player, villageName);
                break;

            case ITEM_FRAME:
                onChangePageVillagerHeader(event, player);
                break;

            case BARRIER:
                FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

                if (Objects.equals(config.getString("village"), villageName))
                    player.openInventory(new InventoryVillagerGui(villageName, 0, player).inventory());
                else
                    player.closeInventory();
                break;

            case WRITABLE_BOOK:
                player.openInventory(new InventoryQueryInvite(villageName, 0).inventory());
                break;
        }
    }

    private void onVillagerEvent(InventoryClickEvent event, Player player, String villageName)
    {
        switch (event.getClick()) {
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                onKickVillager(event, player, villageName);
                break;

            case MIDDLE:
                onInstateVillager(event, player, villageName);
                break;
        }
    }

    private void onKickVillager(InventoryClickEvent event, Player player, String villageName)
    {
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
        
        if (display.contains("*")) {
            if (player.isOp()) {
                String name = display.substring(4);

                player.openInventory(new InventoryQueryKick(villageName, name).inventory());
            }
        } else {
            String name = display.substring(2);

            player.openInventory(new InventoryQueryKick(villageName, name).inventory());
        }
    }

    private void onInstateVillager(InventoryClickEvent event, Player player, String villageName)
    {
        int pageNumber = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2));
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();

        String playerName;
        FileConfiguration villageConfig = Reference.getVillageConfig(villageName);

        if (display.contains("*")) {
            playerName = display.substring(4);

            FileConfiguration playerConfig = Reference.getDataConfigToName(playerName);

            String uuid = playerConfig.getString("uuid");
            assert uuid != null;

            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

            villageConfig.set("header", "");
            playerConfig.set("header", false);

            Reference.saveDataFile(villageConfig, Reference.getVillageFile(villageName));
            Reference.saveDataFile(playerConfig, Reference.getDataFile(uuid));

            if (p.isOnline()) {
                Reference.updatePlayerData(uuid);
                Objects.requireNonNull(p.getPlayer()).sendMessage(Reference.WARING + " " + villageName + " 마을의 이장에서 사임되셨습니다");
            }

            player.sendMessage(Reference.SUCCESS + " " + playerName + "을(를) " + villageName + " 마을의 이장에서 사임시켰습니다");

        } else {
            playerName = display.substring(2);

            FileConfiguration playerConfig = Reference.getDataConfigToName(playerName);

            String uuid = playerConfig.getString("uuid");
            assert uuid != null;

            OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

            if (!(Objects.requireNonNull(villageConfig.getString("header")).isEmpty()))
            {
                String headerUUID = villageConfig.getString("header");

                FileConfiguration c = Reference.getDataConfig(headerUUID);
                c.set("header", false);

                Reference.saveDataFile(c, Reference.getDataFile(headerUUID));
            }

            villageConfig.set("header", uuid);
            playerConfig.set("header", true);

            Reference.saveDataFile(villageConfig, Reference.getVillageFile(villageName));
            Reference.saveDataFile(playerConfig, Reference.getDataFile(uuid));

            if (p.isOnline()) {
                Reference.updatePlayerData(uuid);
                Objects.requireNonNull(p.getPlayer()).sendMessage(Reference.WARING + " " + villageName + " 마을의 이장으로 선정되셨습니다");
            }

            player.sendMessage(Reference.SUCCESS + " " + playerName + "을(를) " + villageName + " 마을의 이장으로 선정하였습니다");

        }
        player.openInventory(new InventoryVillageHeaderGui(villageName, pageNumber - 1).inventory());
    }

    private void onChangePageVillagerHeader(InventoryClickEvent event, Player player)
    {
        int pageNumber = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2)) - 1;
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
        FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

        if (display.equals("§f◀"))
            player.openInventory(new InventoryVillageHeaderGui(config.getString("village"), pageNumber - 1).inventory());
        else if (display.equals("§f▶"))
            player.openInventory(new InventoryVillageHeaderGui(config.getString("village"), pageNumber + 1).inventory());
    }

    private boolean isVillagerHeaderInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§8 마을 관리 ");
    }



    /* Villager Event */
    private void onClickVillagerGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case ITEM_FRAME:
                onChangePageVillager(event, player);
                break;

            case BARRIER:
                onLeaveVillage(event, player);
                break;

            case CHAIN_COMMAND_BLOCK:
                onOpenHeaderGUI(event, player);
                break;
        }
    }

    private void onChangePageVillager(InventoryClickEvent event, Player player)
    {
        int pageNumber = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2)) - 1;
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
        FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

        if (display.equals("§f◀"))
            player.openInventory(new InventoryVillagerGui(config.getString("village"), pageNumber - 1, player).inventory());
        else if (display.equals("§f▶"))
            player.openInventory(new InventoryVillagerGui(config.getString("village"), pageNumber + 1, player).inventory());
    }

    private void onLeaveVillage(InventoryClickEvent event, Player player)
    {
        String villageName = event.getView().getTitle().substring(4, event.getView().getTitle().length() - 9);

        player.openInventory(new InventoryQueryLeave(villageName).inventory());
    }

    private void onOpenHeaderGUI(InventoryClickEvent event, Player player)
    {
        String villageName = event.getView().getTitle().substring(4, event.getView().getTitle().length() - 9);

        player.openInventory(new InventoryVillageHeaderGui(villageName, 0).inventory());
    }

    private boolean isVillagerInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§8 마을 정보 ");
    }





    /* Remove Event */
    private void onClickRemoveGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case RED_CONCRETE:
                player.openInventory(new InventoryVillageOperatorGui().inventory());
                break;

            case GREEN_CONCRETE:
                onClickRemove(event, player);
                break;
        }
    }

    private void onClickRemove(InventoryClickEvent event, Player player)
    {
        String title = event.getView().getTitle();
        String villageName = title.substring(4, title.length() - 14);

        File file = Reference.getVillageFile(villageName);
        if (file.canRead()) {
            FileConfiguration config = Reference.getVillageConfig(villageName);
            List<?> member = config.getList("member");

            assert member != null;
            if (!(member.isEmpty()))
            {
                for (Object b : member)
                {
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
                player.sendMessage(Reference.SUCCESS  + " " + villageName + "§e 마을이 정상적으로 삭제되었습니다");
            } else {
                player.sendMessage(Reference.FAIL + "§c 마을을 삭제 할 수 없습니다");
            }
        } else {
            player.sendMessage(Reference.FAIL + "§c 마을을 찾을 수 없습니다");
        }

        player.closeInventory();
    }

    private boolean isRemoveInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§8 마을을 삭제합니까? ");
    }



    /* Leave Event */
    private void onClickLeaveGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        String villageName = event.getView().getTitle().substring(4, event.getView().getTitle().length() - 16);

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case RED_CONCRETE:
                player.openInventory(new InventoryVillagerGui(villageName, 0, player).inventory());
                break;

            case GREEN_CONCRETE:
                onClickLeave(event, player, villageName);
                break;
        }
    }

    private void onClickLeave(InventoryClickEvent event, Player player, String villageName)
    {
        FileConfiguration villageConfig = Reference.getVillageConfig(villageName);
        FileConfiguration playerConfig = Reference.getDataConfig(player.getUniqueId().toString());

        List<?> member = villageConfig.getList("member");
        assert member != null;

        member.remove(player.getUniqueId().toString());

        villageConfig.set("member", member);
        playerConfig.set("village", "");

        if (Reference.isHeader(player.getUniqueId().toString())) {
            villageConfig.set("header", "");
            playerConfig.set("header", "");
        }

        Reference.saveDataFile(villageConfig, Reference.getVillageFile(villageName));
        Reference.saveDataFile(playerConfig, Reference.getDataFile(player.getUniqueId().toString()));

        Reference.updatePlayerData(player.getUniqueId().toString());
        Reference.playerChatChannel.put(player, 0);
        player.sendMessage(Reference.SUCCESS + " " + villageName + " 마을을 떠났습니다");

        for (Player p : Reference.playerList.keySet()) {
            Object[] b = Reference.playerList.get(p);

            if (b[0].equals(villageName))
                p.sendMessage(Reference.SUCCESS + " " + player.getName() + "이(가) " + villageName + " 마을을 떠났습니다");
        }

        player.closeInventory();
    }

    private boolean isLeaveInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§8 마을을 나가시겠습니까? ");
    }



    /* Invite Event */
    private void onClickInviteGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case PLAYER_HEAD:
                onInviteEvent(event, player);
                break;

            case ITEM_FRAME:
                onChangePageInvite(event, player);
                break;
        }
    }

    private void onInviteEvent(InventoryClickEvent event, Player player)
    {
        String villageName = event.getView().getTitle().substring(4, event.getView().getTitle().length() - 17);
        String playerName = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName().substring(2);
        Player p = Bukkit.getPlayer(playerName);
        assert p != null;
        String uuid = p.getUniqueId().toString();

        if (!(Objects.requireNonNull(Reference.getDataConfig(uuid).getString("village")).isEmpty())) { player.sendMessage(Reference.FAIL + "§c 이미 소속된 마을이 있습니다"); return; }

        if (Reference.playerInviteList.get(uuid) == null) {
            player.sendMessage(Reference.SUCCESS + " " + playerName + "에게 초대장을 발송했습니다");
            p.sendMessage(Reference.WARING + " " + villageName + " 마을에서 초대장이 도착했습니다! ('/마을 수락' 명령어로 초대를 승락할 수 있습니다)");
            Reference.playerInviteList.put(uuid, villageName);

            new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    count++;

                    if (Reference.playerInviteList.get(uuid) == null) {
                        player.sendMessage(Reference.SUCCESS + " 플레이어가 초대를 수락하였습니다");
                        cancel();
                    }

                    if (count > 30) {
                        player.sendMessage(Reference.FAIL + "§c 플레이어가 초대를 거절하였습니다");
                        p.sendMessage(Reference.FAIL + "§c 초대장 기간이 만료되어 초대가 거절되었습니다");
                        Reference.playerInviteList.remove(uuid);
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(Reference.PLUGIN, 30, 30);
        } else {
            player.sendMessage(Reference.FAIL + "§c 이미 초대장을 발송하였습니다");
        }
    }

    private void onChangePageInvite(InventoryClickEvent event, Player player)
    {
        int pageNumber = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(49)).getItemMeta()).getDisplayName().substring(2)) - 1;
        String display = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
        FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

        if (display.equals("§f◀"))
            player.openInventory(new InventoryQueryInvite(config.getString("village"), pageNumber - 1).inventory());
        else if (display.equals("§f▶"))
            player.openInventory(new InventoryQueryInvite(config.getString("village"), pageNumber + 1).inventory());
    }

    private boolean isInviteInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§8 마을에 누굴 초대할까요? ");
    }



    /* Kick Event */
    private void onClickKickGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        String villageName = event.getView().getTitle().substring(4, event.getView().getTitle().length() - 15);
        String playerName = Objects.requireNonNull(Objects.requireNonNull(event.getView().getTopInventory().getItem(4)).getItemMeta()).getDisplayName().substring(2);

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case RED_CONCRETE:
                player.openInventory(new InventoryVillageHeaderGui(villageName, 0).inventory());
                break;

            case GREEN_CONCRETE:
                onClickKick(event, player, villageName, playerName);
                break;
        }
    }

    private void onClickKick(InventoryClickEvent event, Player player, String villageName, String playerName)
    {
        FileConfiguration villageConfig = Reference.getVillageConfig(villageName);
        FileConfiguration playerConfig = Reference.getDataConfigToName(playerName);

        String uuid = playerConfig.getString("uuid");
        assert uuid != null;
        OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

        List<?> member = villageConfig.getList("member");
        assert member != null;

        member.remove(uuid);

        villageConfig.set("member", member);
        playerConfig.set("village", "");

        if (Reference.isHeader(uuid)) {
            villageConfig.set("header", "");
            playerConfig.set("header", "");
        }

        Reference.saveDataFile(villageConfig, Reference.getVillageFile(villageName));
        Reference.saveDataFile(playerConfig, Reference.getDataFile(uuid));

        for (Player ply : Reference.playerList.keySet()) {
            Object[] b = Reference.playerList.get(ply);

            if (b[0].equals(villageName))
                ply.sendMessage(Reference.WARING + " " + playerName + "이(가) 마을에서 추방되었습니다");
        }

        if (p.isOnline()) {
            Reference.updatePlayerData(uuid);
            Reference.playerChatChannel.put(p.getPlayer(), 0);
        }

        player.sendMessage(Reference.SUCCESS + " " + playerName + "을(를) " + villageName + " 마을에서 추방했습니다");
        player.closeInventory();
    }

    private boolean isKickInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§8 마을에서 추방합니까? ");
    }



    /* Village Edit Event */
    private void onClickEditGUI(InventoryClickEvent event, Player player)
    {
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        if (Objects.requireNonNull(event.getCurrentItem()).getType() == Material.BARRIER) {
            String villageName = event.getView().getTitle().substring(4, event.getView().getTitle().length() - 9);

            player.openInventory(new InventoryVillageHeaderGui(villageName, 0).inventory());
        }
    }

    private boolean isEditInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return event.getView().getTitle().contains("§8 마을 수정 ");
    }
}
