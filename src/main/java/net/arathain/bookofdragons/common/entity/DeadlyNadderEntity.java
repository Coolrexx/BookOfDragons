package net.arathain.bookofdragons.common.entity;

import net.arathain.bookofdragons.BODComponents;
import net.arathain.bookofdragons.common.entity.goal.FlyingDragonWanderGoal;
import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.arathain.bookofdragons.common.init.BODEntities;
import net.arathain.bookofdragons.common.init.BODObjects;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.MathHelper;
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

public class DeadlyNadderEntity extends AbstractRideableDragonEntity implements  IAnimatable{
    public static final TrackedData<String> TYPE = DataTracker.registerData(DeadlyNadderEntity.class, TrackedDataHandlerRegistry.STRING);
    private final AnimationFactory factory = new AnimationFactory(this);

    public DeadlyNadderEntity(EntityType<? extends DeadlyNadderEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.285F).add(EntityAttributes.GENERIC_ARMOR, 5.0F).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0F).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        if (this.random.nextInt(10) == 0) {
            this.dataTracker.startTracking(TYPE, NadderType.IMPERIAL.toString());
        } else {
            if (this.random.nextInt(3) == 0) {
                this.dataTracker.startTracking(TYPE, NadderType.LAPIS.toString());
            } else {
                if (this.random.nextBoolean()) {
                    this.dataTracker.startTracking(TYPE, NadderType.GOLDEN.toString());
                } else {
                    this.dataTracker.startTracking(TYPE, NadderType.TEAL.toString());
                }
            }
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);

        if (tag.contains("Type")) {
            this.setNadderType(NadderType.valueOf(tag.getString("Type")));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        tag.putString("Type", this.getNadderType().toString());
    }

    public NadderType getNadderType() {
        return NadderType.valueOf(this.dataTracker.get(TYPE));
    }

    public void setNadderType(NadderType type) {
        this.dataTracker.set(TYPE, type.toString());
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

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return BODEntities.DEADLY_NADDER.create(world);
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(BODObjects.DEADLY_NADDER_SPAWN_EGG);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.5F;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (!isOnGround() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", true));
            return PlayState.CONTINUE;
        } else if (!isOnGround() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("fly_idle", true));
            return PlayState.CONTINUE;
        } else if (!isInAir() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", true));
            return PlayState.CONTINUE;
        } else if (!isInAir() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("land_idle", true));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void travel(Vec3d travelVector) {
        boolean flying = this.isInAir();
        float speed = (float) this.getAttributeValue(flying ? EntityAttributes.GENERIC_FLYING_SPEED : EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (!this.hasPassengers() && !this.canBeControlledByRider() && !this.isSaddled()) {
            this.airStrafingSpeed = 0.02f;
            super.skipTravel(travelVector);
            return;
        }
        LivingEntity passenger = (LivingEntity) this.getControllingPassenger();
        if (passenger != null) {
            this.headYaw = passenger.getHeadYaw() * 0.5f;
            this.serverHeadYaw = this.headYaw;
            this.serverYaw = this.serverYaw - passenger.sidewaysSpeed * 2f;
            boolean isPlayerUpwardsMoving = BODComponents.DRAGON_RIDER_COMPONENT.get(passenger).isPressingUp();
            boolean isPlayerDownwardsMoving = BODComponents.DRAGON_RIDER_COMPONENT.get(passenger).isPressingDown();
            double getFlightDelta = isPlayerUpwardsMoving ? 0.4 : isPlayerDownwardsMoving ? -0.5 : 0;
            this.serverPitch = MathHelper.clamp(this.serverPitch - getFlightDelta * 10, -70, 70);
            this.setPitch((float) this.serverPitch);
            this.setYaw((float) this.serverYaw);
            this.setRotation(this.getYaw(), this.getPitch());
            this.bodyYaw = (float) this.serverYaw;

            if (!flying && isPlayerUpwardsMoving) this.jump();

            if (this.getControllingPassenger() != null) {
                travelVector = new Vec3d(0, -this.getPitch() * 0.05, 3.4335 + passenger.forwardSpeed * 3.5);
                this.setMovementSpeed(speed);
                this.stepBobbingAmount = 0;
            } else if (passenger instanceof PlayerEntity) {
                this.updateLimbs(this, false);
                this.setVelocity(Vec3d.ZERO);
                return;
            }
        }
        if (flying) {
            this.applyMovementInput(travelVector, speed);
            this.move(MovementType.SELF, getVelocity());
            this.setVelocity(getVelocity().multiply(0.91f));
            this.updateLimbs(this, false);
        } else {
            super.travel(travelVector);
        }
    }
    public void updatePassengerPosition(Entity passenger) {
        this.updatePassengerPosition(passenger, Entity::setPosition);
    }

    public void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
        if (this.hasPassenger(passenger)) {
            double d = this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset() + 0.2;
            positionUpdater.accept(passenger, this.getX(), d, this.getZ());
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public enum NadderType {
        GOLDEN,
        TEAL,
        LAPIS,
        IMPERIAL
    }
}
