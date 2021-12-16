package coda.bookofdragons.common.entities;

import coda.bookofdragons.common.entities.util.AbstractFlyingDragonEntity;
import coda.bookofdragons.common.entities.util.AbstractRideableDragonEntity;
import coda.bookofdragons.common.entities.util.goal.FlyingDragonWanderGoal;
import coda.bookofdragons.common.entities.util.goal.TerrorIntimidateGoal;
import coda.bookofdragons.init.BODEntities;
import coda.bookofdragons.init.BODItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class TerribleTerrorEntity extends AbstractFlyingDragonEntity implements FlyingAnimal, IAnimatable, IAnimationTickable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private static final EntityDataAccessor<Boolean> SNAPPING = SynchedEntityData.defineId(AbstractRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private int snapTimer;

    public TerribleTerrorEntity(EntityType<? extends AbstractFlyingDragonEntity> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.25F).add(Attributes.FLYING_SPEED, 0.8F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SNAPPING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new FlyingDragonWanderGoal(this, 50));
        this.goalSelector.addGoal(4, new TerrorIntimidateGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println(this.getSnapping());
        Vec3 lookVec = this.getViewVector(1.0f);
        if(this.getSnapping()){
            if(this.snapTimer < 20){
                this.snapTimer++;
            }
            if(this.snapTimer > 8 && this.snapTimer < 12){
                for(int i = 0; i < 4; i++) {
                    this.level.addParticle(ParticleTypes.FLAME, this.getX() + lookVec.x() * 0.9, this.getY() + 0.35f, this.getZ() +  lookVec.z() * 0.9, lookVec.x()/4 , lookVec.y() / 2, lookVec.z()/4);
                }
            }
        }
        else{
           this.snapTimer = 0;
        }
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredient.of(ItemTags.FISHES);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FISHES);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob ageableMob) {
        return BODEntities.TERRIBLE_TERROR.get().create(world);
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(BODItems.TERRIBLE_TERROR_SPAWN_EGG.get());
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
        data.addAnimationController(new AnimationController<>(this, "controllerSnap", 5, this::predicateSnap));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !this.isOnGround()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.fly", true));
            return PlayState.CONTINUE;
        }
        else if (!this.isOnGround() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.fly_idle", true));
            return PlayState.CONTINUE;
        }
        else if (!this.isFlying() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.walk", true));
            return PlayState.CONTINUE;
        }
        else if (!this.isFlying() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.idle", true));
            return PlayState.CONTINUE;
        }
        else {
            return PlayState.STOP;
        }
    }

    private <E extends IAnimatable> PlayState predicateSnap(AnimationEvent<E> event) {
        if(getSnapping()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.terrible_terror.threaten", true));
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

    @Override
    public int tickTimer() {
        return tickCount;
    }

    public void setSnapping(boolean snapping){
        this.entityData.set(SNAPPING, snapping);
    }

    public boolean getSnapping(){
        return this.entityData.get(SNAPPING);
    }
}
