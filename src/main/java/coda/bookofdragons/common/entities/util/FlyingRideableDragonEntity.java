package coda.bookofdragons.common.entities.util;

import coda.bookofdragons.common.entities.EelEntity;
import coda.bookofdragons.common.entities.util.goal.FlyingDragonWanderGoal;
import coda.bookofdragons.common.entities.util.goal.FollowDriverGoal;
import coda.bookofdragons.registry.BODKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

// Stolen from Wolf, thanks Wolf :)
public abstract class FlyingRideableDragonEntity extends TamableAnimal implements FlyingAnimal, Saddleable {
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(FlyingRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(FlyingRideableDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private final Map<UUID, AtomicInteger> players = new HashMap<>();
    private boolean shotDown;
    public Entity previousDriver = null;

    protected FlyingRideableDragonEntity(EntityType<? extends TamableAnimal> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new MoveController();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.0F, getIngredient(), true));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15, 1));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new FollowDriverGoal(this));
        this.goalSelector.addGoal(4, new FlyingDragonWanderGoal(this, 150));
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, EelEntity.class, 8.0F, 1.0D, 1.2D));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isTame() && getIngredient().test(stack) && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.level.broadcastEntityEvent(this, (byte)7);
        }

        if (isTame() && isOwnedBy(player)) {
            if (stack.getItem() == Items.SADDLE && !hasSaddle()) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                this.entityData.set(SADDLED, true);
                return InteractionResult.SUCCESS;
            } else if (hasSaddle()) {
                if (stack.getItem() == Items.SHEARS) {
                    this.entityData.set(SADDLED, false);
                    spawnAtLocation(new ItemStack(Items.SADDLE));
                } else {
                    player.startRiding(this);
                    this.navigation.stop();
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    public abstract Ingredient getIngredient();

    @Override
    protected void dropEquipment() {
        if (hasSaddle()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
        this.entityData.define(FLYING, false);
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new FlyingPathNavigation(this, worldIn);
    }

    public boolean isFlying() {
        return !isShotDown() && isHighEnough(3);
    }

    public void setFlying(boolean flying) {
        this.entityData.define(FLYING, flying);
    }

    public boolean hasSaddle() {
        return this.entityData.get(SADDLED);
    }

    @Override
    public boolean isSaddleable() {
        return true;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        this.setSaddled(true);
        if (p_21748_ != null) {
            this.level.playSound(null, this, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }
    }

    public boolean isSaddled() {
        return this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean p_30505_) {
        this.entityData.set(SADDLED, p_30505_);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }


    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity && this.isOwnedBy((LivingEntity) this.getControllingPassenger());
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (getControllingPassenger() == passenger) {
            this.previousDriver = passenger;
        }
        super.removePassenger(passenger);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isFlying() && source.isProjectile() && !source.getMsgId().equals("thrown")) {
            setShotDown(true);
        }
        if (level.getDifficulty() != Difficulty.PEACEFUL && source.getEntity() instanceof LivingEntity) {
            setTarget((LivingEntity) source.getEntity());
        }
        return super.hurt(source, amount);
    }

    public static Vec3 getYawVec(float yaw, double xOffset, double zOffset) {
        return new Vec3(xOffset, 0, zOffset).yRot(-yaw * ((float) Math.PI / 180f));
    }

    @Override
    public void travel(Vec3 vec3d) {
        boolean flying = this.isFlying();
        float speed = (float) this.getAttributeValue(flying ? Attributes.FLYING_SPEED : Attributes.MOVEMENT_SPEED);

        setShotDown(shotDown && !onGround);

        if (canBeControlledByRider()) {
            LivingEntity entity = (LivingEntity) getControllingPassenger();
            double moveX = entity.xxa * 0.5;
            double moveY = vec3d.y;
            double moveZ = entity.zza;

            yHeadRot = entity.yHeadRot;
            xRot = entity.xRot * 0.65f;
            yRot = Mth.rotateIfNecessary(yHeadRot, yRot, isFlying() ? 5 : 7);

            if (isControlledByLocalInstance()) {
                if (isFlying()) {
                    moveX = vec3d.x;
                    moveY = Minecraft.getInstance().options.keyJump.isDown() ? 0.5F : BODKeyBindings.DRAGON_DESCEND.isDown() ? -0.5 : 0F;
                    moveZ = moveZ > 0 ? moveZ : 0;
                    setSpeed(speed * 0.005F);
                }
                else {
                    speed *= 0.225f;
                    if (entity.jumping) {
                        flying = true;
                        jumpFromGround();
                    }
                }

                vec3d = new Vec3(moveX, moveY, moveZ);
                setSpeed(speed);
            }
            else if (entity instanceof Player) {
                calculateEntityAnimation(this, true);
                setDeltaMovement(Vec3.ZERO);
                if (!level.isClientSide && isFlying())
                    ((ServerPlayer) entity).connection.aboveGroundVehicleTickCount = 0;
                return;
            }
        }
        if (flying) {
            this.moveRelative(speed, vec3d);
            this.move(MoverType.SELF, getDeltaMovement());
            this.setSpeed(getSpeed() * 0.15F);
            this.setDeltaMovement(getDeltaMovement().scale(0.91f));
            this.calculateEntityAnimation(this, true);
        }
        else {
            super.travel(vec3d);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Saddle", this.entityData.get(SADDLED));
        if (!players.isEmpty()) {
            ListTag list = new ListTag();
            for (Map.Entry<UUID, AtomicInteger> entry : players.entrySet()) {
                CompoundTag nbt = new CompoundTag();
                nbt.putUUID("UUID", entry.getKey());
                nbt.putInt("Value", entry.getValue().get());
                list.add(nbt);
            }
            compound.put("Players", list);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(SADDLED, compound.getBoolean("Saddle"));
        if (compound.contains("Players")) {
            ListTag list = compound.getList("Players", 10);
            for (Tag entry : list) {
                CompoundTag nbt = (CompoundTag) entry;
                players.put(nbt.getUUID("UUID"), new AtomicInteger(nbt.getInt("Values")));
            }
        }
    }

    public void setShotDown(boolean shotDown) {
        this.shotDown = shotDown;
    }

    public boolean isShotDown() {
        return shotDown;
    }

    public boolean isHighEnough(int altitude) {
        return this.getAltitude(altitude) >= altitude;
    }

    public double getAltitude() {
        return this.getAltitude(level.getHeight());
    }

    public double getAltitude(int limit) {
        BlockPos.MutableBlockPos pointer = blockPosition().mutable();
        int i = 0;

        while(i <= limit && pointer.getY() > level.dimensionType().minY() && !level.getBlockState(pointer).getMaterial().isSolid())
        {
            ++i;
            pointer.setY(blockPosition().getY() - i);
        }

        return i;
    }

    private class MoveController extends MoveControl {

        public MoveController() {
            super(FlyingRideableDragonEntity.this);
        }

        @Override
        public void tick() {
            // original movement behavior if the entity isn't flying
            if (!isFlying()) {
                super.tick();
                return;
            }

            if (this.operation == MoveControl.Operation.MOVE_TO) {
                this.operation = MoveControl.Operation.WAIT;
                double xDif = wantedX - mob.getX();
                double yDif = wantedY - mob.getY();
                double zDif = wantedZ - mob.getZ();
                double sqd = xDif * xDif + yDif * yDif + zDif * zDif;
                if (sqd < (double) 2.5000003E-7F) {
                    mob.setYya(0.0F);
                    mob.setZza(0.0F);
                    return;
                }

                double root = Mth.sqrt((float) (xDif * xDif + zDif * zDif));
                float xDir = (float) (-(Mth.atan2(yDif, root) * (double) (180F / (float) Math.PI)));
                float yDir = (float) (Mth.atan2(zDif, xDif) * (double) (180F / (float) Math.PI)) - 90.0F;
                mob.yRot = rotlerp(mob.yRot, yDir, 90.0F);
                mob.xRot = rotlerp(mob.xRot, xDir, 5f);

                float speed = (float) (speedModifier * mob.getAttributeValue(Attributes.FLYING_SPEED));
                mob.setSpeed(speed);
                mob.setYya(yDif > 0.0D ? speed : -speed);
            } else {
                mob.setYya(0.0F);
                mob.setZza(0.0F);
            }
        }
    }
}

