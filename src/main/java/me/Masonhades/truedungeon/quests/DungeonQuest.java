package me.Masonhades.truedungeon.quests;

import java.util.List;

public class DungeonQuest {
    public final String type;
    public final int level;
    public final List<RewardItem> rewards;
    public final ObjectiveData objective;

    public DungeonQuest(String type, int level, List<RewardItem> rewards, ObjectiveData objective) {
        this.type = type;
        this.level = level;
        this.rewards = rewards;
        this.objective = objective;
    }
}

