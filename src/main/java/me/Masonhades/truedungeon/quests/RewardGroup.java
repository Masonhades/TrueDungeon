package me.Masonhades.truedungeon.quests;

import java.util.List;

public class RewardGroup {
    public float chance = 1.0f;
    public List<RewardItem> items;


    public RewardGroup(float chance, List<RewardItem> rewards) {
        this.chance = chance;
        this.items = rewards;
    }
}
