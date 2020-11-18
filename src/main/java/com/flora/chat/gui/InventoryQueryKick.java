package com.flora.chat.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryQueryKick
{
    String villageName;
    String playerName;

    public InventoryQueryKick(String village, String name)
    {
        this.villageName = village;
        this.playerName = name;
    }

    public Inventory inventory()
    {
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "§8ㆍ " + villageName + "§8 마을에서 추방합니까? ");

        inv.setItem(4, iconTarget(playerName));

        inv.setItem(11, InventoryIcon.iconRemove(false));

        inv.setItem(15, InventoryIcon.iconRemove(true));

        return inv;
    }

    @Deprecated
    private ItemStack iconTarget(String name)
    {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        assert meta != null;
        meta.setOwner(name);
        meta.setDisplayName("§f" + name);

        item.setItemMeta(meta);
        return item;
    }
}
