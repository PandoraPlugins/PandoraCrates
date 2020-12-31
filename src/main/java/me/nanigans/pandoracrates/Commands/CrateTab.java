package me.nanigans.pandoracrates.Commands;

import me.nanigans.pandoracrates.Utils.JsonUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrateTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(command.getName().equalsIgnoreCase("createcrate")){

            if(args.length == 1){
                return new ArrayList<>(((Map<String, Object>) JsonUtil.getData("AllCrates")).keySet());
            }

        }
        return null;
    }
}
