package me.Masonhades.truedungeon.structure;

import net.minecraft.core.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class RoomDefinition {
    private final String structureId;
    private int weight;
    private int maxUsages;
    private final List<String> types;
    private final List<Route> routes;
    private boolean levelScaling = true;

    private int currentUsages;

    private int widthChunks = 1;
    private int heightChunks =1;

    public RoomDefinition(String id, int weight, int maxUsages, List<String> types, List<Route> routes, boolean levelScaling) {
        System.out.println("🔄 Создаем RoomDefinition: " + id);

        this.structureId = id;
        this.weight = weight;
        this.maxUsages = maxUsages;
        this.currentUsages = 0;
        this.types = new ArrayList<>(types);
        this.routes = new ArrayList<>(routes);
        this.levelScaling = levelScaling;
    }

    public String getStructureId() { return structureId; }
    public int getWeight() { return weight; }
    public int getMaxUsages() { return maxUsages; }
    public int getCurrentUsages() { return currentUsages; }
    public List<String> getTypes() { return types; }
    public List<Route> getRoutes() { return routes; }

    public void incrementUsages() {
        if (currentUsages >= maxUsages) {
            throw new IllegalStateException("Комната " + structureId + " достигла лимита: " + maxUsages + " использований");
        }
        currentUsages++;
    }

    public List<Direction> getEntrances() {
        return routes.stream()
                .map(Route::getEntrance)
                .distinct()
                .toList();
    }

    public Optional<Direction> getExitForEntrance(Direction entrance) {
        return routes.stream()
                .filter(route -> route.getEntrance() == entrance)
                .map(Route::getExit)
                .findFirst();
    }
    public void setMaxUsages(int maxUsages) {
        this.maxUsages = maxUsages;
    }
    public void setWeight(int weight){
        this.weight = weight;
    }
    public boolean isLevelScaling() {
        return levelScaling;
    }

    public void resetUsages() {
        this.currentUsages = 0;
    }

    public int getHeightChunks() {
        return heightChunks;
    }

    public int getWidthChunks() {
        return widthChunks;
    }

    public void setCurrentUsages(int currentUsages) {
        this.currentUsages = currentUsages;
    }

    public void setHeightChunks(int heightChunks) {
        this.heightChunks = heightChunks;
    }
    public void setWidthChunks(int widthChunks) {
        this.widthChunks = widthChunks;
    }

    @Override
    public String toString() {
        return "RoomDefinition{" +
                "id='" + structureId + '\'' +
                ", weight=" + weight +
                ", maxUsages=" + maxUsages +
                ", routes=" + routes +
                '}';
    }
}