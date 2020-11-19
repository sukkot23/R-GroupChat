package com.flora.chat;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class Reference
{
    public static final Plugin PLUGIN = JavaPlugin.getPlugin(Main.class);
    public static Logger LOG = Logger.getLogger("Minecraft");

    public static final String SUCCESS = "§7[§a ! §7]";
    public static final String WARING = "§7[§e ! §7]";
    public static final String FAIL = "§7[§c ! §7]";

    public static String defaultChatType;
    public static String defaultVillage;

    /* Player, <Village, Header> */
    public static Map<Player, Object[]> playerList = new HashMap<>();
    /* UUID, switch */
    public static Map<Player, Integer> playerChatChannel = new HashMap<>();
    /* UUID, Village */
    public static Map<String, String> playerInviteList = new HashMap<>();
    /* Chat Color */
    public static Map<String, String> villageChatColor = new HashMap<>();
    /* Op Chat View Mod */
    public static Map<Player, Boolean> OpChatViewMod = new HashMap<>();


    public static void getVillageChatColor()
    {
        if (Reference.getVillageFiles() == null) { return; }

        for (File f : Reference.getVillageFiles()) {
            String villageName = f.getName().substring(0, f.getName().length() - 4);

            FileConfiguration config = Reference.getVillageConfig(villageName);
            String color = config.getString("color");

            assert color != null;
            villageChatColor.put(villageName, color.replaceAll("&", "§"));
        }
    }

    public static void getDefaultConfig()
    {
        defaultChatType = PLUGIN.getConfig().getString("default");
        defaultVillage = PLUGIN.getConfig().getString("defaultVillage");
    }



    /* Player Data File */
    public static File getDataFile(String uuid)
    {
        return new File(PLUGIN.getDataFolder() + "\\playerdata", uuid + ".dat");
    }

    public static File[] getDataFiles()
    {
        return new File(PLUGIN.getDataFolder() + "\\playerdata").listFiles();
    }

    public static FileConfiguration getDataConfig(String uuid)
    {
        return YamlConfiguration.loadConfiguration(getDataFile(uuid));
    }




    /* Village Data File */
    public static File getVillageFile(String villageName)
    {
        return new File(PLUGIN.getDataFolder() + "\\village", villageName + ".dat");
    }

    public static File[] getVillageFiles()
    {
        return new File(PLUGIN.getDataFolder() + "\\village").listFiles();
    }

    public static FileConfiguration getVillageConfig(String name)
    {
        return YamlConfiguration.loadConfiguration(getVillageFile(name));
    }


    /* Save Data File */
    public static void saveDataFile(FileConfiguration config, File file)
    {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("§cFile I/O Error!!");
        }
    }

    /* Get Player Data File to PlayerName */
    public static File getDataFileToName(String playerName) throws NullPointerException
    {
        for (File file : getDataFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (Objects.requireNonNull(config.getString("name")).equalsIgnoreCase(playerName))
                return file;
        }
        return null;
    }

    /* Get Player Data File to PlayerName */
    public static FileConfiguration getDataConfigToName(String playerName) throws NullPointerException
    {
        return YamlConfiguration.loadConfiguration(Objects.requireNonNull(getDataFileToName(playerName)));
    }



    /* Hash Map - Player */
    public static void updatePlayerData(String uuid)
    {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        FileConfiguration config = Reference.getDataConfig(uuid);

        Object[] value = { config.getString("village"), config.getBoolean("header") };

        playerList.put(player, value);
    }

    public static void updatePlayerData(Player player)
    {
        FileConfiguration config = Reference.getDataConfig(player.getUniqueId().toString());

        Object[] value = { config.getString("village"), config.getBoolean("header") };

        playerList.put(player, value);
    }

    public static void removePlayerData(String uuid)
    {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));

        playerList.remove(player);
    }

    public static void removePlayerData(Player player)
    {
        playerList.remove(player);
    }


    /* Has Map - Chat */
    public static void updatePlayerChannel(Player player, int channel)
    {
        playerChatChannel.put(player, channel);
    }

    public static void removePlayerChannel(Player player)
    {
        playerChatChannel.remove(player);
    }

//    public static void updateAllPlayerData()
//    {
//        for (Player p : Bukkit.getOnlinePlayers())
//        {
//            updatePlayerData(p.getUniqueId().toString());
//            updatePlayerChannel(p.getUniqueId().toString(), 0);
//        }
//    }

    public static boolean isHeader(String uuid)
    {
        FileConfiguration config = Reference.getDataConfig(uuid);

        return config.getBoolean("header");
    }
}
