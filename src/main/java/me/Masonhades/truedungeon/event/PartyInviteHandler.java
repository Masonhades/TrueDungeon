package me.Masonhades.truedungeon.event;


import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.item.DungeonMapItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Truedungeon.MODID)
public class PartyInviteHandler {

    @SubscribeEvent
    public static void onPlayerRightCLickPlayer(PlayerInteractEvent.EntityInteract event){

        if (event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getTarget() instanceof Player targetPlayer)) return;

        Player sourcePlayer = event.getEntity();
        ItemStack item = event.getItemStack();

        if (item.getItem() instanceof DungeonMapItem map && item.hasTag()){
            var tag = item.getTag();
            if (tag != null && "active".equalsIgnoreCase(tag.getString("dungeon_state"))){

                if (!tag.hasUUID("party_leader")) {
                    sourcePlayer.sendSystemMessage(Component.translatable("party_message.truedungeon.no_party_leader"));
                    return;
                }

                UUID leaderId = tag.getUUID("party_leader");
                if (!leaderId.equals(sourcePlayer.getUUID())) return;


                var party = tag.getList("party", Tag.TAG_STRING);
                if (party.stream().anyMatch(nbt -> UUID.fromString(nbt.getAsString()).equals(targetPlayer.getUUID()))){
                    sourcePlayer.sendSystemMessage(Component.translatable("party_message.truedungeon.already_in_party_with_name", targetPlayer.getName()));
                    return;
                }


                MutableComponent msg = Component.translatable("party_message.truedungeon.invite_in_party",
                        sourcePlayer.getName(),
                        Component.literal(tag.getString("dungeon_type")),
                        Component.translatable("tooltip.truedungeon.dungeon_level_name", tag.getInt("dungeon_level")),
                        Component.translatable("tooltip.truedungeon.dungeon_reward_name", tag.getString("dungeon_reward"))
                );;


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
    }
}
