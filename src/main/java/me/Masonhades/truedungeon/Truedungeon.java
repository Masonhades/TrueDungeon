package me.Masonhades.truedungeon;

import me.Masonhades.truedungeon.block.ModBlocks;
import me.Masonhades.truedungeon.item.ModCreativeModTabs;
import me.Masonhades.truedungeon.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;



@Mod("truedungeon")
public class Truedungeon {
    public static final String MODID = "truedungeon";
    public Truedungeon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.DUNGEON_MAP);
        }
    }
    public static final ResourceKey<Level> DUNGEON_LEVEL_KEY =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation("truedungeon", "dungeon"));


}
