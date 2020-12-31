package me.nanigans.pandoracrates.Commands;

import me.nanigans.pandoracrates.Utils.JsonUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeyTab implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(command.getName().equalsIgnoreCase("keygive")){

            if(sender.hasPermission("Crates.KeyGive")){

                switch (args.length) {
                    case 2: return new ArrayList<>(((Map<String, Object>) JsonUtil.getData("AllCrates")).keySet());
                    case 3: return Collections.singletonList("<amount>");
                }

            }

        }

        return null;
    }
}
