package coda.bookofdragons.common.entities;

import coda.bookofdragons.client.ClientEvents;
import coda.bookofdragons.common.entities.util.AbstractRideableDragonEntity;
import coda.bookofdragons.common.entities.util.goal.FlyingDragonWanderGoal;
import coda.bookofdragons.common.entities.util.goal.FollowDriverGoal;
import coda.bookofdragons.init.BODEntities;
import coda.bookofdragons.init.BODItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
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

public class DeadlyNadderEntity extends AbstractRideableDragonEntity implements FlyingAnimal, IAnimatable, IAnimationTickable {
    public static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(DeadlyNadderEntity.class, EntityDataSerializers.STRING);
    private final AnimationFactory factory = new AnimationFactory(this);
    //flight
    public Vec3 targetPosition;
    public BlockPos circlingCenter = BlockPos.ZERO;

    public DeadlyNadderEntity(EntityType<? extends DeadlyNadderEntity> type, Level world) {
        super(type, world);
        this.moveControl = new NadderMoveControl(this);
        this.lookControl = new NadderLookControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.285F).add(Attributes.ARMOR, 5.0F).add(Attributes.ATTACK_DAMAGE, 7.0F).add(Attributes.ATTACK_KNOCKBACK, 7.0F).add(Attributes.FLYING_SPEED, 0.8F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        if (this.random.nextInt(10) == 0) {
            this.entityData.set(TYPE, NadderType.IMPERIAL.toString());
        } else {
            if (this.random.nextInt(3) == 0) {
                this.entityData.set(TYPE, NadderType.LAPIS.toString());
            } else {
                if (this.random.nextBoolean()) {
                    this.entityData.set(TYPE, NadderType.GOLDEN.toString());
                } else {
                    this.entityData.set(TYPE, NadderType.TEAL.toString());
                }
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Type")) {
            this.setNadderType(NadderType.valueOf(tag.getString("Type")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Type", this.getNadderType().toString());
    }

    public NadderType getNadderType() {
        return NadderType.valueOf(this.entityData.get(TYPE));
    }

    public void setNadderType(NadderType type) {
        this.entityData.set(TYPE, type.toString());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.25D, getIngredient(), false));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new DeadlyNadderEntity.StartAttackGoal());
        this.goalSelector.addGoal(3, new FollowDriverGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15, 1));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new FlyingDragonWanderGoal(this, 150));
        this.goalSelector.addGoal(4, new DeadlyNadderEntity.SwoopMovementGoal());
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, EelEntity.class, 8.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(6, new FlyWithOwnerGoal());
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
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
        return BODEntities.DEADLY_NADDER.get().create(world);
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(BODItems.DEADLY_NADDER_SPAWN_EGG.get());
    }

    @Override
    protected float getStandingEyeHeight(Pose p_21131_, EntityDimensions p_21132_) {
        return isBaby() ? 1.0F : 1.5F;
    }

    @Override
    public void positionRider(Entity passenger) {
        Vec3 pos = getYawVec(yBodyRot, 0.0F, -0.35F).add(getX(), getY() + 1.5F, getZ());
        passenger.setPos(pos.x, pos.y, pos.z);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    public boolean canFly() {
        return true;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationBuilder animationBuilder = new AnimationBuilder();
        if (this.isAggressive()) {
            animationBuilder.addAnimation("animation.deadly_nadder.bite");
        }
        if (isFlying() || !isOnGround() && event.isMoving()) {
            animationBuilder.addAnimation("animation.deadly_nadder.fly", true);
        } else if (isFlying() || !isOnGround() && !event.isMoving()) {
            animationBuilder.addAnimation("animation.deadly_nadder.fly_idle", true);
        } else if (this.isOnGround() && event.isMoving()) {
            animationBuilder.addAnimation("animation.deadly_nadder.walk", true);
        } else if (this.isOnGround()  && !event.isMoving()) {
            animationBuilder.addAnimation("animation.deadly_nadder.idle", true);
        }
        if (!animationBuilder.getRawAnimationList().isEmpty()) {
            event.getController().setAnimation(animationBuilder);
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        boolean flying = !this.isOnGround();
        float speed = (float) this.getAttributeValue(flying ? Attributes.FLYING_SPEED : Attributes.MOVEMENT_SPEED);
        if (!this.isVehicle() && !this.canBeControlledByRider() && !this.isSaddled()) {
            this.flyingSpeed = 0.02f;
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            return;
        }
        LivingEntity passenger = (LivingEntity) this.getControllingPassenger();
        if (passenger != null) {
            this.yHeadRot = passenger.getYHeadRot() * 0.5f;
            this.yHeadRotO = this.yHeadRot;
            this.yRotO = this.yRotO - passenger.xxa * 2f;
            double getFlightDelta = ClientEvents.getFlightDelta();
            this.xRotO = (float) Mth.clamp(this.xRotO - getFlightDelta * 10, -70, 70);
            this.setXRot(this.xRotO);
            this.setYRot((float) this.yRotO);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = (float) this.yRotO;

            if (!flying && getFlightDelta > 0) {
                this.jumpFromGround();
                this.setFlying(true);
            }

            if (this.getControllingPassenger() != null) {
                travelVector = new Vec3(0, -this.getXRot() * 0.05, 3.4335 + passenger.zza * 3.5);
                this.setSpeed(speed);
                this.run = 0;
                this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            } else if (passenger instanceof Player) {
                this.calculateEntityAnimation(this, false);
                this.setDeltaMovement(Vec3.ZERO);
                this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                return;
            }
        }
        if (flying) {
            this.handleRelativeFrictionAndCalculateMovement(travelVector, speed);
            this.move(MoverType.SELF, getDeltaMovement());
            this.setDeltaMovement(getDeltaMovement().multiply(0.91f, 0.91f, 0.91f));
            this.calculateEntityAnimation(this, false);
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        } else {
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            super.travel(travelVector);
        }
    }

    @Override
    public int tickTimer() {
        return tickCount;
    }

    public void updatePassengerPosition(Entity passenger) {
        this.updatePassengerPosition(passenger, Entity::setPos);
    }
    @Override
    public void tick() {
        if (this.hasCustomName()) {
            if (this.getCustomName().getString().equalsIgnoreCase("apathy") && this.getNadderType() != NadderType.ARATHAIN) {
                this.setNadderType(NadderType.ARATHAIN);
            }
        }
        super.tick();
    }

    public void updatePassengerPosition(Entity passenger, Entity.MoveFunction positionUpdater) {
        if (this.hasPassenger(passenger)) {
            double d = this.getY() + this.getPassengersRidingOffset() + passenger.getMyRidingOffset() + 0.2;
            positionUpdater.accept(passenger, this.getX(), d, this.getZ());
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    protected BodyRotationControl createBodyControl() {
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

        public boolean canUse() {
            return !DeadlyNadderEntity.this.isInWater() && DeadlyNadderEntity.this.getTarget() != null && DeadlyNadderEntity.this.canAttack(DeadlyNadderEntity.this.getTarget(), TargetingConditions.DEFAULT);
        }

        public void start() {
            this.startSwoop();
            DeadlyNadderEntity.this.push(0, 1, 0);
            DeadlyNadderEntity.this.setFlying(true);
        }

        public void stop() {
            DeadlyNadderEntity.this.circlingCenter = DeadlyNadderEntity.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, DeadlyNadderEntity.this.circlingCenter).above(10 + DeadlyNadderEntity.this.random.nextInt(20));
            DeadlyNadderEntity.this.setFlying(false);
        }

        private void startSwoop() {
            DeadlyNadderEntity.this.circlingCenter = DeadlyNadderEntity.this.getTarget().blockPosition().above(20 + DeadlyNadderEntity.this.random.nextInt(20));
            if (DeadlyNadderEntity.this.circlingCenter.getY() < DeadlyNadderEntity.this.level.getSeaLevel()) {
                DeadlyNadderEntity.this.circlingCenter = new BlockPos(DeadlyNadderEntity.this.circlingCenter.getX(), DeadlyNadderEntity.this.level.getSeaLevel() + 1, DeadlyNadderEntity.this.circlingCenter.getZ());
            }
        }
    }

    class SwoopMovementGoal extends MovementGoal {
        private SwoopMovementGoal() {
            super();
        }

        public boolean canUse() {
            return DeadlyNadderEntity.this.isFlying() && DeadlyNadderEntity.this.canFly() && DeadlyNadderEntity.this.getTarget() != null;
        }

        public boolean canContinueToUse() {
            LivingEntity livingEntity = DeadlyNadderEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (livingEntity instanceof Player && (livingEntity.isSpectator() || ((Player) livingEntity).isCreative())) {
                return false;
            } else if (!this.canUse()) {
                return false;
            } else {
                return DeadlyNadderEntity.this.isFlying();
            }
        }

        public void tick() {
            LivingEntity livingEntity = DeadlyNadderEntity.this.getTarget();
            DeadlyNadderEntity.this.targetPosition = new Vec3(livingEntity.getX(), livingEntity.getY(0.5D), livingEntity.getZ());
            if (DeadlyNadderEntity.this.getBoundingBox().inflate(0.20000000298023224D).intersects(livingEntity.getBoundingBox())) {
                DeadlyNadderEntity.this.canAttack(livingEntity);
                if (!DeadlyNadderEntity.this.isSilent()) {
                    level.playSound(null, DeadlyNadderEntity.this, SoundEvents.RAVAGER_ATTACK, DeadlyNadderEntity.this.getSoundSource(), DeadlyNadderEntity.this.getSoundVolume(), DeadlyNadderEntity.this.getVoicePitch());
                }
            }
        }
    }

    class FlyWithOwnerGoal extends MovementGoal {
        private FlyWithOwnerGoal() {
            super();
        }

        public boolean canUse() {
            return DeadlyNadderEntity.this.getOwner() != null && DeadlyNadderEntity.this.getOwner().isFallFlying() && !DeadlyNadderEntity.this.isInWater() && DeadlyNadderEntity.this.canFly();
        }

        public boolean canContinueToUse() {
            LivingEntity owner = DeadlyNadderEntity.this.getOwner();
            if (owner == null) {
                return false;
            } else if (!owner.isAlive()) {
                return false;
            } else if (owner instanceof Player && (owner.isSpectator() || ((Player) owner).isCreative())) {
                return false;
            } else if (!this.canUse()) {
                return false;
            } else {
                return DeadlyNadderEntity.this.isFlying();
            }
        }

        public void tick() {
            LivingEntity owner = DeadlyNadderEntity.this.getOwner();
            if (owner != null) {
                DeadlyNadderEntity.this.setFlying(true);
                DeadlyNadderEntity.this.targetPosition = new Vec3(owner.getX(), owner.getY(0.5D), owner.getZ());
            }
        }
    }

    abstract class MovementGoal extends Goal {
        public MovementGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        // todo - delete?
        protected boolean isNearTarget() {
            return DeadlyNadderEntity.this.targetPosition.distanceToSqr(DeadlyNadderEntity.this.getX(), DeadlyNadderEntity.this.getY(), DeadlyNadderEntity.this.getZ()) < 4.0D;
        }
    }

    class NadderLookControl extends LookControl {
        public NadderLookControl(Mob entity) {
            super(entity);
        }

        public void tick() {
            if (!DeadlyNadderEntity.this.isFlying()) {
                super.tick();
            }
        }
    }

    class NadderFlyingBodyControl extends BodyRotationControl {
        public NadderFlyingBodyControl(Mob entity) {
            super(entity);
        }

        public void clientTick() {
            DeadlyNadderEntity.this.yHeadRot = DeadlyNadderEntity.this.yBodyRot;
            DeadlyNadderEntity.this.yBodyRot = DeadlyNadderEntity.this.getYRot();
        }
    }

    class NadderMoveControl extends MoveControl {
        private float targetSpeed = 0.1F;

        public NadderMoveControl(Mob owner) {
            super(owner);
        }

        public void tick() {
            if (DeadlyNadderEntity.this.isVehicle()) {
                if (this.operation == MoveControl.Operation.MOVE_TO) {
                    this.operation = MoveControl.Operation.WAIT;
                    this.mob.setNoGravity(true);
                        double d = this.wantedX - this.mob.getX();
                    double e = this.wantedY - this.mob.getY();
                    double f = this.wantedZ - this.mob.getZ();
                    double g = d * d + e * e + f * f;
                    if (g < 2.500000277905201E-7) {
                        this.mob.setYya(0.0f);
                        this.mob.setZza(0.0f);
                        return;
                    }
                    float h = (float) (Mth.atan2(f, d) * 57.2957763671875) - 90.0f;
                    this.mob.setYRot(this.rotlerp(this.mob.getYRot(), h, 90.0f));
                    float i = this.mob.isOnGround() ? (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)) : (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                    this.mob.setSpeed(i);
                    double j = Math.sqrt(d * d + f * f);
                    if (Math.abs(e) > (double) 1.0E-5f || Math.abs(j) > (double) 1.0E-5f) {
                        this.mob.setYya(e > 0.0 ? i : -i);
                    }
                } else {
                    this.mob.setNoGravity(false);
                    this.mob.setYya(0.0f);
                    this.mob.setZza(0.0f);
                }
            } else {
                if (DeadlyNadderEntity.this.isFlying()) {
                    if (DeadlyNadderEntity.this.horizontalCollision) {
                        DeadlyNadderEntity var10000 = DeadlyNadderEntity.this;
                        var10000.setYRot(var10000.getYRot() + 180.0F);
                        this.targetSpeed = 0.1F;
                    }

                    if (DeadlyNadderEntity.this.targetPosition != null) {
                        float f = (float) (DeadlyNadderEntity.this.targetPosition.x - DeadlyNadderEntity.this.getX());
                        float g = (float) (DeadlyNadderEntity.this.targetPosition.y - DeadlyNadderEntity.this.getY());
                        float h = (float) (DeadlyNadderEntity.this.targetPosition.z - DeadlyNadderEntity.this.getZ());
                        double d = Mth.sqrt(f * f + h * h);
                        double e = 1.0D - (double) Mth.abs(g * 0.7F) / d;
                        f = (float) ((double) f * e);
                        h = (float) ((double) h * e);
                        d = Mth.sqrt(f * f + h * h);
                        double i = Mth.sqrt(f * f + h * h + g * g);
                        float j = DeadlyNadderEntity.this.getYRot();
                        float k = (float) Mth.atan2(h, f);
                        float l = Mth.wrapDegrees(DeadlyNadderEntity.this.getYRot() + 90.0F);
                        float m = Mth.wrapDegrees(k * 57.295776F);
                        DeadlyNadderEntity.this.setYRot(Mth.approachDegrees(l, m, 4.0F) - 90.0F);
                        DeadlyNadderEntity.this.yBodyRot = DeadlyNadderEntity.this.getYRot();
                        this.targetSpeed = Mth.approach(this.targetSpeed, 2.0F, 0.01F * (2.0F / this.targetSpeed));

                        float n = (float) (-(Mth.atan2(-g, d) * 57.2957763671875D));
                        DeadlyNadderEntity.this.setXRot(n);
                        float o = DeadlyNadderEntity.this.getYRot() + 90.0F;
                        double p = (double) (this.targetSpeed * Mth.cos(o * 0.017453292F)) * Math.abs((double) f / i);
                        double q = (double) (this.targetSpeed * Mth.sin(o * 0.017453292F)) * Math.abs((double) h / i);
                        double r = (double) (this.targetSpeed * Mth.sin(n * 0.017453292F)) * Math.abs((double) g / i);
                        Vec3 vec3d = DeadlyNadderEntity.this.getDeltaMovement();
                        DeadlyNadderEntity.this.setDeltaMovement(vec3d.add((new Vec3(p, r, q)).subtract(vec3d).multiply(0.05D, 0.05D, 0.05D)));
                    }
                } else {
                    super.tick();
                }
            }
        }
    }

}
