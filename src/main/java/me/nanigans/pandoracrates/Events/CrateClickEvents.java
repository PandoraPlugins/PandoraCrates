package me.nanigans.pandoracrates.Events;

import me.nanigans.pandoracrates.Crates.CrateSelector;
import me.nanigans.pandoracrates.Crates.Crates;
import me.nanigans.pandoracrates.Crates.Key;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import me.nanigans.pandoracrates.Utils.NBTData;
import me.nanigans.pandoracrates.Utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrateClickEvents implements Listener {

    @EventHandler
    public void onCrateHit(PlayerInteractEvent event){

        if(event.getAction().toString().toLowerCase().contains("right")){

            final Player player = event.getPlayer();
            if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Chest) {

                final String name = Crates.getName(event.getClickedBlock());
                final Map<String, Object> crates = (Map<String, Object>) JsonUtil.getData("AllCrates");

                if(crates != null && name != null) {
                    final boolean allCrates = crates.containsKey(name);

                    final ItemStack item = event.getItem();
                    if (allCrates) {
                        if (item != null && NBTData.containsNBT(item, Key.crateEnum)) {
                        final String nbt = NBTData.getNBT(item, Key.crateEnum);
                            ConfigUtils.playSound("sounds.clickCrate", player);
                            event.setCancelled(true);

                            assert nbt != null;
                            if (!nbt.equals(name)) {
                                reverseVelocity(player);
                                return;
                            }
                            if(CrateSelector.getOpenCrates().containsKey(event.getClickedBlock().getLocation())){
                                if(!CrateSelector.getOpenCrates().get(event.getClickedBlock().getLocation()).isEmpty()){
                                    player.sendMessage(ChatColor.RED+"Someone is already using this crate");
                                    return;
                                }
                            }
                            final Map<String, Object> crate = (Map<String, Object>) crates.get(nbt);

                            ConfigUtils.sendMessage("messages.openCrate", player,
                                    "\\{crate_name}:"+ChatColor.stripColor(
                                            ChatColor.translateAlternateColorCodes('&', crate.get("Hologram_Name").toString())));


                            final CrateSelector crateSelector = new CrateSelector(player, crate, event.getClickedBlock().getLocation(), event.getItem());
                            crateSelector.startSelector();
                            CrateSelector.getOpenCrates().put(event.getClickedBlock().getLocation(),
                                    new HashMap<UUID, CrateSelector>(){{put(player.getUniqueId(), crateSelector);}});


                        }else{
                            reverseVelocity(player);
                            ConfigUtils.playSound("sounds.clickCrate", player);
                            ConfigUtils.sendMessage("messages.noKeyError", player);
                            event.setCancelled(true);
                        }
                    }
                }

            }

        }

    }


    public static void reverseVelocity(Player player){

        final Object data = JsonUtil.getData("AllCrates.invalidCrateBounceVelocity");
        if(data != null)
        player.setVelocity(player.getLocation().getDirection().multiply(-1).multiply(
                Double.parseDouble(data.toString())));

    }


}
