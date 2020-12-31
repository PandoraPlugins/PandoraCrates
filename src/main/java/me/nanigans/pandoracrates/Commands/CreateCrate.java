package me.nanigans.pandoracrates.Commands;

import me.nanigans.pandoracrates.Crates.Crates;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CreateCrate implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("createcrate")){

            if(sender instanceof Player){

                if(sender.hasPermission("Crates.CreateCrate")){

                    if(args.length > 0) {

                        final Map<String, Object> allCrates = (Map<String, Object>) JsonUtil.getData("AllCrates");
                        if (allCrates != null) {
                            if (allCrates.containsKey(args[0])) {

                                final Player player = (Player) sender;
                                final Location loc = player.getLocation();
                                final Crates crates = new Crates(loc, ((Map<String, Object>) allCrates.get(args[0])), args[0]);
                                crates.createCrate();
                            }else {
                                sender.sendMessage(ChatColor.RED+"Please specify a valid crate");
                            }
                        }else sender.sendMessage(ChatColor.RED+"Something went wrong with this command");
                        return true;

                    }
                }

            }else{
                sender.sendMessage(ChatColor.RED+"Only players may use this command");
            }

        }

        return false;
    }
}
