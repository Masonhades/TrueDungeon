package me.Masonhades.truedungeon.network;

import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import me.Masonhades.truedungeon.gui.CustomMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateQuestsPacket {

    public UpdateQuestsPacket() {
    }

    public UpdateQuestsPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static void handle(UpdateQuestsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof CustomMenu menu) {
                Truedungeon.LOGGER.info("Игрок {} нажал кнопку обновления квестов", player.getName().getString());
                if (menu.getDealer() != null) {
                    menu.getDealer().forceQuestRegeneration();
                    Truedungeon.LOGGER.info("Квесты успешно обновлены для NPC: {}", menu.getDealer().getName().getString());
                } else {
                    Truedungeon.LOGGER.warn("Не удалось найти NPC у GUI");
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

