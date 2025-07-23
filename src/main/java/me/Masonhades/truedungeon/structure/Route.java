package me.Masonhades.truedungeon.structure;

import net.minecraft.core.Direction;

public class Route {
    private final Direction entrance;
    private final Direction exit;

    private final int entranceChunkX;
    private final int entranceChunkZ;

    private final int exitChunkX;
    private final int exitChunkZ;

    public Route(Direction entrance, Direction exit, int entranceChunkX, int entranceChunkZ, int exitChunkX, int exitChunkZ) {
        this.entrance = entrance;
        this.exit = exit;
        this.entranceChunkX = entranceChunkX;
        this.entranceChunkZ = entranceChunkZ;
        this.exitChunkX = exitChunkX;
        this.exitChunkZ = exitChunkZ;
    }

    public Direction getEntrance() {
        return entrance;
    }

    public Direction getExit() {
        return exit;
    }

    public int getEntranceChunkX() {
        return entranceChunkX;
    }

    public int getEntranceChunkZ() {
        return entranceChunkZ;
    }

    public int getExitChunkX() {
        return exitChunkX;
    }

    public int getExitChunkZ() {
        return exitChunkZ;
    }

    @Override
    public String toString() {
        return entrance + " → " + exit;
    }
}