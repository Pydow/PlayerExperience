package net.lldv.pydow.playerexperience.listeners;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import net.lldv.pydow.playerexperience.components.api.ExperienceAPI;

import java.util.concurrent.CompletableFuture;

public class EventListener implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> {
            if (!ExperienceAPI.userExists(event.getPlayer().getName())) ExperienceAPI.createUserData(event.getPlayer().getName());
            ExperienceAPI.cachedPlayerLevel.put(event.getPlayer().getName(), ExperienceAPI.getPlayerLevel(event.getPlayer().getName()));
            ExperienceAPI.cachedExperience.put(event.getPlayer().getName(), ExperienceAPI.getPlayerExperience(event.getPlayer().getName()));
            ExperienceAPI.checkForLevelUp(event.getPlayer());
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        ExperienceAPI.setExperience(event.getPlayer().getName());
        ExperienceAPI.cachedExperience.remove(event.getPlayer().getName());
        ExperienceAPI.cachedPlayerLevel.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getLevel().getName().equals("plots")) return;
        if (ExperienceAPI.breakBlockXp.get(block.getId().getName() + ":" + block.getMeta()) == null) return;
        int experience = ExperienceAPI.breakBlockXp.get(block.getId().getName() + ":" + block.getMeta());
        ExperienceAPI.addExperienceCache(event.getPlayer(), experience);
    }

    /*@EventHandler
    public void on(EntityDeathEvent event) {
    }*/
}
