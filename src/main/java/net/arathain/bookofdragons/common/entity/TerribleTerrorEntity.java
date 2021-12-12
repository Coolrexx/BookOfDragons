package net.arathain.bookofdragons.common.entity;

import net.arathain.bookofdragons.common.entity.goal.FlyingDragonWanderGoal;
import net.arathain.bookofdragons.common.entity.goal.TerrorIntimidateGoal;
import net.arathain.bookofdragons.common.entity.util.AbstractFlyingDragonEntity;
import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.arathain.bookofdragons.common.init.BODEntities;
import net.arathain.bookofdragons.common.init.BODObjects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

public class TerribleTerrorEntity extends AbstractFlyingDragonEntity implements Flutterer, IAnimatable {
    private static final TrackedData<Boolean> SNAPPING = DataTracker.registerData(AbstractRideableDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final AnimationFactory factory = new AnimationFactory(this);

    public TerribleTerrorEntity(EntityType<? extends AbstractFlyingDragonEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25F).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(4, new FlyingDragonWanderGoal(this, 50));
        this.goalSelector.add(4, new TerrorIntimidateGoal(this));
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
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SNAPPING, false);
    }

    public void setSnapping(boolean snapping){
        this.dataTracker.set(SNAPPING, snapping);
    }

    public boolean getSnapping(){
        return this.dataTracker.get(SNAPPING);
    }



    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BODObjects.TERRIBLE_TERROR_SPAWN_EGG);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(getSnapping()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.bite", true));
            return PlayState.CONTINUE;
        }
        if (event.isMoving() && !this.isOnGround()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.fly", true));
            return PlayState.CONTINUE;
        }
        else if (!this.isOnGround() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.fly_idle", true));
            return PlayState.CONTINUE;
        }
        else if (!this.isInAir() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.walk", true));
            return PlayState.CONTINUE;
        }
        else if (!this.isInAir() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.idle", true));
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

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return BODEntities.TERRIBLE_TERROR.create(world);
    }

}
