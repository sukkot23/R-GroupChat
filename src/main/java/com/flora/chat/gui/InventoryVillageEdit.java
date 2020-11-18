package com.flora.chat.gui;

import com.flora.chat.Reference;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryVillageEdit
{
    String name, display, color;

    public InventoryVillageEdit(String villageName)
    {
        FileConfiguration config = Reference.getVillageConfig(villageName);

        this.name = config.getString("name");
        this.display = config.getString("display");
        this.color = config.getString("color");
    }

    public Inventory inventory()
    {
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "§8ㆍ " + name + "§8 마을 수정 ");

        for (int i = 0; i < 3; i++)
        {
            int number = 7 + (i * 9);
            inv.setItem(number, InventoryIcon.iconBlock());
        }

        inv.setItem(12, iconColor());

        inv.setItem(26, InventoryIcon.iconExit());

        return inv;
    }

    private ItemStack iconColor()
    {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        List<String> lore = new ArrayList<>();

        assert meta != null;
        meta.setColor(getStringToColor(color));
        meta.setDisplayName("§6  마을 대표 색상  ");
        
        lore.add("");
        lore.add("§fColor Text : " + color);
        lore.add(getColorToString(getStringToColor(color)) + "색상 미리보기");
        lore.add("");
        lore.add("§7* 색상 변경은 운영진에게 문의바랍니다");
        
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DYE);

        item.setItemMeta(meta);
        return item;
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
                } catch (NumberFormatException | NullPointerException e) { return Color.BLACK; }

            case 7:
                try {
                    String b = a.substring(1);
                    int hex2 = Integer.parseInt(b, 16);

                    return Color.fromRGB(hex2);
                } catch (NumberFormatException | NullPointerException e) { return Color.BLACK; }
        }
        return Color.BLACK;
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
