package me.nanigans.pandoracrates.Crates;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;

import java.util.Map;

public class Crates {
    private final Location loc;
    private final Map<String, Object> crateInfo;
    private final String name;
    public Crates(Location playerLoc, Map<String, Object> crateInfo, String crateName){
        this.loc = playerLoc;
        this.crateInfo = crateInfo;
        this.name = crateName;
    }


    public Chest createCrate(){

        final Block block = loc.getBlock();
        block.setType(Material.CHEST);
        final Chest chest = ((Chest) block.getState());
        chest.setCustomName(name);
        chest.setLock(ChatColor.ALL_CODES);

        final int height = Integer.parseInt(crateInfo.get("Hologram_Height").toString());

        ArmorStand stand = loc.getWorld().spawn(loc.clone().add(0, height, 0), ArmorStand.class);
        stand.setInvulnerable(true);
        stand.setSmall(true);
        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', crateInfo.get("Hologram_Name").toString()));
        stand.setVisible(false);
        stand.setGravity(false);

        return chest;

    }
}
