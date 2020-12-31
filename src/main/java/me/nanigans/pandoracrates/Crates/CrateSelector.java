package me.nanigans.pandoracrates.Crates;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.SphereEffect;
import de.slikey.effectlib.effect.StarEffect;
import de.slikey.effectlib.effect.WarpEffect;
import de.slikey.effectlib.util.DynamicLocation;
import de.slikey.effectlib.util.ParticleEffect;
import me.nanigans.pandoracrates.PandoraCrates;
import me.nanigans.pandoracrates.Utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrateSelector implements Listener {
    private final Player player;
    private final Map<String, Object> data;
    private final String name;
    private final ItemStack key;
    private WarpEffect warp;
    private ArmorStand stand;
    private final static PandoraCrates plugin = PandoraCrates.getPlugin(PandoraCrates.class);
    private final List<ArmorStand> armorStands = new ArrayList<>();
    private final Reward reward;
    private int clicksLeft;
    private ArmorStand[] clickedStands;

    public CrateSelector(Player player, Map<String, Object> crateData, String crateName, ItemStack key){
        this.player = player;
        this.data = crateData;
        this.name = crateName;
        this.key = key;
        this.clicksLeft = Integer.parseInt(((Map<String, Object>) crateData.get("key")).get("rewardsPerKey").toString());
        this.reward = new Reward(this);
        this.clickedStands = new ArmorStand[clicksLeft];
    }

    @EventHandler
    public void armorStandClick(PlayerInteractAtEntityEvent event){
        System.out.println("event.getClickedPosition() = " + event.getClickedPosition());
        System.out.println("event.getRightClicked() = " + event.getRightClicked());
        if(event.getPlayer().getUniqueId().equals(this.player.getUniqueId())) {
            event.setCancelled(true);
            final Entity rightClicked = event.getRightClicked();
            if(rightClicked instanceof ArmorStand){
                if(rightClicked.getPassenger() == null) {
                    final Map<String, Object> randomRewards = reward.getRandomRewards();
                    ItemStack reward = ItemUtils.createItem(randomRewards.get("material").toString(), null);
                    final Item item = player.getWorld().dropItem(player.getLocation(), reward);
                    item.setPickupDelay(Integer.MAX_VALUE);
                    rightClicked.setPassenger(item);
                    item.setCustomName(ChatColor.translateAlternateColorCodes('&', randomRewards.get("displayName").toString()));
                    item.setCustomNameVisible(true);
                    this.reward.getRewardCmds().add(randomRewards.get("command").toString());
                    rightClicked.setCustomNameVisible(false);

                    player.getWorld().playSound(player.getLocation(), Sound.valueOf("FIREWORK_BLAST"), 10, 1);
                    StarEffect effect = new StarEffect(PandoraCrates.manager);
                    effect.asynchronous = true;
                    effect.type = EffectType.INSTANT;
                    effect.innerRadius = 0.1F;
                    effect.spikeHeight = 0.5F;
                    effect.spikesHalf = 4;
                    effect.particle = ParticleEffect.FIREWORKS_SPARK;
                    effect.offset = new Vector(0, 1, 0);
                    effect.setDynamicOrigin(new DynamicLocation(rightClicked));
                    effect.start();
                    this.clicksLeft--;
                    clickedStands[clicksLeft] = ((ArmorStand) rightClicked);
                    this.armorStands.remove(rightClicked);

                    if(this.clicksLeft == 0 || this.armorStands.size() == this.clickedStands.length){
                        endSelector();
                    }
                    System.out.println("effect = " + effect.getLocation());
                }

            }
        }

    }

    private void endSelector(){

        for (ArmorStand armorStand : this.armorStands) {

            SphereEffect effect = new SphereEffect(PandoraCrates.manager);
            effect.particle = ParticleEffect.SMOKE_NORMAL;
            effect.asynchronous = true;
            effect.radius = 0.35;
            effect.type = EffectType.INSTANT;
            effect.start();
            effect.setDynamicOrigin(new DynamicLocation(armorStand.getEyeLocation()));
            effect.callback = armorStand::remove;

        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (ArmorStand clickedStand : CrateSelector.this.clickedStands) {
                    clickedStand.setGravity(true);
                    clickedStand.setVelocity(player.getEyeLocation().toVector().subtract(clickedStand.getLocation().toVector()).normalize().divide(new Vector(2, 2, 2)));
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        HandlerList.unregisterAll(CrateSelector.this);
                        warp.cancel();
                        stand.remove();
                        for (ArmorStand clickedStand : clickedStands) {
                            clickedStand.remove();
                            clickedStand.getPassenger().remove();
                            giveRewards();
                        }
                    }
                }.runTaskLaterAsynchronously(plugin, 10);
            }
        }.runTaskLaterAsynchronously(plugin, 20);
    }

    private void giveRewards(){

        for (String rewardCmd : this.reward.getRewardCmds()) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), rewardCmd.replaceAll("\\{player}", player.getName()));
        }

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

                final int amount = Integer.parseInt(data.get("Crate_Chest_Number").toString());
                final List<Location> locations = circleParts(location.add(0, 2, 0), 2, amount);
                Vector pVec = player.getLocation().toVector();

                ItemStack item = createCustomHead(data.get("Skull_TextureValue").toString());
                Location pLoc = player.getEyeLocation();
                pLoc.add(0, 3, 0);
                for (final Location loc : locations) {
                    ArmorStand stand = loc.getWorld().spawn(pLoc, ArmorStand.class);
                    stand.setHelmet(item);
                    stand.setVisible(false);
                    stand.setSmall(true);
                    stand.setCustomName("Click Me!");
                    stand.setCustomNameVisible(true);
                    final Vector vector = loc.toVector().subtract(new Vector(0, 2, 0));
                    final Vector facing = pVec.clone().subtract(vector).normalize();
                    final double yaw = -Math.atan2(facing.getX(), facing.getZ());
                    final double pitch = -Math.atan2(facing.getY(), Math.hypot(facing.getX(), facing.getZ()));
                    stand.setHeadPose(new EulerAngle(pitch, yaw, 0));
                    stand.setVelocity(facing.multiply(-0.5));
                    armorStands.add(stand);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (ArmorStand armorStand : armorStands) {
                            armorStand.setGravity(false);
                        }
                    }
                }.runTaskLaterAsynchronously(plugin, 10);

            }
        }.runTaskLater(plugin, 20);

    }

    public static List<Location> circleParts(Location center, double radius, int amount) {
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
            this.armorStands.forEach(i -> {
                if(i.getPassenger() != null){
                    i.getPassenger().remove();
                }
                i.remove();
            });
            HandlerList.unregisterAll(this);

        }

    }



    public ItemStack createCustomHead(String value) {
        String signature = "H116D5fhmj/7BVWqiRQilXmvoJO6wJzXH4Dvou6P2o9YMb+HaJT8s9+zt03GMYTipzK+NsW2D2JfzagnxLUTuiOtrCHm6V2udOM0HG0JeL4zR0Wn5oHmu+S7kUPUbt7HVlKaRXry5bobFQ06nUf7hOV3kPfpUJsfMajfabmoJ9RGMRVot3uQszjKOHQjxyAjfHP2rjeI/SktBrSscx0DzwBW9LCra7g/6Cp7/xPQTIZsqz2Otgp6i2h3YpXJPy02j4pIk0H4biR3CaU7FB0V4/D1Hvjd08giRvUpqF0a1w9rbpIWIH5GTUP8eLFdG/9SnHqMCQrTj4KkQiN0GdBO18JvJS/40LTn3ZLag5LBIa7AyyGus27N3wdIccvToQ6kHHRVpW7cUSXjircg3LOsSQbJmfLoVJ/KAF/m+de4PxIjOJIcbiOkVyQfMQltPg26VzRiu3F0qRvJNAAydH8AHdaqhkpSf6yjHqPU3p3BHFJld5o59WoD4WNkE3wOC//aTpV/f9RJ0JQko08v2mGBVKx7tpN7vHD1qD5ILzV1nDCV1/qbKgiOK9QmdXqZw9J3pM/DHtZ6eiRKni9BuGWlbWFN/qfFO2xY+J7SYFqTxBbffmvwvuF83QP5UdRTNVLYoV5S+yR5ac7fVWUZmLbq7tawyuCu0Dw24M9E1BSnpSc=";

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
        skull.setItemMeta(skullMeta);

        return skull;
    }


    public Player getPlayer() {
        return player;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public static PandoraCrates getPlugin() {
        return plugin;
    }

    public List<ArmorStand> getArmorStands() {
        return armorStands;
    }
}
