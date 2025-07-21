package me.Masonhades.truedungeon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.IOException;


@Mod.EventBusSubscriber(modid = "truedungeon", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AutoWorldLoader {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Minecraft mc = Minecraft.getInstance();
        String worldName = "New World (1)";

        event.enqueueWork(() -> {
            try {
                LevelStorageSource levelSource = mc.getLevelSource();
                LevelStorageSource.LevelStorageAccess access = levelSource.createAccess(worldName);

                mc.forceSetScreen(null);
                mc.createWorldOpenFlows().loadLevel(null, String.valueOf(access));

                System.out.println("✅ Автозагрузка мира " + worldName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

