package me.nanigans.pandoracrates.Utils;


import me.nanigans.pandoracrates.PandoraCrates;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonUtil {

    private static final PandoraCrates plugin = PandoraCrates.getPlugin(PandoraCrates.class);

    public static File jsonPath = new File(plugin.getDataFolder() + "/crates.json");
    public static File configPath = new File(plugin.getDataFolder()+"/config.json");
    public static File lootPath = new File(plugin.getDataFolder()+"/lootbags.json");


    public static Object getLootData(String path){

        try {
            JSONParser jsonParser = new JSONParser();
            Object parsed = jsonParser.parse(new FileReader(lootPath));
            JSONObject jsonObject = (JSONObject) parsed;

            JSONObject currObject = (JSONObject) jsonObject.clone();
            if(path == null) return currObject;
            String[] paths = path.split("\\.");

            for (String s : paths) {

                if (currObject.get(s) instanceof JSONObject)
                    currObject = (JSONObject) currObject.get(s);
                else return currObject.get(s);

            }

            return currObject;
        }catch(IOException | ParseException ignored){
            return null;
        }

    }

    public static Object getConfigData(String path){

        try {
            JSONParser jsonParser = new JSONParser();
            Object parsed = jsonParser.parse(new FileReader(configPath));
            JSONObject jsonObject = (JSONObject) parsed;

            JSONObject currObject = (JSONObject) jsonObject.clone();
            if(path == null) return currObject;
            String[] paths = path.split("\\.");

            for (String s : paths) {

                if (currObject.get(s) instanceof JSONObject)
                    currObject = (JSONObject) currObject.get(s);
                else return currObject.get(s);

            }

            return currObject;
        }catch(IOException | ParseException ignored){
            return null;
        }

    }

    public static Object getData(String path) {

        try {
            JSONParser jsonParser = new JSONParser();
            Object parsed = jsonParser.parse(new FileReader(jsonPath));
            JSONObject jsonObject = (JSONObject) parsed;

            JSONObject currObject = (JSONObject) jsonObject.clone();
            if(path == null) return currObject;
            String[] paths = path.split("\\.");

            for (String s : paths) {

                if (currObject.get(s) instanceof JSONObject)
                    currObject = (JSONObject) currObject.get(s);
                else return currObject.get(s);

            }

            return currObject;
        }catch(IOException | ParseException ignored){
            return null;
        }
    }

}
