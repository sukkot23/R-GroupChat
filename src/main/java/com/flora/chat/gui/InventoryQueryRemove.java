package com.flora.chat.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class InventoryQueryRemove
{
    String villageName;

    public InventoryQueryRemove(String name)
    {
        this.villageName = name;
    }

    public Inventory inventory()
    {
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "§8ㆍ " + villageName + "§8 마을을 삭제합니까? ");

        inv.setItem(11, InventoryIcon.iconRemove(false));

        inv.setItem(15, InventoryIcon.iconRemove(true));

        return inv;
    }
}
