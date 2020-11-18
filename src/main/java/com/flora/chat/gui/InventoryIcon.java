package com.flora.chat.gui;

import com.flora.chat.Reference;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InventoryIcon
{
    /* Village Icon */
    public static ItemStack iconVillage(FileConfiguration config)
    {
        ItemStack item = config.getItemStack("icon");
        assert item != null;
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        List<String> lore = new ArrayList<>();

        String headerUUID = config.getString("header");
        assert headerUUID != null;
        if (headerUUID.isEmpty())
            lore.add("§6 이장 §f: 無");
        else
            lore.add("§6 이장 §f: " + Reference.getDataConfig(headerUUID).getString("name"));

        List<?> member = config.getList("member");

        assert member != null;
        if (!(member.isEmpty()))
        {
            lore.add("§b 멤버");

            if (member.size() > 3) {
                lore.add("§f   " + Reference.getDataConfig((String) member.get(0)).getString("name") + " 외 " + (member.size() - 1));
            }
            else {
                for (Object a : Objects.requireNonNull(config.getList("member"))) {
                    String uuid = (String) a;

                    if (!(Reference.isHeader(uuid)))
                        lore.add("§f   " + Reference.getDataConfig(uuid).getString("name"));
                }
            }
        }


        lore.add("");
        lore.add("§7 창설일 : " + config.getString("date"));


        meta.setDisplayName("§r" + config.getString("name"));
        meta.setLore(lore);

        item.setItemMeta(meta);
        item.setAmount(1);

        return item;
    }

    /* Head */
    public static ItemStack iconVillager(String uuid)
    {
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        assert meta != null;
        meta.setOwningPlayer(player);

        String name = player.getName();

        if (Reference.isHeader(uuid))
            name = "* " + player.getName();


        if (player.isOnline())
            meta.setDisplayName("§a" + name);
        else
            meta.setDisplayName("§7" + name);

        item.setItemMeta(meta);
        return item;
    }

    /* BLOCK */
    public static ItemStack iconBlock()
    {
        ItemStack item = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(" ");

        item.setItemMeta(meta);

        return item;
    }

    /* Arrow */
    public static ItemStack iconArrow(boolean left)
    {
        ItemStack item = new ItemStack(Material.ITEM_FRAME);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if (left) {
            meta.setDisplayName("§f◀");
        } else
            meta.setDisplayName("§f▶");

        item.setItemMeta(meta);

        return item;
    }

    /* Page Number */
    public static ItemStack iconCenter(int page)
    {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName("§b" + (page + 1));

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack iconRemove(boolean value)
    {
        ItemStack itemStack;

        if (value) {
            itemStack = new ItemStack(Material.RED_CONCRETE);
            ItemMeta meta = itemStack.getItemMeta();

            assert meta != null;
            meta.setDisplayName("§c 거 절 ");

            itemStack.setItemMeta(meta);
        } else {
            itemStack = new ItemStack(Material.GREEN_CONCRETE);
            ItemMeta meta = itemStack.getItemMeta();

            assert meta != null;
            meta.setDisplayName("§a 수 락 ");

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }


    public static ItemStack iconLeave()
    {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName("§c 마을 떠나기 ");

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack iconExit()
    {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName("§f 뒤 로 가 기 ");

        item.setItemMeta(meta);
        return item;
    }
}
