package com.flora.chat.gui;

import com.flora.chat.Main;
import com.flora.chat.Reference;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InventoryVillageHeaderGui
{
    String name, display, color, header, date;
    ItemStack icon;
    List<?> member;
    int page;

    public InventoryVillageHeaderGui(String villageName, int page)
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
    }

    public Inventory inventory()
    {
        Inventory inv = Bukkit.getServer().createInventory(null, 54, "§8ㆍ " + name + "§8 마을 관리 ");

        inv.setItem(4, mainIcon());

        int max = 27;
        int lengthFile = member.size();
        int maxPage = lengthFile / max;

        if (lengthFile % max == 0)
            maxPage--;

        new BukkitRunnable() {
            @Override
            public void run()
            {
                for (int i = 0; i < max; i++) {
                    int number = i + (page * max);

                    if (lengthFile > number) {
                        inv.setItem(i + 9, InventoryIcon.iconVillager((String) member.get(number)));
                    } else {
                        inv.setItem(i + 9, new ItemStack(Material.AIR));
                    }
                }
            }
        }.runTaskAsynchronously(JavaPlugin.getPlugin(Main.class));

        inv.setItem(45, iconSign());
        inv.setItem(46, iconInvite());

        inv.setItem(49, InventoryIcon.iconCenter(page));

        for (int j = 36; j < 45; j++)
            inv.setItem(j, InventoryIcon.iconBlock());

        if (page > 0)
            inv.setItem(48, InventoryIcon.iconArrow(true));

        if (maxPage > page)
            inv.setItem(50, InventoryIcon.iconArrow(false));

        inv.setItem(53, InventoryIcon.iconExit());

        return inv;
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

    private ItemStack iconSign()
    {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        assert meta != null;
        meta.setDisplayName("§6도 움 말");

        lore.add("");
        lore.add("§7마을아이콘");
        lore.add("§7 좌클릭 :§f 설정");
        lore.add("");
        lore.add("§7플레이어");
        lore.add("§7 쉬프트 + 우클릭 :§f 강퇴");

        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack iconInvite()
    {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName("§b 초대하기 ");

        item.setItemMeta(meta);
        return item;
    }
}
