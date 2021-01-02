package me.nanigans.pandoracrates.Commands.Tab;

import me.nanigans.pandoracrates.Utils.JsonUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LootbagTab implements TabCompleter {


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(command.getName().equalsIgnoreCase("lootbaggive")){

            if(sender.hasPermission("LootBag.Give")){

                switch (args.length) {
                    case 2: return new ArrayList<>(((Map<String, Object>) JsonUtil.getLootData("AllBags")).keySet());
                    case 3: return Collections.singletonList("<amount>");
                }

            }

        }

        return null;
    }
}
