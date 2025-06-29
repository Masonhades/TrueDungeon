package me.Masonhades.truedungeon.entity.client;

import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class QuestDealerModel extends GeoModel<QuestDealerEntity> {


    @Override
    public ResourceLocation getModelResource(QuestDealerEntity questDealerEntity) {
        return new ResourceLocation(Truedungeon.MODID, "geo/quest_dealer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(QuestDealerEntity questDealerEntity) {
        return new ResourceLocation(Truedungeon.MODID, "textures/entity/quest_dealer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(QuestDealerEntity questDealerEntity) {
        return new ResourceLocation(Truedungeon.MODID, "animations/quest_dealer.animation.json");
    }
//    @Override
//    public void setCustomAnimations(QuestDealerModel animatable, long instanceId, AnimationState<QuestDealerModel> animationState){
//        CoreGeoBone head = getAnimationProcessor().getBone("head");
//
//        if(head != null){
//            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//
//            head.setRotX(entityData.headPitch()* Mth.DEG_TO_RAD);
//            head.setRotY(entityData.netHeadYaw()* Mth.DEG_TO_RAD);
//        }
//    }
}


