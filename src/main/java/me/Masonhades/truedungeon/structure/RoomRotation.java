package me.Masonhades.truedungeon.structure;

import net.minecraft.world.level.block.Rotation;

public enum RoomRotation {
    NONE(Rotation.NONE),
    CLOCKWISE_90(Rotation.CLOCKWISE_90),
    CLOCKWISE_180(Rotation.CLOCKWISE_180),
    COUNTERCLOCKWISE_90(Rotation.COUNTERCLOCKWISE_90);

    private final Rotation mcRotation;

    RoomRotation(Rotation mcRotation) {
        this.mcRotation = mcRotation;
    }

    public Rotation getMcRotation() {
        return mcRotation;
    }

    public static RoomRotation fromMcRotation(Rotation rotation) {
        for (RoomRotation r : values()) {
            if (r.mcRotation == rotation) return r;
        }
        return NONE;
    }

    public static RoomRotation fromSteps(int steps) {
        return values()[steps % values().length];
    }
}
