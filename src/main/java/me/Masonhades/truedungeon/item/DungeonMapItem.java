package me.Masonhades.truedungeon.item;

import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DungeonMapItem extends Item {
    public DungeonMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!stack.hasTag()) {
            tooltip.add(Component.translatable("tooltip.truedungeon.empty_map"));
            return;
        }

        CompoundTag tag = stack.getTag();
        assert tag != null;

        // Данные квеста
        CompoundTag dungeonData = tag.getCompound("DungeonData");

        // Тип подземелья
        if (dungeonData.contains("dungeon_type")) {
            tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_type")
                    .append(": ").append(Component.literal(dungeonData.getString("dungeon_type"))));
        }

        // Уровень подземелья
        if (dungeonData.contains("dungeon_level")) {
            int dungeonLevel = dungeonData.getInt("dungeon_level");
            dungeonLevel = Math.max(1, Math.min(5, dungeonLevel));

            ChatFormatting levelColor = switch (dungeonLevel) {
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

        // Краткий тултип без Shift
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.truedungeon.hold_shift").withStyle(ChatFormatting.DARK_AQUA));
            return;
        }

        // Цель подземелья
        if (dungeonData.contains("dungeon_objective")) {
            tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_objective")
                    .append(": ").append(Component.literal(dungeonData.getString("dungeon_objective"))));
        }

        // Награды
        if (dungeonData.contains("dungeon_rewards", Tag.TAG_LIST)) {
            ListTag rewards = dungeonData.getList("dungeon_rewards", Tag.TAG_COMPOUND);
            tooltip.add(Component.literal("Rewards:").withStyle(ChatFormatting.GOLD));

            for (Tag rewardTagRaw : rewards) {
                CompoundTag rewardTag = (CompoundTag) rewardTagRaw;
                String itemId = rewardTag.getString("id");
                int count = rewardTag.getInt("count");

                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                if (item != null) {
                    ItemStack displayStack = new ItemStack(item, count);
                    MutableComponent name = (MutableComponent) displayStack.getHoverName();

                    tooltip.add(Component.literal(" - ")
                            .append(Component.literal(count + "x ").withStyle(ChatFormatting.GRAY))
                            .append(name));
                } else {
                    tooltip.add(Component.literal(" - " + count + "x " + itemId).withStyle(ChatFormatting.RED));
                }
            }
        }

        // Состояние
        if (dungeonData.contains("dungeon_state")) {
            String state = dungeonData.getString("dungeon_state");
            ChatFormatting color = switch (state) {
                case "sealed" -> ChatFormatting.GRAY;
                case "active" -> ChatFormatting.YELLOW;
                case "complete" -> ChatFormatting.DARK_GREEN;
                default -> ChatFormatting.WHITE;
            };

            tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_state")
                    .append(": ")
                    .append(Component.translatable("tooltip.truedungeon.dungeon_state." + state).withStyle(color)));
        }

        // Пати лидер
        if (dungeonData.contains("leader_name")) {
            tooltip.add(Component.translatable("tooltip.truedungeon.party_leader")
                    .append(": ").append(Component.literal(dungeonData.getString("leader_name")).withStyle(ChatFormatting.GOLD)));
        }

        // Участники пати
        if (dungeonData.contains("party_names", Tag.TAG_LIST)) {
            ListTag names = dungeonData.getList("party_names", Tag.TAG_STRING);
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
        CompoundTag dungeonData = tag.getCompound("DungeonData");

        if (!dungeonData.contains("dungeon_state") || !dungeonData.getString("dungeon_state").equalsIgnoreCase("sealed")) {
            return InteractionResultHolder.pass(stack);
        }

        dungeonData.putString("dungeon_state", "active");
        dungeonData.putUUID("party_leader", player.getUUID());
        dungeonData.putString("leader_name", player.getName().getString());

        ListTag party = new ListTag();
        party.add(StringTag.valueOf(player.getUUID().toString()));
        dungeonData.put("party", party);

        tag.put("DungeonData", dungeonData);

        if (!level.isClientSide) {
            player.displayClientMessage(
                    Component.translatable("message.truedungeon.quest_activated").withStyle(ChatFormatting.BLUE), true);
            player.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1.0f, 1.5f);
        }

        return InteractionResultHolder.success(stack);
    }


    public static boolean tryAddPlayerToParty(ItemStack map, ServerPlayer player) {
        if (!(map.getItem() instanceof DungeonMapItem) || !map.hasTag())
            return false;
        CompoundTag tag = map.getTag();
        if (tag == null || !tag.contains("DungeonData"))
            return false;

        CompoundTag dungeonData = tag.getCompound("DungeonData");

        if (!"active".equalsIgnoreCase(dungeonData.getString("dungeon_state")))
            return false;

        UUID leaderId = dungeonData.getUUID("party_leader");


        ServerPlayer leader = player.server.getPlayerList().getPlayer(leaderId);
        if (leader == null && !player.getUUID().equals(leaderId)) {
            return false;
        }


        ListTag party = dungeonData.getList("party", Tag.TAG_STRING);
        UUID playerId = player.getUUID();

        boolean alreadyInParty = party.stream()
                .anyMatch(nbt -> UUID.fromString(nbt.getAsString()).equals(playerId));
        if (alreadyInParty)
            return false;


        party.add(StringTag.valueOf(playerId.toString()));
        dungeonData.put("party", party);


        ListTag names = dungeonData.contains("party_names", Tag.TAG_STRING)
                ? dungeonData.getList("party_names", Tag.TAG_STRING)
                : new ListTag();

        String playerName = player.getName().getString();
        boolean nameExists = names.stream()
                .anyMatch(nbt -> nbt.getAsString().equals(playerName));

        if (!nameExists) {
            names.add(StringTag.valueOf(playerName));
        }
        dungeonData.put("party_names", names);
        tag.put("DungeonData", dungeonData);

        return true;
    }
}

