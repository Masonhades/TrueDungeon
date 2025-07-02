package me.Masonhades.truedungeon.event;

import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import me.Masonhades.truedungeon.item.DungeonMapItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Truedungeon.MODID)
public class QuestRewardHandler {

    @SubscribeEvent
    public static void onPlayerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (event.getLevel().isClientSide()) return;
        if (!player.isShiftKeyDown()) return;
        if (!(event.getTarget() instanceof QuestDealerEntity)) return;

        ItemStack stack = player.getItemInHand(event.getHand());
        if (!(stack.getItem() instanceof DungeonMapItem) || !stack.hasTag()) return;

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("DungeonData")) return;

        CompoundTag dungeonData = tag.getCompound("DungeonData");
        if (!"complete".equalsIgnoreCase(dungeonData.getString("dungeon_state"))) return;

        Truedungeon.LOGGER.info("Выдача наград за завершённый квест игроку: {}", player.getName().getString());

        if (dungeonData.contains("dungeon_rewards", Tag.TAG_LIST)) {
            ListTag rewards = dungeonData.getList("dungeon_rewards", Tag.TAG_COMPOUND);

            for (Tag rewardTagRaw : rewards) {
                CompoundTag rewardTag = (CompoundTag) rewardTagRaw;
                String itemId = rewardTag.getString("id");
                int count = rewardTag.getInt("count");

                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                if (item != null) {
                    ItemStack rewardStack = new ItemStack(item, count);
                    if (!player.getInventory().add(rewardStack)) {
                        player.drop(rewardStack, false);
                    }
                }
            }
        }

        player.sendSystemMessage(Component.translatable("message.truedungeon.reward_received").withStyle(ChatFormatting.GREEN));
        player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0f, 1.2f);
        stack.shrink(1);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
