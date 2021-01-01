package me.nanigans.pandoracrates;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.slikey.effectlib.EffectManager;
import me.nanigans.pandoracrates.Commands.CrateTab;
import me.nanigans.pandoracrates.Commands.CreateCrate;
import me.nanigans.pandoracrates.Commands.KeyGive;
import me.nanigans.pandoracrates.Commands.KeyTab;
import me.nanigans.pandoracrates.Events.CrateClickEvents;
import me.nanigans.pandoracrates.Utils.CustomizedObjectTypeAdapter;
import me.nanigans.pandoracrates.Utils.Glow;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
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
    public HashMap map = new HashMap<>();
    public static EffectManager manager;

    @Override
    public void onEnable() {

        manager = new EffectManager(PandoraCrates.getPlugin(PandoraCrates.class));
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }

        // Plugin startup logic
        getCommand("keygive").setExecutor(new KeyGive());
        getCommand("createcrate").setExecutor(new CreateCrate());
        getCommand("createcrate").setTabCompleter(new CrateTab());
        getCommand("keygive").setTabCompleter(new KeyTab());
        getServer().getPluginManager().registerEvents(new CrateClickEvents(), this);
        File crateFile = new File(getDataFolder(), "crates.json");
        File configFile = new File(getDataFolder(), "config.json");
        checkFileExists(crateFile);
        checkFileExists(configFile);

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
    }
}
