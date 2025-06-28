package me.Masonhades.truedungeon.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DungeonMapItem extends Item {
    public DungeonMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!stack.hasTag()){
            tooltip.add(Component.translatable("tooltip.truedungeon.empty_map"));
            return;
        }

        CompoundTag tag = stack.getTag();
        assert tag != null;

            // Тип подземелья
            if (tag.contains("dungeon_type")) {
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_type")
                        .append(": ").append(Component.literal(tag.getString("dungeon_type"))));
            }
            // Уровень подземелья
            if (tag.contains("dungeon_level")) {
                int dungeonLevel = tag.getInt("dungeon_level");
                dungeonLevel = Math.max(1, Math.min(5, dungeonLevel));

                var levelColor = switch (dungeonLevel) {
                    case 1 -> ChatFormatting.GREEN;
                    case 2 -> ChatFormatting.DARK_GREEN;
                    case 3 -> ChatFormatting.GOLD;
                    case 4 -> ChatFormatting.RED;
                    case 5 -> ChatFormatting.DARK_RED;
                    default -> ChatFormatting.GRAY;
                };

                String stars = "★".repeat(dungeonLevel);
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_level")
                        .append(": ").append(Component.literal(stars).withStyle(levelColor)));
            }
            //Краткая информация
            if (!Screen.hasShiftDown()){
                tooltip.add(Component.translatable("tooltip.truedungeon.hold_shift")
                        .withStyle(ChatFormatting.DARK_AQUA));
                return;
            }

            // Цель задания
            if (tag.contains("dungeon_objective")){
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_objective")
                        .append(": ").append(Component.literal((tag.getString("dungeon_objective")))));

            }
            // Награда за выполнение
            if (tag.contains("dungeon_reward")){
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_reward")
                        .append(": ").append(Component.literal((tag.getString("dungeon_reward")))));
            }
            //Состояние карты
            if (tag.contains("dungeon_state")){
                String state = tag.getString("dungeon_state");
                ChatFormatting color = switch (state){
                    case "sealed" -> ChatFormatting.GRAY;
                    case "active" -> ChatFormatting.YELLOW;
                    case "complete" -> ChatFormatting.DARK_GREEN;
                    default -> ChatFormatting.WHITE;
                };
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_state")
                        .append(": ")
                        .append(Component.translatable("tooltip.truedungeon.dungeon_state." + state.toLowerCase()).withStyle(color)));
            }
             // Лидер
            if (tag.contains("leader_name")) {
                tooltip.add(Component.translatable("tooltip.truedungeon.party_leader")
                    .append(": ").append(Component.literal(tag.getString("leader_name")).withStyle(ChatFormatting.GOLD)));
             }
            //Пати
             if (tag.contains("party_names")) {
                ListTag names = tag.getList("party_names", Tag.TAG_STRING);
                    if (!names.isEmpty()) {
                    tooltip.add(Component.translatable("tooltip.truedungeon.party_members").withStyle(ChatFormatting.GRAY));
                    for (Tag nameTag : names) {
                    tooltip.add(Component.literal("- " + nameTag.getAsString()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
    }
    public static void upgradePartyList(ItemStack stack, List<String> names){
        ListTag tagList = new ListTag();
        for (String name : names){
            tagList.add(StringTag.valueOf(name));
        }
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("dungeon_party", tagList);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains("dungeon_state") || !tag.getString("dungeon_state").equalsIgnoreCase("sealed")) {
            return InteractionResultHolder.pass(stack);
        }

        tag.putString("dungeon_state", "active");

        tag.putUUID("party_leader", player.getUUID());

        tag.putString("leader_name", player.getName().getString());

        ListTag party = new ListTag();
        party.add(StringTag.valueOf(player.getUUID().toString()));
        tag.put("party", party);

        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("message.truedungeon.quest_activated").withStyle(ChatFormatting.BLUE), true);
            player.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0f, 1.5f);
        }

        return InteractionResultHolder.success(stack);
    }


    public static boolean tryAddPlayerToParty(ItemStack map, ServerPlayer player){
        if (!(map.getItem() instanceof  DungeonMapItem) || !map.hasTag()) return false;

        var tag = map.getTag();
        if (tag == null) return false;

        if (!"active".equalsIgnoreCase(tag.getString("dungeon_state"))) return false;

        if (!tag.getUUID("party_leader").equals(player.server.getPlayerList().getPlayer(tag.getUUID("party_leader")).getUUID()))
            return false;

        ListTag party = tag.getList("party", Tag.TAG_STRING);

        UUID playerId = player.getUUID();
        boolean alreadyInParty = party.stream().anyMatch(nbt -> UUID.fromString(nbt.getAsString()).equals(playerId));
        if (alreadyInParty) return false;

        party.add(StringTag.valueOf(playerId.toString()));
        tag.put("party", party);

        ListTag names = tag.contains("party_names") ? tag.getList("party_names", Tag.TAG_STRING) : new ListTag();
        String playerName = player.getName().getString();
        boolean nameExists = names.stream().anyMatch(nbt -> nbt.getAsString().equals(playerName));
        if (!nameExists) {
            names.add(StringTag.valueOf(playerName));
        }
        tag.put("party_names", names);

        return true;
    }
}

