package me.nanigans.pandoracrates.LootBags;

import me.nanigans.pandoracrates.Utils.Glow;
import me.nanigans.pandoracrates.Utils.ItemUtils;
import me.nanigans.pandoracrates.Utils.JsonUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LootBag {
    public static final String LOOTBAG = "LOOTBAG";

    public static Map<String, Integer> generateRandomRewards(Map<String, Object> bagData){

        final Map<String, Object> rewards = (Map<String, Object>) bagData.get("rewards");
        if(!rewards.isEmpty()){
            final int maxItems = Integer.parseInt(bagData.get("maxItems").toString());
            final List<String> keys = new ArrayList<>(rewards.keySet());
            final Map<String, Integer> rewardMap = new HashMap<>();
            int itemAmt = 0;
            while(itemAmt < maxItems){
                int itemIndx = ThreadLocalRandom.current().nextInt(keys.size());
                Map<String, Object> itemData = (Map<String, Object>) rewards.get(keys.get(itemIndx));
                final String name = keys.get(itemIndx);
                final int amt = Integer.parseInt(itemData.get("amountAmp").toString());
                final int amountAdd = amt * ThreadLocalRandom.current().nextInt(3);
                if(rewardMap.containsKey(name)){
                    rewardMap.put(name, rewardMap.get(name)+ amountAdd);
                }else rewardMap.put(name, amountAdd);
                itemAmt += amountAdd;
            }
            return rewardMap;
        }
        return null;
    }

    public static ItemStack createLootBag(Map<String, Object> bagData, String name){
        final String material = bagData.get("material").toString();
        final String displayName = ChatColor.translateAlternateColorCodes('&', bagData.get("displayName").toString());
        final ItemStack item = ItemUtils.createItem(material, displayName, LOOTBAG + "~" + name);

        final ItemMeta meta = item.getItemMeta();
        meta.setLore(
                (List<String>) ((JSONArray) JsonUtil.getLootData("AllBags." + name + ".lore")).stream().map(i ->
                        ChatColor.translateAlternateColorCodes('&', i.toString())).collect(Collectors.toList())
        );
        if(Boolean.parseBoolean(bagData.get("glow").toString())){
            final Glow glow = new Glow(70);
            meta.addEnchant(glow, 1, true);
        }
        item.setItemMeta(meta);

        return item;
    }

}
