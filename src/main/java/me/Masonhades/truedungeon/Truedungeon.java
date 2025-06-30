package me.Masonhades.truedungeon;

import me.Masonhades.truedungeon.block.ModBlocks;
import me.Masonhades.truedungeon.entity.ModEntities;
import me.Masonhades.truedungeon.entity.client.QuestDealerRender;
import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import me.Masonhades.truedungeon.gui.ModMenus;
import me.Masonhades.truedungeon.item.ModCreativeModTabs;
import me.Masonhades.truedungeon.item.ModItems;
import me.Masonhades.truedungeon.network.NetworkHandler;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;


@Mod("truedungeon")
public class Truedungeon {
    public static final String MODID = "truedungeon";
    public static final Logger LOGGER = (Logger) LogManager.getLogger();

    public Truedungeon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        ModMenus.MENUS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntities.register(modEventBus);
        NetworkHandler.register(modEventBus);
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.DUNGEON_MAP);
        }
    }

    public static final ResourceKey<Level> DUNGEON_LEVEL_KEY =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation("truedungeon", "dungeon"));

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvent {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            EntityRenderers.register(ModEntities.QUEST_DEALER.get(), (EntityRendererProvider<QuestDealerEntity>) QuestDealerRender::new);

        }
    }



}
