package coda.bookofdragons.common.entities.util;

import coda.bookofdragons.common.entities.util.goal.FlyingDragonWanderGoal;
import coda.bookofdragons.init.BODItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public abstract class AbstractFlyingDragonEntity extends TamableAnimal implements FlyingAnimal {
    public static final Predicate<LivingEntity> NOT_HOLDING_EEL = (p_20436_) -> {
        return !p_20436_.getOffhandItem().is(BODItems.EEL.get()) || !p_20436_.getMainHandItem().is(BODItems.EEL.get());
    };

    protected AbstractFlyingDragonEntity(EntityType<? extends TamableAnimal> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingDragonMovementController(this, 20);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.25D, getIngredient(), false));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15, 1));
        this.goalSelector.addGoal(4, new FlyingDragonWanderGoal(this));
        //TODO - fix the eel thing
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.0D, 1.2D, NOT_HOLDING_EEL));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public abstract Ingredient getIngredient();

    @Override
    public boolean isFlying() {
        return isHighEnough((int) maxUpStep + 1);
    }

    public boolean isHighEnough(int altitude) {
        return this.getAltitude(altitude) >= altitude;
    }

    public double getAltitude(int limit) {
        BlockPos.MutableBlockPos mutable = this.blockPosition().mutable();

        // limit so we don't do dozens of iterations per tick
        for (int i = 0; i <= limit && mutable.getY() > 0 && !this.level.getBlockState(mutable.move(Direction.DOWN)).getMaterial().blocksMotion(); i++);

        return this.getY() - mutable.getY() - 1;
    }

    @Override
    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new FlyingPathNavigation(this, worldIn);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (!this.isAlive()) return;

        boolean flying = this.isFlying();
        float speed = (float) this.getAttributeValue(flying ? Attributes.FLYING_SPEED : Attributes.MOVEMENT_SPEED);

        if (flying) {
            this.moveRelative(speed, travelVector);
            this.move(MoverType.SELF, getDeltaMovement());
            this.setDeltaMovement(getDeltaMovement().scale(0.91f));
            this.calculateEntityAnimation(this, true);
        } else {
            super.travel(travelVector);
        }
    }

    private static class FlyingDragonMovementController extends MoveControl {
        private final int maxPitchChange;

        public FlyingDragonMovementController(AbstractFlyingDragonEntity dragon, int maxPitchChange) {
            super(dragon);
            this.maxPitchChange = maxPitchChange;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                this.operation = MoveControl.Operation.WAIT;
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedY - this.mob.getY();
                double d2 = this.wantedZ - this.mob.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double) 2.5000003E-7F) {
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    return;
                }

                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.mob.yRotO = this.rotlerp(this.mob.getYRot(), f, 90.0F);
                float f1;
                if (this.mob.isOnGround()) {
                    f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
                } else {
                    f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
                }

                this.mob.setSpeed(f1);
                double d4 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
                float f2 = (float) (-(Mth.atan2(d1, d4) * (double) (180F / (float) Math.PI)));
                this.mob.xRotO = this.rotlerp(this.mob.getXRot(), f2, (float) this.maxPitchChange);
                this.mob.setYya(d1 > 0.0D ? f1 : -f1);
            } else {
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }
        }
    }
}
