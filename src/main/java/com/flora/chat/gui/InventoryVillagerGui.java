package com.flora.chat.gui;

import com.flora.chat.Reference;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryVillagerGui
{
    String name, display, color, header, date;
    ItemStack icon;
    List<?> member;
    int page;
    Player player;

    public InventoryVillagerGui(String villageName, int page, Player player)
    {
        FileConfiguration config = Reference.getVillageConfig(villageName);

        this.name = config.getString("name");
        this.display = config.getString("display");
        this.color = config.getString("color");
        this.header = config.getString("header");
        this.date = config.getString("date");
        this.icon = config.getItemStack("icon");
        this.member = config.getList("member");

        this.page = page;
        this.player = player;
    }

    public Inventory inventory()
    {
        Inventory inv = Bukkit.getServer().createInventory(null, 54, "§8ㆍ " + name + "§8 마을 정보 ");

        inv.setItem(4, mainIcon());

        int max = 27;
        int lengthFile = member.size();
        int maxPage = lengthFile / max;

        if (lengthFile % max == 0)
            maxPage--;

        for (int i = 0; i < max; i++) {
            int number = i + (page * max);

            if (lengthFile > number) {
                inv.setItem(i + 9, InventoryIcon.iconVillager((String) member.get(number)));
            } else {
                inv.setItem(i + 9, new ItemStack(Material.AIR));
            }
        }

        inv.setItem(49, InventoryIcon.iconCenter(page));

        for (int j = 36; j < 45; j++)
            inv.setItem(j, InventoryIcon.iconBlock());

        if (page > 0)
            inv.setItem(48, InventoryIcon.iconArrow(true));

        if (maxPage > page)
            inv.setItem(50, InventoryIcon.iconArrow(false));

        inv.setItem(53, InventoryIcon.iconLeave());

        if (Reference.isHeader(player.getUniqueId().toString()))
            inv.setItem(52, headerIcon());

        return inv;
    }

    private ItemStack headerIcon()
    {
        ItemStack item = new ItemStack(Material.CHAIN_COMMAND_BLOCK);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName("§x§7§6§B§2§9§7 마을 관리모드 ");

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack mainIcon()
    {
        ItemStack mainIcon = icon;
        ItemMeta meta = mainIcon.getItemMeta();

        assert meta != null;
        meta.setDisplayName("§r" + display);

        List<String> lore = new ArrayList<>();

        if (header.isEmpty())
            lore.add("§6 이장 §f: 無");
        else
            lore.add("§6 이장 §f: " + Reference.getDataConfig(header).getString("name"));

        lore.add("");
        lore.add("§7 창설일 : " + date);

        meta.setLore(lore);

        mainIcon.setItemMeta(meta);
        return mainIcon;
    }
}
