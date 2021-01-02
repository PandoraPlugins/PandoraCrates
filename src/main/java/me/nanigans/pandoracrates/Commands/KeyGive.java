package me.nanigans.pandoracrates.Commands;

import me.nanigans.pandoracrates.Crates.Key;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class KeyGive implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("keygive")){

            if(sender.hasPermission("Crates.KeyGive")){

                if(args.length > 1){

                    Player player = Bukkit.getPlayerExact(args[0]);
                    if(player != null){

                        final Map<String, Object> allCrates = (Map<String, Object>) JsonUtil.getData("AllCrates");
                        if(allCrates != null){

                            if (allCrates.containsKey(args[1])) {

                                final Key key = new Key(((Map<String, Object>) allCrates.get(args[1])), args[1]);
                                final ItemStack key1 = key.createKey();
                                if(args.length > 2 && NumberUtils.isNumber(args[2])){
                                    key1.setAmount(Integer.parseInt(args[2]));
                                }
                                if (!player.getInventory().addItem(key1).isEmpty()) {
                                    player.getWorld().dropItem(player.getLocation(), key1);
                                }
                                return true;

                            }

                        }

                    }else{
                        sender.sendMessage(ChatColor.RED+"Invalid Player");
                        return true;
                    }

                }else{
                    sender.sendMessage(ChatColor.RED+"Invalid Arguments");
                    return false;
                }

            }

        }

        return false;
    }
}
