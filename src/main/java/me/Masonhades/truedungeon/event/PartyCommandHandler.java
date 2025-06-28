package me.Masonhades.truedungeon.event;

import com.mojang.brigadier.CommandDispatcher;
import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.item.DungeonMapItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Truedungeon.MODID)
public class PartyCommandHandler {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("truedungeon")
                        .then(Commands.literal("accept")
                                .then(Commands.argument("leader", UuidArgument.uuid())
                                        .executes(ctx -> {
                                            ServerPlayer self = ctx.getSource().getPlayerOrException();
                                            UUID leaderId = UuidArgument.getUuid(ctx, "leader");

                                            return PartyCommandHandler.acceptInvite(self, leaderId);
                                        })
                                )
                        )
                        .then(Commands.literal("decline")
                                .then(Commands.argument("leader", UuidArgument.uuid())
                                        .executes(ctx ->{
                                            ServerPlayer self = ctx.getSource().getPlayerOrException();
                                            UUID leaderId = UuidArgument.getUuid(ctx, "leader");

                                            self.sendSystemMessage(
                                                    Component.translatable("party_message.truedungeon.decline_party_invite")
                                                            .append(" (").append(leaderId.toString()).append(")")
                                            );
                                            return 1;
                                        })
                                )
                        )

        );
    }
    public static int acceptInvite(ServerPlayer self, UUID leaderId) {
        ServerPlayer leader = self.server.getPlayerList().getPlayer(leaderId);
        if (leader != null) {
            for (ItemStack stack : leader.getInventory().items) {
                if (DungeonMapItem.tryAddPlayerToParty(stack, self)) {
                    self.sendSystemMessage(Component.translatable("party_message.truedungeon.join_the_party"));
                    leader.sendSystemMessage(Component.translatable("party_message.truedungeon.player_joined", self.getName()));
                    return 1;
                }
            }
        }

        self.sendSystemMessage(Component.translatable("party_message.truedungeon.failed_to_join"));
        return 0;
    }


}
