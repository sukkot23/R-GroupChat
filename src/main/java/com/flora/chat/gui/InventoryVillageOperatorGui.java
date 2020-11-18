package com.flora.chat.gui;

import com.flora.chat.Reference;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;

public class InventoryVillageOperatorGui
{
    int page = 0;

    public InventoryVillageOperatorGui()
    {
        super();
    }

    public InventoryVillageOperatorGui(int page)
    {
        this.page = page;
    }

    public Inventory inventory()
    {
        Inventory inv = Bukkit.getServer().createInventory(null, 54, "§8ㆍ [ §6마을 목록 §8] ㆍ");

        File[] villageList = Reference.getVillageFiles();
        Arrays.sort(villageList);

        int max = 36;
        int lengthFile = villageList.length;
        int maxPage = lengthFile / max;

        if (lengthFile % max == 0)
            maxPage--;

        for (int i = 0; i < max; i++)
        {
            int number = i + (page * max);

            if (lengthFile > number) {
                FileConfiguration config = Reference.getVillageConfig(villageList[number].getName().substring(0, villageList[number].getName().length() - 4));

                inv.setItem(i, InventoryIcon.iconVillage(config));
            } else {
                inv.setItem(i, new ItemStack(Material.AIR));
            }
        }

        inv.setItem(49, InventoryIcon.iconCenter(page));

        for (int j = 36; j < 45; j++)
            inv.setItem(j, InventoryIcon.iconBlock());

        if (page > 0)
            inv.setItem(48, InventoryIcon.iconArrow(true));

        if (maxPage > page)
            inv.setItem(50, InventoryIcon.iconArrow(false));

        return inv;
    }
}
