package com.flora.chat.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryQueryInvite
{
    int page;
    String villageName;
    List<Player> onlinePlayer = new ArrayList<>();

    public InventoryQueryInvite(String name, int page)
    {
        this.villageName = name;
        this.page = page;

        onlinePlayer.addAll(Bukkit.getOnlinePlayers());
    }

    public Inventory inventory()
    {
        Inventory inv = Bukkit.getServer().createInventory(null, 54, "§8ㆍ " + villageName + "§8 마을에 누굴 초대할까요? ");

        int max = 36;
        int lengthFile = onlinePlayer.size();
        int maxPage = lengthFile / max;

        if (lengthFile % max == 0)
            maxPage--;

        for (int i = 0; i < max; i++)
        {
            int number = i + (page * max);

            if (lengthFile > number)
                inv.setItem(i, iconPlayerHead(onlinePlayer.get(i)));
            else
                inv.setItem(i, new ItemStack(Material.AIR));
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

    private ItemStack iconPlayerHead(Player player)
    {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        assert meta != null;
        meta.setOwningPlayer(player);
        meta.setDisplayName("§f" + player.getName());

        item.setItemMeta(meta);
        return item;
    }
}
