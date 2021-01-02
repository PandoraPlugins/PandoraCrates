package me.nanigans.pandoracrates.Crates;

import me.nanigans.pandoracrates.Utils.Glow;
import me.nanigans.pandoracrates.Utils.ItemUtils;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import me.nanigans.pandoracrates.Utils.NBTData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Key {

    private final Map<String, Object> crateInfo;
    private final Map<String, Object> keyInfo;
    private final String name;
    private ItemStack key;
    public final static String crateEnum = "CRATEKEY";
    private int uses;

    public Key(Map<String, Object> crateInfo, String crateName){
        this.crateInfo = crateInfo;
        this.keyInfo = ((Map<String, Object>) crateInfo.get("key"));
        this.name = crateName;
        this.key = null;
        this.uses = Integer.parseInt(keyInfo.get("uses").toString());
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
            this.key = item;
            this.uses = Integer.parseInt(NBTData.getNBT(item, "USES"));
    }

    public void addUse(){

        if (NBTData.containsNBT(key, "USES")) {
            this.uses+=1;
            key = NBTData.setNBT(key, "USES~"+uses);

            final ItemMeta itemMeta = key.getItemMeta();
            itemMeta.setLore(
                    (List<String>) ((JSONArray) JsonUtil.getData("AllCrates." + this.name + ".key.metaData.lore")).stream().map(i ->
                            ChatColor.translateAlternateColorCodes('&', i.toString().replaceAll("\\{uses}", String.valueOf(this.uses)))).collect(Collectors.toList())
            );
            this.key.setItemMeta(itemMeta);
        }

    }

    public void removeUse(){

        if (NBTData.containsNBT(key, "USES")) {
            this.uses-=1;
            key = NBTData.setNBT(key, "USES~"+uses);

            final ItemMeta itemMeta = key.getItemMeta();
            itemMeta.setLore(
                    (List<String>) ((JSONArray) JsonUtil.getData("AllCrates." + this.name + ".key.metaData.lore")).stream().map(i ->
                            ChatColor.translateAlternateColorCodes('&', i.toString().replaceAll("\\{uses}", String.valueOf(this.uses)))).collect(Collectors.toList())
            );
            this.key.setItemMeta(itemMeta);
        }

    }

    public ItemStack createKey(){

        final String material = keyInfo.get("material").toString();
        final Map<String, Object> keyMeta = ((Map<String, Object>) keyInfo.get("metaData"));
        final ItemStack item = ItemUtils.createItem(material, keyMeta.get("displayName").toString(), crateEnum+"~"+name, "USES~"+keyInfo.get("uses"), "UUID~"+UUID.randomUUID());

        final ItemMeta meta = item.getItemMeta();
        meta.setLore(
                (List<String>) ((JSONArray) JsonUtil.getData("AllCrates." + this.name + ".key.metaData.lore")).stream().map(i ->
                        ChatColor.translateAlternateColorCodes('&', i.toString().replaceAll("\\{uses}", keyInfo.get("uses").toString()))).collect(Collectors.toList())
        );
        if(Boolean.parseBoolean(keyInfo.get("glow").toString())){
            final Glow glow = new Glow(70);
            meta.addEnchant(glow, 1, true);
        }
        item.setItemMeta(meta);


        return item;
    }

    public void setKey(ItemStack key) {
        this.key = key;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public int getUses() {
        return uses;
    }

    public Map<String, Object> getKeyInfo() {
        return keyInfo;
    }

    public String getName() {
        return name;
    }

    public ItemStack getKey() {
        return key;
    }
}
