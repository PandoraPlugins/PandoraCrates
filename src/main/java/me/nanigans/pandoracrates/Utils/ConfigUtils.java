package me.nanigans.pandoracrates.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class ConfigUtils {

    public static void sendMessage(String messagePath, Player player, String... replacers){

        final Object configData = JsonUtil.getConfigData(messagePath);
        if(configData != null && !configData.toString().isEmpty()){

            String s = ChatColor.translateAlternateColorCodes('&', configData.toString());
            for (String replacer : replacers) {
                final String[] split = replacer.split(":");
                s = s.replaceAll(split[0], split[1]);
            }
            player.sendMessage(s);

        }

    }

    public static void playSound(String soudPath, Player player){

        final Object soundPath = JsonUtil.getConfigData(soudPath);
        if(soundPath != null){

            final Map<String, Object> soundData = (Map<String, Object>) soundPath;
            Sound sound = Sound.valueOf(soundData.get("name").toString());
            float vol = Float.parseFloat(soundData.get("volume").toString());
            float pitch = Float.parseFloat(soundData.get("pitch").toString());

            if(soundData.containsKey("forPlayer") && !Boolean.parseBoolean(soundData.get("forPlayer").toString())){
                player.getWorld().playSound(player.getLocation(), sound, vol, pitch);
            }else player.playSound(player.getLocation(), sound, vol, pitch);

        }

    }

}
