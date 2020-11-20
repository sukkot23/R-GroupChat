package com.flora.chat.gui;

import com.flora.chat.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

        new BukkitRunnable() {
            @Override
            public void run()
            { inv.setItem(4, iconTarget(playerName)); }
        }.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));


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
