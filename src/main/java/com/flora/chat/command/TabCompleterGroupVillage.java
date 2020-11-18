package com.flora.chat.command;

import com.flora.chat.Reference;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class TabCompleterGroupVillage implements TabCompleter
{
    List<String> empty = new ArrayList<String>() {{ add(""); }};
    String[] en_commandsA = { "help", "info", "accept", "create", "remove", "list", "count", "color", "icon"};
    String[] ko_commandsA = { "도움말", "정보", "수락", "생성", "제거", "목록", "횟수", "색상", "아이콘"};

    String[] en_commandsB = { "help", "info"};
    String[] ko_commandsB = { "도움말", "정보"};

    String[] en_commandsC = { "help", "info", "accept"};
    String[] ko_commandsC = { "도움말", "정보", "수락"};

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args)
    {
        if (args.length > 0) {
            switch (args[0]) {
                case "help":
                case "도움말":

                case "accept":
                case "수락":

                case "list":
                case "목록":
                    return empty;

                case "info":
                case "정보":
                    return tabCommandInfo(sender, args);

                case "create":
                case "생성":
                    return tabCommandCreate(sender, alias, args);

                case "remove":
                case "제거":
                    return tabCommandRemove(sender, args);

                case "count":
                case "횟수":
                    return tabCommandCount(sender, alias, args);

                case "color":
                case "색상":
                    return tabCommandColor(sender, alias, args);

                case "icon":
                case "아이콘":
                    return tabCommandIcon(sender, args);

                default:
                    return tabCommandMain(sender, alias, args[0]);
            }
        } else {
            return empty;
        }
    }

    private List<String> tabCommandMain(CommandSender sender, String alias, String args)
    {
        if (alias.equals("마을")) {
            List<String> list_ko = new ArrayList<>(Arrays.asList(ko_commandsA));

            if (!(sender.isOp())) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (Reference.isHeader(player.getUniqueId().toString())) {
                        List<String> listB = new ArrayList<>(Arrays.asList(ko_commandsB));

                        return tabCompleteSort(listB, args);
                    } else {
                        List<String> listC = new ArrayList<>(Arrays.asList(ko_commandsC));

                        return tabCompleteSort(listC, args);
                    }
                }
            }

            return tabCompleteSort(list_ko, args);
        }
        else {
            List<String> list_en = new ArrayList<>(Arrays.asList(en_commandsA));

            if (!(sender.isOp())) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    if (Reference.isHeader(player.getUniqueId().toString())) {
                        List<String> listB = new ArrayList<>(Arrays.asList(en_commandsB));

                        return tabCompleteSort(listB, args);
                    } else {
                        List<String> listC = new ArrayList<>(Arrays.asList(en_commandsC));

                        return tabCompleteSort(listC, args);
                    }
                }
            }

            return tabCompleteSort(list_en, args);
        }
    }

    private List<String> tabCompleteSort(List<String> list, String args)
    {
        List<String> sortList = new ArrayList<>();
        for (String s : list)
        {
            if (args.isEmpty()) return list;

            if (s.toLowerCase().startsWith(args.toLowerCase()))
                sortList.add(s);
        }
        return sortList;
    }



    private List<String> tabCommandCreate(CommandSender sender, String alias, String[] args)
    {
        if (sender.isOp()) {
            if (alias.equals("생성")) {
                if (args.length > 1)
                    return new ArrayList<String>() {{ add("(마을이름)"); }};
            } else {
                if (args.length > 1)
                    return new ArrayList<String>() {{ add("(VillageName)"); }};
            }
        }
        return empty;
    }

    private List<String> tabCommandRemove(CommandSender sender, String[] args)
    {
        if (sender.isOp()) {
            if (args.length == 2)
                return tabCompleteSort(tabCommandVillageList(), args[1]);
            else
                return empty;
        } else {
            return empty;
        }
    }

    private List<String> tabCommandInfo(CommandSender sender, String[] args)
    {
        if (sender.isOp()) {
            if (args.length == 2)
                return tabCompleteSort(tabCommandVillageList(), args[1]);
            else
                return empty;
        } else {
            return empty;
        }
    }

    private List<String> tabCommandCount(CommandSender sender, String alias, String[] args)
    {
        if (sender.isOp()) {
            if (args.length == 2)
                return tabCompleteSort(tabOfflinePlayers(), args[1]);
            else if (args.length == 3) {
                if (args[1].isEmpty())
                    return empty;
                else
                {
                    if (alias.equals("횟수")) {
                        return new ArrayList<String>() {{ add("(숫자)"); }};
                    } else {
                        return new ArrayList<String>() {{ add("(Number)"); }};
                    }
                }
            } else
                return empty;
        } else {
            return empty;
        }
    }

    private List<String> tabCommandColor(CommandSender sender, String alias, String[] args)
    {
        if (sender.isOp()) {
            if (args.length == 2)
                return tabCompleteSort(tabCommandVillageList(), args[1]);
            else if (args.length == 3) {
                if (args[1].isEmpty())
                    return empty;
                else
                {
                    if (alias.equals("색상")) {
                        return new ArrayList<String>() {{ add("(색상코드)"); }};
                    } else {
                        return new ArrayList<String>() {{ add("(ColorCode)"); }};
                    }
                }
            } else
                return empty;
        } else {
            return empty;
        }
    }

    private List<String> tabCommandIcon(CommandSender sender, String[] args)
    {
        if (sender.isOp()) {
            if (args.length == 2)
                return tabCompleteSort(tabCommandVillageList(), args[1]);
            else
                return empty;
        } else {
            return empty;
        }
    }



    private List<String> tabCommandVillageList()
    {
        List<String> list = new ArrayList<>();

        for (File file : Reference.getVillageFiles()) {
            list.add(file.getName().substring(0, file.getName().length() - 4));
        }

        Collections.sort(list);

        return list;
    }

    private List<String> tabOfflinePlayers()
    {
        List<String> list = new ArrayList<>();

        for (File f : Reference.getDataFiles()) {
            FileConfiguration config = Reference.getDataConfig(f.getName().substring(0, f.getName().length() - 4));
            list.add(config.getString("name"));
        }

        return list;
    }

    private List<String> tabVillagerList(String villageName)
    {
        List<String> list = new ArrayList<>();
        FileConfiguration config = Reference.getVillageConfig(villageName);

        List<?> member = config.getList("member");

        assert member != null;
        for (Object b : member) {
            FileConfiguration c = Reference.getDataConfig((String) b);

            list.add(c.getString("name"));
        }

        return list;
    }
}