package me.Masonhades.truedungeon.quests;

public class RewardItem {
    public String id;
    public int count;

    public RewardItem(String id, int count) {
        this.id = id;
        this.count = count;
    }

    @Override
    public String toString() {
        return count + "x " + id;
    }
}

