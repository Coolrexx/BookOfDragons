package net.arathain.bookofdragons.common.entity;

import net.arathain.bookofdragons.BODComponents;
import net.arathain.bookofdragons.common.entity.goal.FlyingDragonWanderGoal;
import net.arathain.bookofdragons.common.entity.goal.FollowDriverGoal;
import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.arathain.bookofdragons.common.init.BODEntities;
import net.arathain.bookofdragons.common.init.BODObjects;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;

public class DeadlyNadderEntity extends AbstractRideableDragonEntity implements  IAnimatable{
    public static final TrackedData<String> TYPE = DataTracker.registerData(DeadlyNadderEntity.class, TrackedDataHandlerRegistry.STRING);
    private final AnimationFactory factory = new AnimationFactory(this);
    //flight
    public Vec3d targetPosition;
    public BlockPos circlingCenter = BlockPos.ORIGIN;


    public DeadlyNadderEntity(EntityType<? extends DeadlyNadderEntity> type, World world) {
        super(type, world);
        this.moveControl = new NadderMoveControl(this);
        this.lookControl = new NadderLookControl(this);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.285F).add(EntityAttributes.GENERIC_ARMOR, 5.0F).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0F).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 7.0F).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.8F).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
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
        this.goalSelector.add(4, new FlyingDragonWanderGoal(this, 150));
        this.goalSelector.add(3, new DeadlyNadderEntity.StartAttackGoal());
        this.goalSelector.add(4, new DeadlyNadderEntity.SwoopMovementGoal());
        this.goalSelector.add(6, new FlyWithOwnerGoal());
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(3, new FollowDriverGoal(this));
        this.goalSelector.add(1, new TemptGoal(this, 1.25D, getIngredient(), false));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0D));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
        this.goalSelector.add(2, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 15, 1));
        this.goalSelector.add(5, new FleeEntityGoal<>(this, EelEntity.class, 8.0F, 1.0D, 1.2D));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    public boolean canFly() {
        return true;
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
        AnimationBuilder animationBuilder = new AnimationBuilder();
        if(this.isAttacking()) {
            animationBuilder.addAnimation("bite");
        }
        if (isFlying() || !isOnGround() && event.isMoving()) {
            animationBuilder.addAnimation("fly", true);
        } else if (isFlying() || !isOnGround() && !event.isMoving()) {
            animationBuilder.addAnimation("fly_idle", true);
        } else if (!this.isInAir() && event.isMoving()) {
            animationBuilder.addAnimation("walk", true);
        } else if (!this.isInAir()  && !event.isMoving()) {
            animationBuilder.addAnimation("land_idle", true);
        }
        if(!animationBuilder.getRawAnimationList().isEmpty()) {
            event.getController().setAnimation(animationBuilder);
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
            this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
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

            if (!flying && isPlayerUpwardsMoving) {this.jump(); this.setFlying(true);};

            if (this.getControllingPassenger() != null) {
                travelVector = new Vec3d(0, -this.getPitch() * 0.05, 3.4335 + passenger.forwardSpeed * 3.5);
                this.setMovementSpeed(speed);
                this.stepBobbingAmount = 0;
                this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            } else if (passenger instanceof PlayerEntity) {
                this.updateLimbs(this, false);
                this.setVelocity(Vec3d.ZERO);
                this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
                return;
            }
        }
        if (flying) {
            this.applyMovementInput(travelVector, speed);
            this.move(MovementType.SELF, getVelocity());
            this.setVelocity(getVelocity().multiply(0.91f));
            this.updateLimbs(this, false);
            this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        } else {
            this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            super.travel(travelVector);
        }
    }
    public void updatePassengerPosition(Entity passenger) {
        this.updatePassengerPosition(passenger, Entity::setPosition);
    }
    @Override
    public void mobTick() {
        if (this.hasCustomName()) {
            if (this.getCustomName().getString().equalsIgnoreCase("apathy") && this.getNadderType() != NadderType.ARATHAIN) {
                this.setNadderType(NadderType.ARATHAIN);
            }
        }
        super.mobTick();
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


    protected BodyControl createBodyControl() {
        return new NadderFlyingBodyControl(this);
    }

    public enum NadderType {
        GOLDEN,
        TEAL,
        LAPIS,
        IMPERIAL,
        ARATHAIN
    }

    class StartAttackGoal extends Goal {

        private StartAttackGoal() {
        }

        public boolean canStart() {
            return !DeadlyNadderEntity.this.isTouchingWater() && DeadlyNadderEntity.this.canFly() && DeadlyNadderEntity.this.getTarget() != null && DeadlyNadderEntity.this.isTarget(DeadlyNadderEntity.this.getTarget(), TargetPredicate.DEFAULT);
        }

        public void start() {
            this.startSwoop();
            DeadlyNadderEntity.this.addVelocity(0, 1, 0);
            DeadlyNadderEntity.this.setFlying(true);
        }

        public void stop() {
            DeadlyNadderEntity.this.circlingCenter = DeadlyNadderEntity.this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, DeadlyNadderEntity.this.circlingCenter).up(10 + DeadlyNadderEntity.this.random.nextInt(20));
            DeadlyNadderEntity.this.setFlying(false);
        }

        private void startSwoop() {
            DeadlyNadderEntity.this.circlingCenter = DeadlyNadderEntity.this.getTarget().getBlockPos().up(20 + DeadlyNadderEntity.this.random.nextInt(20));
            if (DeadlyNadderEntity.this.circlingCenter.getY() < DeadlyNadderEntity.this.world.getSeaLevel()) {
                DeadlyNadderEntity.this.circlingCenter = new BlockPos(DeadlyNadderEntity.this.circlingCenter.getX(), DeadlyNadderEntity.this.world.getSeaLevel() + 1, DeadlyNadderEntity.this.circlingCenter.getZ());
            }
        }
    }

    class SwoopMovementGoal extends MovementGoal {
        private SwoopMovementGoal() {
            super();
        }

        public boolean canStart() {
            return DeadlyNadderEntity.this.isFlying() && DeadlyNadderEntity.this.canFly() && DeadlyNadderEntity.this.getTarget() != null;
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = DeadlyNadderEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (livingEntity instanceof PlayerEntity && (livingEntity.isSpectator() || ((PlayerEntity) livingEntity).isCreative())) {
                return false;
            } else if (!this.canStart()) {
                return false;
            } else {
                return DeadlyNadderEntity.this.isFlying();
            }
        }

        public void tick() {
            LivingEntity livingEntity = DeadlyNadderEntity.this.getTarget();
            DeadlyNadderEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5D), livingEntity.getZ());
            if (DeadlyNadderEntity.this.getBoundingBox().expand(0.20000000298023224D).intersects(livingEntity.getBoundingBox())) {
                DeadlyNadderEntity.this.tryAttack(livingEntity);
                if (!DeadlyNadderEntity.this.isSilent()) {
                    world.playSoundFromEntity(null, DeadlyNadderEntity.this, SoundEvents.ENTITY_RAVAGER_ATTACK, DeadlyNadderEntity.this.getSoundCategory(), DeadlyNadderEntity.this.getSoundVolume(), DeadlyNadderEntity.this.getSoundPitch());
                }
            }
        }
    }

    class FlyWithOwnerGoal extends MovementGoal {
        private FlyWithOwnerGoal() {
            super();
        }

        public boolean canStart() {
            return DeadlyNadderEntity.this.getOwner() != null && DeadlyNadderEntity.this.getOwner().isFallFlying() && !DeadlyNadderEntity.this.isTouchingWater() && DeadlyNadderEntity.this.canFly();
        }

        public boolean shouldContinue() {
            LivingEntity owner = DeadlyNadderEntity.this.getOwner();
            if (owner == null) {
                return false;
            } else if (!owner.isAlive()) {
                return false;
            } else if (owner instanceof PlayerEntity && (owner.isSpectator() || ((PlayerEntity) owner).isCreative())) {
                return false;
            } else if (!this.canStart()) {
                return false;
            } else {
                return DeadlyNadderEntity.this.isFlying();
            }
        }

        public void tick() {
            LivingEntity owner = DeadlyNadderEntity.this.getOwner();
            if (owner != null) {
                DeadlyNadderEntity.this.setFlying(true);
                DeadlyNadderEntity.this.targetPosition = new Vec3d(owner.getX(), owner.getBodyY(0.5D), owner.getZ());
            }
        }
    }

    abstract class MovementGoal extends Goal {
        public MovementGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        protected boolean isNearTarget() {
            return DeadlyNadderEntity.this.targetPosition.squaredDistanceTo(DeadlyNadderEntity.this.getX(), DeadlyNadderEntity.this.getY(), DeadlyNadderEntity.this.getZ()) < 4.0D;
        }
    }

    class NadderLookControl extends LookControl {
        public NadderLookControl(MobEntity entity) {
            super(entity);
        }

        public void tick() {
            if (!DeadlyNadderEntity.this.isFlying()) {
                super.tick();
            }
        }
    }

    class NadderFlyingBodyControl extends BodyControl {
        public NadderFlyingBodyControl(MobEntity entity) {
            super(entity);
        }

        public void tick() {
            DeadlyNadderEntity.this.headYaw = DeadlyNadderEntity.this.bodyYaw;
            DeadlyNadderEntity.this.bodyYaw = DeadlyNadderEntity.this.getYaw();
        }
    }

    class NadderMoveControl extends MoveControl {
        private float targetSpeed = 0.1F;

        public NadderMoveControl(MobEntity owner) {
            super(owner);
        }

        public void tick() {
            if (DeadlyNadderEntity.this.hasPassengers()) {
                if (this.state == MoveControl.State.MOVE_TO) {
                    this.state = MoveControl.State.WAIT;
                    this.entity.setNoGravity(true);
                    double d = this.targetX - this.entity.getX();
                    double e = this.targetY - this.entity.getY();
                    double f = this.targetZ - this.entity.getZ();
                    double g = d * d + e * e + f * f;
                    if (g < 2.500000277905201E-7) {
                        this.entity.setUpwardSpeed(0.0f);
                        this.entity.setForwardSpeed(0.0f);
                        return;
                    }
                    float h = (float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
                    this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), h, 90.0f));
                    float i = this.entity.isOnGround() ? (float) (this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)) : (float) (this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_FLYING_SPEED));
                    this.entity.setMovementSpeed(i);
                    double j = Math.sqrt(d * d + f * f);
                    if (Math.abs(e) > (double) 1.0E-5f || Math.abs(j) > (double) 1.0E-5f) {
                        this.entity.setUpwardSpeed(e > 0.0 ? i : -i);
                    }
                } else {
                    this.entity.setNoGravity(false);
                    this.entity.setUpwardSpeed(0.0f);
                    this.entity.setForwardSpeed(0.0f);
                }
            } else {
                    if (DeadlyNadderEntity.this.isFlying()) {
                        if (DeadlyNadderEntity.this.horizontalCollision) {
                            DeadlyNadderEntity var10000 = DeadlyNadderEntity.this;
                            var10000.setYaw(var10000.getYaw() + 180.0F);
                            this.targetSpeed = 0.1F;
                        }

                        if (DeadlyNadderEntity.this.targetPosition != null) {
                            float f = (float) (DeadlyNadderEntity.this.targetPosition.x - DeadlyNadderEntity.this.getX());
                            float g = (float) (DeadlyNadderEntity.this.targetPosition.y - DeadlyNadderEntity.this.getY());
                            float h = (float) (DeadlyNadderEntity.this.targetPosition.z - DeadlyNadderEntity.this.getZ());
                            double d = MathHelper.sqrt(f * f + h * h);
                            double e = 1.0D - (double) MathHelper.abs(g * 0.7F) / d;
                            f = (float) ((double) f * e);
                            h = (float) ((double) h * e);
                            d = MathHelper.sqrt(f * f + h * h);
                            double i = MathHelper.sqrt(f * f + h * h + g * g);
                            float j = DeadlyNadderEntity.this.getYaw();
                            float k = (float) MathHelper.atan2(h, f);
                            float l = MathHelper.wrapDegrees(DeadlyNadderEntity.this.getYaw() + 90.0F);
                            float m = MathHelper.wrapDegrees(k * 57.295776F);
                            DeadlyNadderEntity.this.setYaw(MathHelper.stepUnwrappedAngleTowards(l, m, 4.0F) - 90.0F);
                            DeadlyNadderEntity.this.bodyYaw = DeadlyNadderEntity.this.getYaw();
                            this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 2.0F, 0.01F * (2.0F / this.targetSpeed));

                            float n = (float) (-(MathHelper.atan2(-g, d) * 57.2957763671875D));
                            DeadlyNadderEntity.this.setPitch(n);
                            float o = DeadlyNadderEntity.this.getYaw() + 90.0F;
                            double p = (double) (this.targetSpeed * MathHelper.cos(o * 0.017453292F)) * Math.abs((double) f / i);
                            double q = (double) (this.targetSpeed * MathHelper.sin(o * 0.017453292F)) * Math.abs((double) h / i);
                            double r = (double) (this.targetSpeed * MathHelper.sin(n * 0.017453292F)) * Math.abs((double) g / i);
                            Vec3d vec3d = DeadlyNadderEntity.this.getVelocity();
                            DeadlyNadderEntity.this.setVelocity(vec3d.add((new Vec3d(p, r, q)).subtract(vec3d).multiply(0.05D)));
                        }
                    } else {
                        super.tick();
                    }
            }
        }
    }
}
