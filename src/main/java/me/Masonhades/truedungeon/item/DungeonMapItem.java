package me.Masonhades.truedungeon.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
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
                    case "Sealed" -> ChatFormatting.GRAY;
                    case "Active" -> ChatFormatting.YELLOW;
                    case "Complete" -> ChatFormatting.DARK_GREEN;
                    default -> ChatFormatting.WHITE;
                };
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_state")
                        .append(": ").append(Component.literal(state).withStyle(color)));
            }
            //Пати
            if (tag.contains("dungeon_party")){
                tooltip.add(Component.translatable("tooltip.truedungeon.party_members")
                        .withStyle(ChatFormatting.GOLD));
                ListTag members = tag.getList("dungeon_party", ListTag.TAG_STRING);
                for (Tag entry : members){
                    tooltip.add(Component.literal("* " + entry.getAsString()).withStyle(ChatFormatting.BLUE));
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
    public InteractionResultHolder<ItemStack> use (Level level, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();

        if(!tag.contains("dungeon_state") || !tag.getString("dungeon_state").equals("Sealed")){
            return InteractionResultHolder.pass(stack);
        }
        tag.putString("dungeon_state", "Active");
        tag.putString("dungeon_leader", player.getName().getString());
        ListTag party = new ListTag();
        party.add(StringTag.valueOf(player.getName().getString()));
        tag.put("dungeon_party", party);

        if (!level.isClientSide){
            player.displayClientMessage(Component.translatable("message.truedungeon.quest_activated").withStyle(ChatFormatting.BLUE), true);
            player.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0f, 1.5f);
        }
        return InteractionResultHolder.success(stack);
    }

}

