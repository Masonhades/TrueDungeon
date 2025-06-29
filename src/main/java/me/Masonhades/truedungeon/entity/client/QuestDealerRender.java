package me.Masonhades.truedungeon.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.entity.custom.QuestDealerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class QuestDealerRender extends GeoEntityRenderer<QuestDealerEntity> {
    public QuestDealerRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new QuestDealerModel());
    }
    @Override
    public ResourceLocation getTextureLocation(QuestDealerEntity animatable){
        return new ResourceLocation(Truedungeon.MODID, "textures/entity/quest_dealer.png");
    }
    @Override
    public void render(QuestDealerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){


        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
