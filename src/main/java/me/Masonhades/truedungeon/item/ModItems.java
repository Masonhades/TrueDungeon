package me.Masonhades.truedungeon.item;

import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Truedungeon.MODID);

    public static final RegistryObject <Item> DUNGEON_MAP =
            ITEMS.register("dungeon_map", () -> new DungeonMapItem(new Item.Properties()));


    public static final RegistryObject<Item> QUEST_DEALER_SPAWN_EGG = ITEMS.register("tiger_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.QUEST_DEALER, 0xD57E36, 0x1D0D00,
                    new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

