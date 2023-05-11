package me.nanigans.pandoracrates.Crates;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.nanigans.pandoracrates.PandoraCrates;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.Map;

public class Crates {
    private final static PandoraCrates plugin = PandoraCrates.getPlugin(PandoraCrates.class);
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
        setName(block, this.name);

        final int height = Integer.parseInt(crateInfo.get("Hologram_Height").toString());


        Hologram hologram = HolographicDisplaysAPI.get(plugin).createHologram(loc.add(0, height, 0));

        hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&', crateInfo.get("Hologram_Name").toString()));
        return chest;

    }

    private static void setName(Block block, String name)
    {
        if (block.getType() != Material.CHEST) {
            return;
        }

        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
        final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        TileEntity te = nmsWorld.getTileEntity(blockPosition);
        if (!(te instanceof TileEntityChest))
        {
            return;
        }
        ((TileEntityChest) te).a(name);
    }

    public static String getName(Block block){
        if (block.getType() != Material.CHEST) {
            return null;
        }

        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
        final BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        TileEntity te = nmsWorld.getTileEntity(blockPosition);
        if (!(te instanceof TileEntityChest))
        {
            return null;
        }
        return ((TileEntityChest) te).getName();
    }

    public String getName() {
        return name;
    }

    public static PandoraCrates getPlugin() {
        return plugin;
    }

    public Map<String, Object> getCrateInfo() {
        return crateInfo;
    }
}
