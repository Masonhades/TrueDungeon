package me.Masonhades.truedungeon.entity.custom;


import me.Masonhades.truedungeon.gui.CustomMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;



public class QuestDealerEntity extends PathfinderMob implements GeoEntity  {
    public  QuestDealerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private String currentAnimation = "idle";

    public static AttributeSupplier setAttributes(){
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .build();
    }
    @Override
    protected void registerGoals(){
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }



    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (!"interact".equals(currentAnimation)) {
            if (tAnimationState.isMoving()) {
                tAnimationState.getController().setAnimation(
                        RawAnimation.begin().then("animation.questdealer.walk", Animation.LoopType.LOOP)
                );
            } else {
                tAnimationState.getController().setAnimation(
                        RawAnimation.begin().then("animation.questdealer.idle", Animation.LoopType.LOOP)
                );
            }
        } else {
            tAnimationState.getController().setAnimation(
                    RawAnimation.begin().then("animation.questdealer.interact", Animation.LoopType.PLAY_ONCE)
            );
            currentAnimation = "idle";
        }
        return PlayState.CONTINUE;
    }

    @Override
    public  InteractionResult interactAt( Player player,  Vec3 hitPos,  InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            this.currentAnimation = "interact";

            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public  Component getDisplayName() {
                    return Component.literal("Quest Dealer");
                }

                @Override
                public AbstractContainerMenu createMenu(int id,  Inventory inventory,  Player player) {
                    return new CustomMenu(id, inventory);
                }
            });
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft", "entity.generic.hurt"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft", "entity.generic.death"));
    }
}
