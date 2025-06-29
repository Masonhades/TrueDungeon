package me.Masonhades.truedungeon.entity;

import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
        public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
                DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Truedungeon.MODID);


        public static final RegistryObject<EntityType<QuestDealerEntity>> QUEST_DEALER =
                    ENTITY_TYPES.register("quest_dealer", () -> EntityType.Builder.of(QuestDealerEntity:: new, MobCategory.CREATURE)
                            .sized(1f, 2f)
                            .build(new ResourceLocation(Truedungeon.MODID, "quest_dealer").toString()));





        public static void register(IEventBus eventBus){
            ENTITY_TYPES.register(eventBus);
        }
}
