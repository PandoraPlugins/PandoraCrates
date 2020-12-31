package me.nanigans.pandoracrates.Crates;

import me.nanigans.pandoracrates.Utils.Glow;
import me.nanigans.pandoracrates.Utils.ItemUtils;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import me.nanigans.pandoracrates.Utils.NBTData;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Key {

    private final Map<String, Object> crateInfo;
    private final Map<String, Object> keyInfo;
    private final String name;
    private final static String crateEnum = "CRATEKEY";

    public Key(Map<String, Object> crateInfo, String crateName){
        this.crateInfo = crateInfo;
        this.keyInfo = ((Map<String, Object>) crateInfo.get("key"));
        this.name = crateName;
    }

    /**
     * creates a new key object from an itemstack
     * @requires itemstack to be guarenteed to be an object in the crates.json
     * @param item the itemstack to make a key object from
     */
    public Key(ItemStack item){
            final String name = NBTData.getNBT(item, crateEnum);
            this.name = name;
            this.crateInfo = ((Map<String, Object>) JsonUtil.getData("AllCrates." + name));
            this.keyInfo = ((Map<String, Object>) crateInfo.get("key"));
    }

    public ItemStack createKey(){

        final String material = keyInfo.get("material").toString();
        final Map<String, Object> keyMeta = ((Map<String, Object>) keyInfo.get("metaData"));
        final boolean validEnum = EnumUtils.isValidEnum(Material.class, material);
        final ItemStack item = validEnum ? ItemUtils.createItem(Material.valueOf(material),
                    ChatColor.translateAlternateColorCodes('&', keyMeta.get("displayName").toString()), crateEnum+"~"+this.name) :
            ItemUtils.createItem(material,
                    ChatColor.translateAlternateColorCodes('&', keyMeta.get("displayName").toString()), crateEnum+"~"+this.name);

        final ItemMeta meta = item.getItemMeta();
        meta.setLore(
                (List<String>) ((JSONArray) JsonUtil.getData("AllCrates." + this.name + ".key.metaData.lore")).stream().map(i ->
                        ChatColor.translateAlternateColorCodes('&', i.toString())).collect(Collectors.toList())
        );
        if(Boolean.parseBoolean(keyInfo.get("glow").toString())){
            final Glow glow = new Glow(70);
            meta.addEnchant(glow, 1, true);
        }
        item.setItemMeta(meta);


        return item;
    }

}
