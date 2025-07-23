package me.Masonhades.truedungeon.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;



public class DungeonGraphPlacer {
    private final StructureTemplateManager templateManager;
    private final ServerLevel level;

    public DungeonGraphPlacer(StructureTemplateManager templateManager, ServerLevel level) {
        this.templateManager = templateManager;
        this.level = level;
    }

    public void place(DungeonNode root) {
        if (root == null) {
            System.err.println("❌ root == null");
            return;
        }

        placeNode(root);
    }

    private void placeNode(DungeonNode node) {
        if (node == null || node.getStructureId() == null || node.getStructureId().isEmpty()) {
            System.err.println("❌ Ошибка: node.structure или её id — null! node: " + node);
            return;
        }

        if (!placeStructure(node)) {
            System.err.println("❌ Не удалось разместить структуру: " + node.getStructureId());
            return;
        }

        for (DungeonNode child : node.getChildren()) {
            placeNode(child);
        }
    }

    private boolean placeStructure(DungeonNode node) {
        String structureId = node.getStructureId();
        Rotation rotation = node.getRotation();

        // 🔁 Размер комнаты в чанках
        int widthChunks = node.getWidthChunks();
        int heightChunks = node.getHeightChunks();

        // ✅ Загружаем структуру
        ResourceLocation location = new ResourceLocation("truedungeon", structureId);
        StructureTemplate template = templateManager.get(location).orElse(null);

        if (template == null) {
            System.err.println("❌ Структура не найдена: " + location);
            return false;
        }

        // ✅ Центр чанка (верхний левый угол комнаты)
        BlockPos chunkOrigin = node.getWorldPos();

        // ✅ Смещение при повороте
        Vec3i size = template.getSize();
        int offsetX = 0;
        int offsetZ = 0;

        // 🔁 Корректируем смещение в зависимости от поворота
        switch (rotation) {
            case CLOCKWISE_90 -> {
                offsetX = (size.getZ() - 1) - 16 * (widthChunks - 1);
            }
            case CLOCKWISE_180 -> {
                offsetX = (size.getX() - 1) - 16 * (widthChunks - 1);
                offsetZ = (size.getZ() - 1) - 16 * (heightChunks - 1);
            }
            case COUNTERCLOCKWISE_90 -> {
                offsetZ = (size.getX() - 1) - 16 * (heightChunks - 1);
            }
            default -> {
                // Rotation.NONE — без смещения
            }
        }

        BlockPos placementPos = chunkOrigin.offset(offsetX, 0, offsetZ);

        // ✅ Настройки размещения
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);

        // ✅ Размещаем структуру
        template.placeInWorld(level, placementPos, placementPos, settings, level.getRandom(), 2);
        System.out.println("✅ Размещена структура: " + structureId + " на позиции: " + placementPos +
                " (" + widthChunks + "×" + heightChunks + " чанков)");

        return true;
    }
}