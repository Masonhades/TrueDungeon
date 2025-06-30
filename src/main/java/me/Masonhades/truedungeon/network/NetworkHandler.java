package me.Masonhades.truedungeon.network;

import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Truedungeon.MODID, "main"), // путь
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    public static void register(IEventBus modEventBus) {
        int id = 0;
        INSTANCE.registerMessage(
                id++, UpdateQuestsPacket.class,
                UpdateQuestsPacket::toBytes,
                UpdateQuestsPacket::new,
                UpdateQuestsPacket::handle
        );
    }


}
