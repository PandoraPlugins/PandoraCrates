package me.nanigans.pandoracrates.Events;

import me.nanigans.pandoracrates.Crates.CrateFinish;
import me.nanigans.pandoracrates.Crates.CrateSelector;
import me.nanigans.pandoracrates.Crates.Crates;
import me.nanigans.pandoracrates.Crates.Key;
import me.nanigans.pandoracrates.LootBags.LootBag;
import me.nanigans.pandoracrates.PandoraCrates;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import me.nanigans.pandoracrates.Utils.NBTData;
import me.nanigans.pandoracrates.Utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

public class CrateClickEvents implements Listener {

    private static final PandoraCrates plugin = PandoraCrates.getPlugin(PandoraCrates.class);

    @EventHandler
    public void onLootBagClick(PlayerInteractEvent event) {

        if (event.getAction().toString().toLowerCase().contains("right")) {

            final Player player = event.getPlayer();
            final ItemStack item = event.getItem();

            if (event.getItem() != null && NBTData.containsNBT(item, LootBag.LOOTBAG)) {

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        final String nbt = NBTData.getNBT(item, LootBag.LOOTBAG);
                        Map<String, Object> data = ((Map<String, Object>) JsonUtil.getLootData("AllBags." + nbt));
                        if (data != null) {
                            final Map<String, Integer> rewards = LootBag.generateRandomRewards(data);
                            if (rewards != null) {
                                Map<String, Object> rewardsMap = ((Map<String, Object>) data.get("rewards"));
                                ConfigUtils.sendMessage("messages.rewardLootBagTitle", player);
                                rewards.forEach((i, j) ->{
                                    final Map<String, Object> reward = (Map<String, Object>) rewardsMap.get(i);

                                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                                        reward.get("command").toString().replaceAll("\\{player}",
                                                player.getName()).replaceAll("\\{amount}", String.valueOf(j)));
                                    final Object rewardMsg = reward.get("rewardMsg");
                                    if(rewardMsg != null && !rewardMsg.toString().isEmpty()){
                                        player.sendMessage(rewardMsg.toString().replaceAll("\\{amount}", String.valueOf(j)));
                                    }
                                });
                                ConfigUtils.sendMessage("messages.rewardLootBagFooter", player);
                                ConfigUtils.playSound("sounds.lootbagOpen", player);
                            } else {
                                ConfigUtils.sendMessage("messages.emptyLootBag", player);
                            }
                        } else {
                            ConfigUtils.sendMessage("messages.emptyLootBag", player);
                        }
                        subtractItem(item, player);

                    }

                }.runTaskAsynchronously(plugin);
            }

        }

    }

    private static void subtractItem(ItemStack item, Player player){

        final PlayerInventory inv = player.getInventory();
        final int first = inv.first(item);
        final ItemStack item1 = inv.getItem(first);
        int amount = item1.getAmount();
        item1.setAmount(--amount);
        if(amount <= 0){
            inv.setItem(first, null);
        }

    }

    @EventHandler
    public void onCrateHit(PlayerInteractEvent event) {

        if (event.getAction().toString().toLowerCase().contains("right")) {

            final Player player = event.getPlayer();
            if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Chest) {

                final String name = Crates.getName(event.getClickedBlock());
                final Map<String, Object> crates = (Map<String, Object>) JsonUtil.getData("AllCrates");

                if (crates != null && name != null) {
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
                            if (CrateSelector.getOpenCrates().containsKey(event.getClickedBlock().getLocation())) {
                                if (!CrateSelector.getOpenCrates().get(event.getClickedBlock().getLocation()).isEmpty()) {
                                    ConfigUtils.sendMessage("messages.crateInUse", player);
                                    reverseVelocity(player);
                                    ;
                                    return;
                                }
                            }
                            final Map<String, Object> crate = (Map<String, Object>) crates.get(nbt);

                            ConfigUtils.sendMessage("messages.openCrate", player,
                                    "\\{crate_name}:" + ChatColor.stripColor(
                                            ChatColor.translateAlternateColorCodes('&', crate.get("Hologram_Name").toString())));


                            final CrateSelector crateSelector = new CrateSelector(player, crate, event.getClickedBlock().getLocation(), event.getItem(), null);
                            final Object data = JsonUtil.getData("AllCrates.timeToComplete");
                            if (data != null) {
                                final CrateFinish crateFinish = new CrateFinish(crateSelector);
                                Timer t = new Timer();
                                t.schedule(crateFinish, Long.parseLong(data.toString()));
                                crateSelector.setFinish(crateFinish);
                            }
                            crateSelector.startSelector();

                            CrateSelector.getOpenCrates().put(event.getClickedBlock().getLocation(),
                                    new HashMap<UUID, CrateSelector>() {{
                                        put(player.getUniqueId(), crateSelector);
                                    }});


                        } else {
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


    public static void reverseVelocity(Player player) {

        final Object data = JsonUtil.getData("AllCrates.invalidCrateBounceVelocity");
        if (data != null)
            player.setVelocity(player.getLocation().getDirection().multiply(-1).multiply(
                    Double.parseDouble(data.toString())));

    }


}
