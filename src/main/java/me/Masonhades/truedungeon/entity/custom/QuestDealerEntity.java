package me.Masonhades.truedungeon.entity.custom;


import me.Masonhades.truedungeon.Truedungeon;
import me.Masonhades.truedungeon.gui.CustomMenu;
import me.Masonhades.truedungeon.item.DungeonMapItem;
import me.Masonhades.truedungeon.item.ModItems;
import me.Masonhades.truedungeon.quests.QuestData;
import me.Masonhades.truedungeon.quests.QuestDataLoader;
import me.Masonhades.truedungeon.quests.DungeonQuest;
import me.Masonhades.truedungeon.quests.RewardItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

import java.util.Optional;


public class QuestDealerEntity extends PathfinderMob implements GeoEntity  {
    public  QuestDealerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private String currentAnimation = "idle";
    private final SimpleContainer questInventory  = new SimpleContainer(27);
    private long lastQuestGenerationDay = -1;

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
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            this.currentAnimation = "interact";
            this.generateQuestsIfNeeded();

            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("Quest Dealer");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    return new CustomMenu(id, inventory, QuestDealerEntity.this);
                }
            }, buffer -> buffer.writeVarInt(getId()));
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

    public SimpleContainer getQuestInventory() {
        return questInventory;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        tag.putLong("LastQuestGenerationDay", lastQuestGenerationDay);
        ListTag items = new ListTag();
        for (int i = 0; i < questInventory.getContainerSize(); i++){
            ItemStack stack = questInventory.getItem(i);
            if (!stack.isEmpty()){
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte)i);
                stack.save(itemTag);
                items.add(itemTag);
            }
        }
        tag.put("QuestInventory", items);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        if (tag.contains("LastQuestGenerationDay")) {
            lastQuestGenerationDay = tag.getLong("LastQuestGenerationDay");
        }
        ListTag items = tag.getList("QuestInventory", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++){
            CompoundTag itemTag = items.getCompound(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < questInventory.getContainerSize()){
                questInventory.setItem(slot, ItemStack.of(itemTag));
            }
        }
    }

    public void generateQuestsIfNeeded() {
        long currentDay = level().getDayTime()/24000;
        if( currentDay!= lastQuestGenerationDay){
            lastQuestGenerationDay = currentDay;
            generateNewQuests();
        }
    }

    public void  forceQuestRegeneration(){
        generateNewQuests();
    }

    private void generateNewQuests(){
        Truedungeon.LOGGER.info("NPC name: '{}'", this.getName().getString());
        questInventory.clearContent();


        String dealerName = this.getName().getString().toLowerCase().replace(" ", "_");
        Truedungeon.LOGGER.info("Генерация новых квестов для NPC: {}", dealerName);
        Optional<QuestData> dataOpt = QuestDataLoader.loadQuestData(dealerName);

        if (dataOpt.isEmpty()){
            Truedungeon.LOGGER.warn("Не удалось загрузить данные квестов для NPC '{}'", dealerName);
            return;
        }

        QuestData data = dataOpt.get();
        RandomSource random = RandomSource.create();

        for (int i = 0; i < 3; i++){
            DungeonQuest quest = QuestDataLoader.generateRandomQuest(data);
            Truedungeon.LOGGER.info("Квест {}: тип={}, уровень={}, награда={}", i + 1, quest.type, quest.level, quest.rewards);


            ItemStack map = new ItemStack(ModItems.DUNGEON_MAP.get());
            CompoundTag dungeonData = new CompoundTag();

            dungeonData.putString("dungeon_type", quest.type);
            dungeonData.putInt("dungeon_level", quest.level);
            dungeonData.putString("dungeon_objective", quest.objective);
            dungeonData.putString("dungeon_state", "sealed");


            ListTag rewardsList = new ListTag();
            for (RewardItem reward : quest.rewards) {
                CompoundTag rewardTag = new CompoundTag();
                rewardTag.putString("id", reward.id);
                rewardTag.putInt("count", reward.count);
                rewardsList.add(rewardTag);
            }
            dungeonData.put("dungeon_rewards", rewardsList);


            CompoundTag tag = map.getOrCreateTag();
            tag.put("DungeonData", dungeonData);

            CompoundTag display = new CompoundTag();
            display.putString("Name", "{\"text\":\"Dungeon Map - " + quest.type + "\"}");
            tag.put("display", display);

            map.setTag(tag);



            int slot;
            int attempts = 0;
            do {
                slot = random.nextInt(questInventory.getContainerSize());
                attempts++;
            } while (!questInventory.getItem(slot).isEmpty() && attempts < 20);

            if (questInventory.getItem(slot).isEmpty()) {
                questInventory.setItem(slot, map);
            } else {
                Truedungeon.LOGGER.warn("Не удалось найти свободный слот для карты (попыток: {})", attempts);
            }
        }
    }
}