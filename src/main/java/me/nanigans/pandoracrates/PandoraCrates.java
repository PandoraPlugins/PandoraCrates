package me.nanigans.pandoracrates;

import me.nanigans.pandoracrates.Commands.KeyGive;
import me.nanigans.pandoracrates.Utils.Glow;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class PandoraCrates extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        getCommand("keygive").setExecutor(new KeyGive());

        registerGlow();
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
