package net.lldv.pydow.playerexperience.components.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.player.Player;
import net.lldv.pydow.playerexperience.components.data.ExperienceLevel;

public class PlayerLevelUpEvent extends PlayerEvent {

    private final ExperienceLevel currentLevel;
    private final ExperienceLevel nextLevel;
    private static final HandlerList handlers = new HandlerList();

    public PlayerLevelUpEvent(Player player, ExperienceLevel currentLevel, ExperienceLevel nextLevel) {
        super(player);
        this.currentLevel = currentLevel;
        this.nextLevel = nextLevel;
    }

    @Override
    public Player getPlayer() {
        return super.getPlayer();
    }

    public ExperienceLevel getCurrentLevel() {
        return currentLevel;
    }

    public ExperienceLevel getNextLevel() {
        return nextLevel;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
