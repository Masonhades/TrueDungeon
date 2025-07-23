package me.Masonhades.truedungeon.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    private final float baseBranchingProbability = 0.7f;
    private int maxSideBranchDepth;

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
        this.maxSideBranchDepth = baseDepth + level;

        for (RoomDefinition room : availableRooms) {
            if (room.isLevelScaling()) {
                int scaledWeight = Math.max(1, room.getWeight() + dungeonLevel);
                int scaledMaxUsages = Math.max(1, room.getMaxUsages() + dungeonLevel);
                room.setWeight(scaledWeight);
                room.setMaxUsages(scaledMaxUsages);
            }
            room.resetUsages();
        }

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
        root.setWidthChunks(startRoom.getWidthChunks());
        root.setHeightChunks(startRoom.getHeightChunks());

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

        float currentBranchingProbability = baseBranchingProbability * (1 - depth / (float) currentMaxDepth);

        for (Route route : shuffledRoutes) {
            if (random.nextFloat() > currentBranchingProbability) {
                System.out.println("🛑 Создан тупик на маршруте " + route);
                continue;
            }

            System.out.println("🔍 Пытаемся расшириться по маршруту: " + route + " из комнаты " + currentNode.getStructureId());

            int targetChunkX = currentNode.getPosition().x + route.getExitChunkX();
            int targetChunkZ = currentNode.getPosition().z + route.getExitChunkZ();
            ChunkPos nextChunk = new ChunkPos(targetChunkX, targetChunkZ);

            System.out.printf("📍 Целевой чанк для подключения: (%d, %d)%n", nextChunk.x, nextChunk.z);

            List<RoomDefinition> candidates = findCompatibleRooms(route.getExit());
            if (candidates.isEmpty()) {
                System.out.println("🟡 Нет подходящих комнат для выхода: " + route.getExit());
                continue;
            }

            List<RoomDefinition> fittingCandidates = new ArrayList<>();
            for (RoomDefinition candidate : candidates) {
                if (canRoomFit(nextChunk, candidate)) {
                    fittingCandidates.add(candidate);
                }
            }

            if (fittingCandidates.isEmpty()) {
                System.out.println("🔴 Нет комнат, помещающихся в доступное пространство");
                continue;
            }

            RoomDefinition selected = selectWeightedRandom(fittingCandidates, depth);
            System.out.printf("✅ Выбрана комната: %s (размер: %dx%d)%n",
                    selected.getStructureId(),
                    selected.getWidthChunks(),
                    selected.getHeightChunks());

            Direction exit = route.getExit();
            Rotation requiredRotation = calculateRotation(exit, selected);

            BlockPos childWorldPos = new BlockPos(
                    nextChunk.getWorldPosition().getX(),
                    currentNode.getWorldPos().getY(),
                    nextChunk.getWorldPosition().getZ()
            );

            System.out.printf("🎯 Размещаем %s в чанке (%d, %d) → мировая позиция: %s%n",
                    selected.getStructureId(), nextChunk.x, nextChunk.z, childWorldPos);

            DungeonNode child = new DungeonNode(
                    selected.getStructureId(),
                    requiredRotation,
                    nextChunk,
                    childWorldPos,
                    selected.getRoutes(),
                    currentNode
            );
            child.setWidthChunks(selected.getWidthChunks());
            child.setHeightChunks(selected.getHeightChunks());

            for (int dx = 0; dx < selected.getWidthChunks(); dx++) {
                for (int dz = 0; dz < selected.getHeightChunks(); dz++) {
                    ChunkPos occupied = new ChunkPos(nextChunk.x + dx, nextChunk.z + dz);
                    occupiedChunks.add(occupied);
                    System.out.printf("🔒 Заняли чанк: (%d, %d)%n", occupied.x, occupied.z);
                }
            }

            selected.incrementUsages();
            currentNode.addChild(child);

            System.out.printf("🟢 Добавлен узел: %s → %s (глубина %d)%n",
                    currentNode.getStructureId(), selected.getStructureId(), depth + 1);

            buildRecursive(child, depth + 1);
        }
    }

    private boolean canRoomFit(ChunkPos origin, RoomDefinition room) {
        int width = room.getWidthChunks();
        int height = room.getHeightChunks();

        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < height; dz++) {
                ChunkPos checkPos = new ChunkPos(origin.x + dx, origin.z + dz);
                if (occupiedChunks.contains(checkPos)) {
                    System.out.printf("❌ Чанк (%d, %d) занят → комната %s не помещается%n",
                            checkPos.x, checkPos.z, room.getStructureId());
                    return false;
                }
            }
        }
        return true;
    }

    private List<RoomDefinition> findCompatibleRooms(Direction exit) {
        if (exit == null) {
            System.out.println("🟡 Нет выхода — это dead end");
            return Collections.emptyList();
        }

        Direction requiredEntrance = exit.getOpposite();
        List<RoomDefinition> candidates = availableRooms.stream()
                .filter(room -> room.getRoutes().stream()
                        .anyMatch(route -> route.getEntrance() == requiredEntrance))
                .filter(room -> room.getMaxUsages() == -1 || room.getCurrentUsages() < room.getMaxUsages())
                .filter(room -> room.getTypes().stream().anyMatch(allowedTypes::contains))
                .toList();

        System.out.println("🔎 Кандидаты для выхода " + exit + ": " + candidates.size());
        return candidates;
    }

    private Rotation calculateRotation(Direction exit, RoomDefinition room) {
        Direction requiredEntrance = exit.getOpposite();
        Optional<Route> matchedRoute = room.getRoutes().stream()
                .filter(route -> route.getEntrance() == requiredEntrance)
                .findFirst();

        if (matchedRoute.isEmpty()) return Rotation.NONE;

        Direction roomExit = matchedRoute.get().getExit();
        return getRotationToMatch(roomExit, exit);
    }

    private Rotation getRotationToMatch(Direction expectedEntrance, Direction roomEntrance) {
        for (Rotation rotation : Rotation.values()) {
            if (rotation.rotate(roomEntrance) == expectedEntrance) {
                return rotation;
            }
        }
        return Rotation.NONE;
    }

    private RoomDefinition selectWeightedRandom(List<RoomDefinition> candidates, int depth) {
        int totalWeight = 0;
        List<Integer> weights = new ArrayList<>();

        for (RoomDefinition room : candidates) {
            int weight = room.getWeight();
            if (depth > maxSideBranchDepth / 2) {
                int sizePenalty = room.getWidthChunks() * room.getHeightChunks();
                weight = Math.max(1, weight - sizePenalty);
            }
            weights.add(weight);
            totalWeight += weight;
        }

        int randomValue = random.nextInt(totalWeight);
        for (int i = 0; i < candidates.size(); i++) {
            randomValue -= weights.get(i);
            if (randomValue < 0) {
                return candidates.get(i);
            }
        }
        return candidates.get(0);
    }
}