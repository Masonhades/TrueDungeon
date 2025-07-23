package me.Masonhades.truedungeon.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;

import java.util.*;
import java.util.List;

public class DungeonNode {
    private final String structureId;
    private final Rotation rotation;
    private final ChunkPos position;
    private final BlockPos worldPos;
    private final List<Route> routes;
    private final DungeonNode parent;
    private final List<DungeonNode> children = new ArrayList<>();
    private int widthChunks = 1;
    private int heightChunks = 1;

    public DungeonNode(String structureId, Rotation rotation, ChunkPos position, BlockPos worldPos, List<Route> routes, DungeonNode parent) {
        this.structureId = structureId;
        this.rotation = rotation;
        this.position = position;
        this.worldPos = worldPos;
        this.routes = new ArrayList<>(routes);
        this.parent = parent;
    }



    public String getStructureId() {
        return structureId;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public ChunkPos getPosition() {
        return position;
    }

    public BlockPos getWorldPos() {
        return worldPos;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public DungeonNode getParent() {
        return parent;
    }

    public List<DungeonNode> getChildren() {
        return children;
    }

    public void addChild(DungeonNode child) {
        children.add(child);
    }
    public int getWidthChunks() {
        return widthChunks;
    }

    public void setWidthChunks(int widthChunks) {
        this.widthChunks = widthChunks;
    }

    public int getHeightChunks() {
        return heightChunks;
    }

    public void setHeightChunks(int heightChunks) {
        this.heightChunks = heightChunks;
    }
}