package me.Masonhades.truedungeon.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.*;


public class DungeonGraphBuilder {
    private final Random random;
    private final List<RoomDefinition> availableRooms;
    private final Set<ChunkPos> occupiedChunks = new HashSet<>();
    private DungeonNode root;
    private final StructureTemplateManager templateManager;
    private final int dungeonLevel;

    private final int baseDepth = 3;
    private int currentMaxDepth;
    private List<String> allowedTypes;

    public DungeonGraphBuilder(Random random, List<RoomDefinition> rooms, StructureTemplateManager templateManager, int dungeonLevel) {
        this.random = random;
        this.availableRooms = rooms;
        this.templateManager = templateManager;
        this.dungeonLevel = dungeonLevel;
    }

    public DungeonNode getRoot() {
        return root;
    }

    public void generate(ChunkPos startChunk, BlockPos placementPos, int level, List<String> allowedTypes) {
        this.allowedTypes = allowedTypes;
        this.currentMaxDepth = baseDepth + level * 2;

        // ⬆️ Масштабируем weight и maxUsages в зависимости от уровня
        for (RoomDefinition room : availableRooms) {
            if (room.isLevelScaling()) {
                int scaledWeight = Math.max(1, room.getWeight() + dungeonLevel); // или другой рост
                int scaledMaxUsages = Math.max(1, room.getMaxUsages() + dungeonLevel);
                room.setWeight(scaledWeight);
                room.setMaxUsages(scaledMaxUsages);
            }
            room.resetUsages(); // всегда сбрасываем перед генерацией
        }

        // Находим стартовую комнату
        RoomDefinition startRoom = availableRooms.stream()
                .filter(r -> "start_room".equals(r.getStructureId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("❌ Стартовая комната не найдена"));

        BlockPos worldPos = new BlockPos(
                startChunk.getWorldPosition().getX(),
                placementPos.getY(),
                startChunk.getWorldPosition().getZ()
        );

        root = new DungeonNode(
                startRoom.getStructureId(),
                Rotation.NONE,
                startChunk,
                worldPos,
                startRoom.getRoutes(),
                null
        );

        occupiedChunks.add(startChunk);
        System.out.println("✅ Стартовая позиция: " + worldPos);
        System.out.println("📍 Чанк: " + startChunk);

        buildRecursive(root, 0);
    }

    private void buildRecursive(DungeonNode currentNode, int depth) {
        if (depth >= currentMaxDepth) {
            System.out.println("⛔ Достигнута максимальная глубина: " + depth);
            return;
        }

        List<Route> shuffledRoutes = new ArrayList<>(currentNode.getRoutes());
        Collections.shuffle(shuffledRoutes, random);

        for (Route route : shuffledRoutes) {
            Direction exit = route.getExit();
            if (exit == null) continue;

            ChunkPos nextChunk = getNeighborChunk(currentNode.getPosition(), exit);
            if (occupiedChunks.contains(nextChunk)) continue;

            List<RoomDefinition> candidates = findCompatibleRooms(exit);
            if (candidates.isEmpty()) continue;

            RoomDefinition selected = selectWeightedRandom(candidates);
            Rotation requiredRotation = calculateRotation(exit, selected);

            BlockPos childWorldPos = new BlockPos(
                    nextChunk.getWorldPosition().getX(),
                    currentNode.getWorldPos().getY(),
                    nextChunk.getWorldPosition().getZ()
            );

            DungeonNode child = new DungeonNode(
                    selected.getStructureId(),
                    requiredRotation,
                    nextChunk,
                    childWorldPos,
                    selected.getRoutes(),
                    currentNode
            );

            occupiedChunks.add(nextChunk);
            selected.incrementUsages();
            currentNode.addChild(child);

            buildRecursive(child, depth + 1);
        }
    }

    private ChunkPos getNeighborChunk(ChunkPos current, Direction direction) {
        int x = current.x;
        int z = current.z;
        return switch (direction) {
            case NORTH -> new ChunkPos(x, z - 1);
            case SOUTH -> new ChunkPos(x, z + 1);
            case EAST -> new ChunkPos(x + 1, z);
            case WEST -> new ChunkPos(x - 1, z);
            default -> current;
        };
    }

    private List<RoomDefinition> findCompatibleRooms(Direction exit) {
        Direction requiredEntrance = exit.opposite();
        return availableRooms.stream()
                .filter(room -> room.getRoutes().stream()
                        .anyMatch(route -> route.getEntrance() == requiredEntrance))
                .filter(room -> room.getMaxUsages() == -1 || room.getCurrentUsages() < room.getMaxUsages())
                .filter(room -> room.getTypes().stream().anyMatch(allowedTypes::contains))
                .toList();
    }

    private Rotation calculateRotation(Direction exit, RoomDefinition room) {
        Direction requiredEntrance = exit.opposite();
        Optional<Route> matchedRoute = room.getRoutes().stream()
                .filter(route -> route.getEntrance() == requiredEntrance)
                .findFirst();

        if (matchedRoute.isEmpty()) return Rotation.NONE;

        Direction roomExit = matchedRoute.get().getExit();
        return getRotationToMatch(roomExit, exit);
    }

    private Rotation getRotationToMatch(Direction expectedEntrance, Direction roomEntrance) {
        for (RoomRotation rotation : RoomRotation.values()) {
            if (roomEntrance.rotate(rotation) == expectedEntrance) {
                return rotation.getMcRotation();
            }
        }
        return Rotation.NONE;
    }

    private RoomDefinition selectWeightedRandom(List<RoomDefinition> candidates) {
        int totalWeight = candidates.stream().mapToInt(RoomDefinition::getWeight).sum();
        int randomValue = random.nextInt(totalWeight);
        for (RoomDefinition room : candidates) {
            if (randomValue < room.getWeight()) return room;
            randomValue -= room.getWeight();
        }
        return candidates.get(0);
    }
}

