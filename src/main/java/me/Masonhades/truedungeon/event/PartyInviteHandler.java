package me.Masonhades.truedungeon.event;


import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.item.DungeonMapItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Truedungeon.MODID)
public class PartyInviteHandler {

    @SubscribeEvent
    public static void onPlayerRightCLickPlayer(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getTarget() instanceof Player targetPlayer)) return;

        Player sourcePlayer = event.getEntity();
        ItemStack item = event.getItemStack();

        if (!(item.getItem() instanceof DungeonMapItem) || !item.hasTag()) return;

        CompoundTag tag = item.getTag();
        if (tag == null || !tag.contains("DungeonData", Tag.TAG_COMPOUND)) return;

        CompoundTag dungeonData = tag.getCompound("DungeonData");

        if (!"active".equalsIgnoreCase(dungeonData.getString("dungeon_state"))) return;

        if (!dungeonData.hasUUID("party_leader")) {
            sourcePlayer.sendSystemMessage(Component.translatable("party_message.truedungeon.no_party_leader"));
            return;
        }

        UUID leaderId = dungeonData.getUUID("party_leader");
        if (!leaderId.equals(sourcePlayer.getUUID())) return;

        ListTag party = dungeonData.getList("party", Tag.TAG_STRING);
        if (party.stream().anyMatch(nbt -> UUID.fromString(nbt.getAsString()).equals(targetPlayer.getUUID()))) {
            sourcePlayer.sendSystemMessage(Component.translatable("party_message.truedungeon.already_in_party_with_name", targetPlayer.getName()));
            return;
        }

        ListTag rewards = dungeonData.getList("dungeon_rewards", Tag.TAG_COMPOUND);
        MutableComponent rewardsText = Component.literal("Rewards:").withStyle(ChatFormatting.GOLD);

        for (Tag rewardTagRaw : rewards) {
            CompoundTag rewardTag = (CompoundTag) rewardTagRaw;
            String itemId = rewardTag.getString("id");
            int count = rewardTag.getInt("count");

            Item item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
            if (item1 != null) {
                ItemStack rewardStack = new ItemStack(item1, count);
                Component rewardComponent = rewardStack.getDisplayName()
                        .copy()
                        .withStyle(style -> style
                                .withColor(ChatFormatting.AQUA)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(rewardStack)))
                        );
                rewardsText.append(Component.literal(" + ").append(rewardComponent));
            }
        }
        targetPlayer.sendSystemMessage(rewardsText);


        MutableComponent msg = Component.translatable("party_message.truedungeon.invite_in_party",
                sourcePlayer.getName(),
                Component.literal(dungeonData.getString("dungeon_type")),
                Component.translatable("tooltip.truedungeon.dungeon_level_name", dungeonData.getInt("dungeon_level")),
                Component.literal("...")
        );

        MutableComponent accept = Component.translatable("party_message.truedungeon.accept").withStyle(style ->
                style.withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/truedungeon accept " + sourcePlayer.getUUID()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("party_message.truedungeon.join_the_party")))
        );

        MutableComponent decline = Component.translatable("party_message.truedungeon.decline").withStyle(style ->
                style.withColor(ChatFormatting.RED)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/truedungeon decline " + sourcePlayer.getUUID()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("party_message.truedungeon.decline_party_invite")))
        );

        targetPlayer.sendSystemMessage(msg);
        targetPlayer.sendSystemMessage(Component.empty().append(accept).append(" ").append(decline));

        sourcePlayer.sendSystemMessage(Component.translatable(
                "party_message.truedungeon.party_invite",
                Component.literal(targetPlayer.getName().getString())
        ));
    }
}

