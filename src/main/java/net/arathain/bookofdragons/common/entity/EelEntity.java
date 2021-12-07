package net.arathain.bookofdragons.common.entity;

import net.arathain.bookofdragons.common.init.BODObjects;
import net.arathain.bookofdragons.common.entity.goal.EelLeapAtTargetGoal;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class EelEntity extends WaterCreatureEntity implements IAnimatable, Bucketable {
    private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(EelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final AnimationFactory factory = new AnimationFactory(this);

    public EelEntity(EntityType<? extends WaterCreatureEntity> type, World world) {
        super(type, world);
        this.moveControl = new AquaticMoveControl(this, 15, 10, 0.02F, 0.1F, true);
        this.lookControl = new YawAdjustingLookControl(this, 10);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    protected void registerGoals() {
        this.goalSelector.add(0, new EelLeapAtTargetGoal(this, 0.4F));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(1, new SwimAroundGoal(this, 1.0D, 1));
        this.goalSelector.add(2, new LookAroundGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 20.0F, 0.0005F, false));
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public void travel(Vec3d movementInput) {
        if (!this.isAiDisabled() && this.isInsideWaterOrBubbleColumn()) {
            if (!world.getBlockState(getBlockPos().down()).isOf(Blocks.WATER) && !world.getBlockState(getBlockPos().up()).isAir()) {
                this.setVelocity(this.getVelocity().add(0, 0.015, 0));
            }
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(1.2D));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0D, -0.005D, 0.0D));
            }
        } else if (!this.isAiDisabled() && !this.isInsideWaterOrBubbleColumn()) {
            this.setMovementSpeed(2.0F);
        }
        super.travel(movementInput);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new EelPathNavigation(this, world);
    }

    @Override
    protected void tickWaterBreathingAir(int air) {

    }

    @Override
    public SoundEvent getBucketedSound() {
        return SoundEvents.ITEM_BUCKET_FILL_FISH;
    }

    @Override
    public boolean isFromBucket() {
        return this.dataTracker.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean bucket) {
        this.dataTracker.set(FROM_BUCKET, bucket);
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FROM_BUCKET, false);
    }

    @Override
    public ItemStack getBucketItem() {
        return BODObjects.EEL_BUCKET.getDefaultStack();
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BODObjects.EEL_SPAWN_EGG);
    }


    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.225f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_COD_DEATH;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (isInsideWaterOrBubbleColumn() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.swim", true));
            return PlayState.CONTINUE;
        }
        else if (isInsideWaterOrBubbleColumn() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.water_idle", true));
            return PlayState.CONTINUE;
        }
        else if (!isInsideWaterOrBubbleColumn() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.slither", true));
            return PlayState.CONTINUE;
        }
        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.land_idle", true));
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    static class EelPathNavigation extends SwimNavigation {
        EelPathNavigation(EelEntity entity, World world) {
            super(entity, world);
        }

        protected boolean isAtValidPosition() {
            return true;
        }

        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new AmphibiousPathNodeMaker(false);
            return new PathNodeNavigator(this.nodeMaker, range);
        }

        public boolean isValidPosition(BlockPos pos) {
            return !this.world.getBlockState(this.entity.getBlockPos().down()).isAir();
        }
    }
}
