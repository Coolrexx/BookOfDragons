package net.arathain.bookofdragons.common.entity;

import net.arathain.bookofdragons.common.entity.goal.FlyingDragonWanderGoal;
import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.arathain.bookofdragons.common.init.BODEntities;
import net.arathain.bookofdragons.common.init.BODObjects;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ItemTags;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GronckleEntity extends AbstractRideableDragonEntity implements Flutterer, IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public GronckleEntity(EntityType<? extends GronckleEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25F).add(EntityAttributes.GENERIC_ARMOR, 10.0F).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 30.0F).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0F).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.4F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(4, new FlyingDragonWanderGoal(this, 150));
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredient.fromTag(ItemTags.FISHES);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ItemTags.FISHES);
    }

    @Override
    protected void mobTick() {
        if (this.getPositionTarget() != null && !(this.getOwner() != null && this.getOwner().isFallFlying()) && (this.isTouchingWater() || this.isOnGround())) {
            this.setFlying(false);
        }
        super.mobTick();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return BODEntities.GRONCKLE.create(world);
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BODObjects.GRONCKLE_SPAWN_EGG);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.2F;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (isInAir() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", true));
            return PlayState.CONTINUE;
        }
        else if (isInAir() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("fly_idle", true));
            return PlayState.CONTINUE;
        }
        else if (!isInAir() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", true));
            return PlayState.CONTINUE;
        }
        else if (!isInAir() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("land_idle", true));
            return PlayState.CONTINUE;
        }
        else {
            return PlayState.STOP;
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
