package me.nanigans.pandoracrates.Crates;

import me.nanigans.pandoracrates.Utils.ConfigUtils;

import java.util.TimerTask;

public class CrateFinish extends TimerTask {
    CrateSelector crate;
    public CrateFinish(CrateSelector toFinish){
        this.crate = toFinish;
    }

    @Override
    public void run() {
        crate.getKeyObj().addUse();
        int indx = crate.getPlayer().getInventory().first(crate.getKey());
        if (indx == -1) {
            crate.getPlayer().getInventory().addItem(crate.getKeyObj().getKey());
        } else crate.getPlayer().getInventory().setItem(indx, crate.getKeyObj().getKey());
        ConfigUtils.sendMessage("messages.timerRanOut", crate.getPlayer());
        ConfigUtils.playSound("sounds.timeUp", crate.getPlayer());
        crate.killAll();
    }
}
