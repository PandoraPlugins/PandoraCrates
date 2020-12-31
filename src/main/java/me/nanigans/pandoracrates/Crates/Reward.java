package me.nanigans.pandoracrates.Crates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Reward {
    private final Map<String, Double> chanceMap = new HashMap<>();
    private final Map<String, Object> rewardMap;
    private final List<Map<String, Object>> rewardCmds = new ArrayList<>();

    public Reward(CrateSelector map){
        this.rewardMap = ((Map<String, Object>) map.getData().get("rewards"));
        double totalEnabledRewardsChance = 0;
        for (Map.Entry<String, Object> stringRewardEntry : rewardMap.entrySet()) {
            totalEnabledRewardsChance += Double.parseDouble(((Map<String, Object>) stringRewardEntry.getValue()).get("chance").toString());
        }

        for (Map.Entry<String, Object> stringRewardEntry : rewardMap.entrySet()) {
                double chance = Double.parseDouble(((Map<String, Object>) stringRewardEntry.getValue()).get("chance").toString()) / totalEnabledRewardsChance;
            chanceMap.put(stringRewardEntry.getKey(), chance);
        }

    }

    public Map<String, Object> getRandomRewards() {

        double roll = ThreadLocalRandom.current().nextDouble(1);
        double currentSum = 0;
        for (Map.Entry<String, Object> stringRewardEntry : rewardMap.entrySet()) {
            currentSum += chanceMap.get(stringRewardEntry.getKey());
            if(currentSum >= roll)
                return ((Map<String, Object>) stringRewardEntry.getValue());
        }
        return null;
    }

    public List<Map<String, Object>> getRewardCmds() {
        return rewardCmds;
    }
}
