package me.nanigans.pandoracrates;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.slikey.effectlib.EffectManager;
import me.nanigans.pandoracrates.Commands.*;
import me.nanigans.pandoracrates.Commands.Tab.CrateTab;
import me.nanigans.pandoracrates.Commands.Tab.KeyTab;
import me.nanigans.pandoracrates.Commands.Tab.LootbagTab;
import me.nanigans.pandoracrates.Crates.CrateSelector;
import me.nanigans.pandoracrates.Events.CrateClickEvents;
import me.nanigans.pandoracrates.Utils.CustomizedObjectTypeAdapter;
import me.nanigans.pandoracrates.Utils.Glow;
import me.nanigans.pandoracrates.Utils.Packets.PacketInjector;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class PandoraCrates extends JavaPlugin {
    GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new CustomizedObjectTypeAdapter());
    public Map map = new HashMap<>();
    public static EffectManager manager;
    public static PacketInjector pj;

    @Override
    public void onEnable() {

        manager = new EffectManager(PandoraCrates.getPlugin(PandoraCrates.class));
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }
        pj = new PacketInjector();

        // Plugin startup logic
        getCommand("keygive").setExecutor(new KeyGive());
        getCommand("createcrate").setExecutor(new CreateCrate());
        getCommand("createcrate").setTabCompleter(new CrateTab());
        getCommand("keygive").setTabCompleter(new KeyTab());
        getCommand("lootbaggive").setExecutor(new LootBag());
        getCommand("lootbaggive").setTabCompleter(new LootbagTab());
        getServer().getPluginManager().registerEvents(new CrateClickEvents(), this);
        File crateFile = new File(getDataFolder(), "crates.json");
        File configFile = new File(getDataFolder(), "config.json");
        File lootPath = new File(getDataFolder(), "lootbags.json");

        checkFileExists(crateFile);
        checkFileExists(configFile);
        checkFileExists(lootPath);

        registerGlow();
    }

    private void checkFileExists(File configFile){

        if(!configFile.exists()) {

            saveResource(configFile.getName(), false);
            try {
                Gson gson = gsonBuilder.create();

                map = gson.fromJson(new FileReader(configFile), HashMap.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glow glow = new Glow(70);
            Enchantment.registerEnchantment(glow);
        }
        catch (IllegalArgumentException ignored){
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CrateSelector.getOpenCrates().forEach((i, j) -> j.forEach((l, m) -> {
            m.getFinish().cancel();
            m.getKeyObj().addUse();
            Player player = m.getPlayer();
            int indx = player.getInventory().first(m.getKey());
            if(indx == -1){
                player.getInventory().addItem(m.getKeyObj().getKey());
            }else player.getInventory().setItem(indx, m.getKeyObj().getKey());
            m.killAll();
        }));
    }
}
