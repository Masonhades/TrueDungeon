package me.Masonhades.truedungeon.item;

import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Truedungeon.MODID);

    public static final RegistryObject <Item> DUNGERON_MAP =
            ITEMS.register("dungeon_map", () -> new DungeonMapItem(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

