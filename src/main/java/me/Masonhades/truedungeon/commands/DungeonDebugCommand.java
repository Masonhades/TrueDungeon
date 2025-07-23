package me.Masonhades.truedungeon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.Masonhades.truedungeon.DungeonGraphExporter;
import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.structure.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Truedungeon.MODID)
public class DungeonDebugCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("truedungeon")
                .then(Commands.literal("debug_generate")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 5))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ServerLevel level = player.serverLevel();

                                            String dungeonType = StringArgumentType.getString(context, "type");
                                            int dungeonLevel = IntegerArgumentType.getInteger(context, "level");

                                            ChunkPos playerChunk = new ChunkPos(player.blockPosition());
                                            context.getSource().sendSuccess(() -> Component.literal("🔄 Генерация подземелья: тип = " + dungeonType + ", уровень = " + dungeonLevel), true);

                                            try {
                                                InputStream stream = Truedungeon.class.getClassLoader().getResourceAsStream("data/truedungeon/structures/rooms.json");
                                                if (stream == null) {
                                                    context.getSource().sendFailure(Component.literal("❌ Файл rooms.json не найден"));
                                                    return 0;
                                                }

                                                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                                                StringBuilder sb = new StringBuilder();
                                                String line;
                                                while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                                                List<RoomDefinition> allRooms = RoomDefinitionLoader.loadFromJson(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));

                                                List<RoomDefinition> filteredRooms = allRooms.stream()
                                                        .filter(room -> room.getTypes().contains(dungeonType))
                                                        .toList();

                                                if (filteredRooms.isEmpty()) {
                                                    context.getSource().sendFailure(Component.literal("❌ Комнаты не найдены для типа: " + dungeonType));
                                                    return 0;
                                                }

                                                StructureTemplateManager templateManager = level.getStructureManager();
                                                DungeonGraphBuilder builder = new DungeonGraphBuilder(new Random(), filteredRooms, templateManager, dungeonLevel);

                                                BlockPos placementPos = new BlockPos(playerChunk.getWorldPosition().getX(), player.getBlockY() + 10, playerChunk.getWorldPosition().getZ());
                                                builder.generate(playerChunk, placementPos, dungeonLevel, List.of(dungeonType));



                                                DungeonGraphPlacer placer = new DungeonGraphPlacer(templateManager, level);
                                                placer.place(builder.getRoot());
                                                Path dotPath = Paths.get("run", "dungeon.dot");
                                                DungeonGraphExporter.exportToDot(builder.getRoot(), dotPath);

                                                context.getSource().sendSuccess(() -> Component.literal("✅ Подземелье сгенерировано! Файл: dungeon.dot"), false);

                                                context.getSource().sendSuccess(() -> Component.literal("✅ Подземелье сгенерировано!"), false);
                                                return 1;

                                            } catch (Exception e) {
                                                context.getSource().sendFailure(Component.literal("❌ Ошибка генерации: " + e.getMessage()));
                                                e.printStackTrace();
                                                return 0;
                                            }})))));
    }
}