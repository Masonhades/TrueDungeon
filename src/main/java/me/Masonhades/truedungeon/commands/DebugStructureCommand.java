package me.Masonhades.truedungeon.commands;



import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;


@Mod.EventBusSubscriber(modid = Truedungeon.MODID)
public class DebugStructureCommand {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("truedungeon")
                .then(Commands.literal("place_room")
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    MinecraftServer server = ctx.getSource().getServer();
                                    return SharedSuggestionProvider.suggest(
                                            server.getStructureManager().listTemplates()
                                                    .sorted()
                                                    .filter(id -> id.getNamespace().equals("truedungeon"))
                                                    .map(ResourceLocation::getPath),
                                            builder
                                    );
                                })
                                .then(Commands.argument("rotation", IntegerArgumentType.integer(0, 270))
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(List.of("0", "90", "180", "270"), builder))
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            ServerLevel level = player.serverLevel();

                                            String idRaw = StringArgumentType.getString(ctx, "id");
                                            int rotationInt = IntegerArgumentType.getInteger(ctx, "rotation");

                                            Rotation rotation = switch (rotationInt) {
                                                case 0 -> Rotation.NONE;
                                                case 90 -> Rotation.CLOCKWISE_90;
                                                case 180 -> Rotation.CLOCKWISE_180;
                                                case 270 -> Rotation.COUNTERCLOCKWISE_90;
                                                default -> throw new IllegalArgumentException("Неверный угол поворота.");
                                            };

                                            ResourceLocation id = new ResourceLocation("truedungeon", idRaw);
                                            StructureTemplate template = level.getServer().getStructureManager().getOrCreate(id);


                                            ChunkPos targetChunk = new ChunkPos(player.blockPosition());
                                            BlockPos chunkOrigin = targetChunk.getWorldPosition().atY(player.getBlockY());
                                            Vec3i rawSize = template.getSize();
                                            int sizeX = rawSize.getX();
                                            int sizeZ = rawSize.getZ();

                                            int offsetX = switch (rotation) {
                                                case CLOCKWISE_90 -> sizeZ - 1;
                                                case CLOCKWISE_180 -> sizeX - 1;
                                                case COUNTERCLOCKWISE_90 -> 0;
                                                default -> 0;
                                            };

                                            int offsetZ = switch (rotation) {
                                                case CLOCKWISE_180 -> sizeZ - 1;
                                                case COUNTERCLOCKWISE_90 -> sizeX - 1;
                                                default -> 0;
                                            };

                                            BlockPos placementPos = chunkOrigin.offset(offsetX, 0, offsetZ);

                                            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
                                            template.placeInWorld(level, placementPos, placementPos, settings, level.getRandom(), 2);

                                            ctx.getSource().sendSuccess(() ->
                                                    Component.literal("✅ Структура " + id + " размещена в " + placementPos + " с поворотом " + rotationInt + "°"), false
                                            );

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}

