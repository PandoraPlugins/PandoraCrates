package me.nanigans.pandoracrates.LootBags;

import me.nanigans.pandoracrates.Utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class LootBag {
    public static final String LOOTBAG = "LOOTBAG";

    public static ItemStack createLootBag(Map<String, Object> bagData, String name){
        final String material = bagData.get("material").toString();
        final String displayName = ChatColor.translateAlternateColorCodes('&', bagData.get("displayName").toString());
        return ItemUtils.createItem(material, displayName, LOOTBAG + "~" +name);
    }

}
