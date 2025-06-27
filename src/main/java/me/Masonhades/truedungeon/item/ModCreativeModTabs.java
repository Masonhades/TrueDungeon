package me.Masonhades.truedungeon.item;

import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Truedungeon.MODID);

    public static final RegistryObject<CreativeModeTab> TRUEDUNGEON_TAB  = CREATIVE_MODE_TABS.register("truedungeon_tab",
            ()-> CreativeModeTab.builder().icon(()-> new ItemStack(ModItems.DUNGEON_MAP.get()))
                    .title(Component.translatable("creativetab.truedungeon"))
                    .displayItems((p_270258_, p_259752_) -> {
                        p_259752_.accept(ModItems.DUNGEON_MAP.get());

                        p_259752_.accept(ModBlocks.ENTRANCE_DOOR.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
