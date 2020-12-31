package me.nanigans.pandoracrates.Crates;

import de.slikey.effectlib.effect.WarpEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.nanigans.pandoracrates.PandoraCrates;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrateSelector implements Listener {
    private final Player player;
    private final Map<String, Object> data;
    private final String name;
    private final ItemStack key;
    private boolean preventVelocity = false;
    private WarpEffect warp;
    private ArmorStand stand;
    private final static PandoraCrates plugin = PandoraCrates.getPlugin(PandoraCrates.class);
    public CrateSelector(Player player, Map<String, Object> crateData, String crateName, ItemStack key){
        this.player = player;
        this.data = crateData;
        this.name = crateName;
        this.key = key;
    }

    public void startSelector(){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        player.setVelocity(new Vector(0, 1.5, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                warp = new WarpEffect(PandoraCrates.manager);
                warp.asynchronous = true;
                warp.particle = ParticleEffect.SPELL;
                warp.grow = 0;
                warp.offset = new Vector(0, -1.4, 0);
                warp.setDynamicOrigin(new DynamicLocation(player));
                warp.radius = 0.5F;
                warp.infinite();
                warp.start();

                final Location location = player.getLocation();
                stand = player.getWorld().spawn(location, ArmorStand.class);
                stand.setGravity(false);
                stand.setVisible(false);
                stand.setPassenger(player);

                final List<Location> locations = circleParts(location.add(0, 2, 0), 2, 4);
                final List<Location> locations1 = circleParts(location, 1, 4);

                for (int i = 0; i < locations.size(); i++) {
                    locations.get(i).getBlock().setType(Material.SKULL);
                    Skull s = ((Skull) locations.get(i).getBlock().getState());
                    s.setOwner("Chest");
                    s.setRotation(locations.get(i).getBlock().getFace(locations1.get(i).getBlock()));
                    s.update();
                }

            }
        }.runTaskLater(plugin, 20);

    }

    public List<Location> circleParts(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;

    }

    @EventHandler
    public void sneakEvent(PlayerToggleSneakEvent event){

        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())){

            stand.remove();
            warp.cancel();
            HandlerList.unregisterAll(this);

        }

    }

}
