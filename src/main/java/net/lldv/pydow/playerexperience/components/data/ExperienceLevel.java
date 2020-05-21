package net.lldv.pydow.playerexperience.components.data;

import java.util.List;

public class ExperienceLevel {

    private final int level;
    private final int experience;
    private final List<RewardData> rewards;

    public ExperienceLevel(int level, int experience, List<RewardData> rewards) {
        this.level = level;
        this.experience = experience;
        this.rewards = rewards;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public List<RewardData> getRewards() {
        return rewards;
    }
}
