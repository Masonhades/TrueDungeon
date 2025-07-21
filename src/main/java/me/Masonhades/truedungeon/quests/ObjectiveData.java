package me.Masonhades.truedungeon.quests;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.List;
import java.util.Map;

public class ObjectiveData {
    public String type;
    public List<String> allowedTypes;
    public Map<String, Object> parameters;
    public Float rewardMultiplier;
    public List<ObjectiveData> combo;
    public List<Subgoal> subgoals;
    public Integer amount;


    public ObjectiveData() {
    }

    public static class Subgoal {
        public String type;
        public int amount;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", this.type);

        if (this.rewardMultiplier != null) {
            tag.putFloat("rewardMultiplier", this.rewardMultiplier);
        }

        if ("combo".equals(this.type) && this.subgoals != null) {
            ListTag list = new ListTag();
            for (Subgoal sg : subgoals) {
                CompoundTag sub = new CompoundTag();
                sub.putString("type", sg.type);
                sub.putInt("amount", sg.amount);
                list.add(sub);
            }
            tag.put("subgoals", list);
        } else if (this.amount != null) {
            tag.putInt("amount", this.amount);
        }

        return tag;
    }

}
