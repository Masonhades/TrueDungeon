package me.Masonhades.truedungeon.client;


import me.Masonhades.truedungeon.gui.CustomScreen;
import me.Masonhades.truedungeon.gui.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "truedungeon", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        MenuScreens.register(ModMenus.CUSTOM_MENU.get(), CustomScreen::new);
    }
}
