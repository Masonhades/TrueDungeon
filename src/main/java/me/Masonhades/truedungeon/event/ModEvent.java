package me.Masonhades.truedungeon.event;


import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.entity.ModEntities;
import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Truedungeon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.QUEST_DEALER.get(), QuestDealerEntity.setAttributes());
    }
}
