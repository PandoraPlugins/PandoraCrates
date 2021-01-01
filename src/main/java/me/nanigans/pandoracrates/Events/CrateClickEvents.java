package me.nanigans.pandoracrates.Events;

import me.nanigans.pandoracrates.Crates.CrateSelector;
import me.nanigans.pandoracrates.Crates.Crates;
import me.nanigans.pandoracrates.Crates.Key;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import me.nanigans.pandoracrates.Utils.NBTData;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CrateClickEvents implements Listener {

    @EventHandler
    public void onCrateHit(PlayerInteractEvent event){

        if(event.getAction().toString().toLowerCase().contains("right")){

            final Player player = event.getPlayer();
            if (event.getClickedBlock().getState() instanceof Chest) {

                final ItemStack item = event.getItem();
                    if(item != null && NBTData.containsNBT(item, Key.crateEnum)){
                        final String nbt = NBTData.getNBT(item, Key.crateEnum);

                        final String name = Crates.getName(event.getClickedBlock());
                        final Map<String, Object> crates = (Map<String, Object>) JsonUtil.getData("AllCrates");
                        final boolean allCrates = crates.containsKey(name);
                        if (allCrates) {
                            player.getWorld().playSound(player.getLocation(), Sound.valueOf("ENDERDRAGON_WINGS"), 100, 1);
                            event.setCancelled(true);

                            assert nbt != null;
                            if(!nbt.equals(name)) {
                                reverseVelocity(player);
                                return;
                            }

                            final CrateSelector crateSelector = new CrateSelector(player, ((Map<String, Object>) crates.get(nbt)), name, event.getItem());
                            crateSelector.startSelector();

                        }
                }

            }

        }

    }


    public static void reverseVelocity(Player player){

        final Object data = JsonUtil.getData("AllCrates.invalidCrateBounceVelocity");
        if(data != null)
        player.setVelocity(player.getVelocity().multiply(-1).multiply(
                Double.parseDouble(data.toString())));

    }


}
