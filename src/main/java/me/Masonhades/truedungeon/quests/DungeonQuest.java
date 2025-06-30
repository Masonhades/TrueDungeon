package me.Masonhades.truedungeon.quests;

public class DungeonQuest {
    public final String type;
    public final int level;
    public final String reward;
    public final String objective;

    public DungeonQuest(String type, int level, String reward, String objective) {
        this.type = type;
        this.level = level;
        this.reward = reward;
        this.objective = objective;
    }
}
