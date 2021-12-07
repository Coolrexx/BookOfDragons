package net.arathain.bookofdragons.common.entity.util;

import net.arathain.bookofdragons.common.entity.EelEntity;
import net.arathain.bookofdragons.common.init.BODObjects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Predicate;

public abstract class AbstractFlyingDragonEntity extends TameableEntity implements Flutterer {
    public static final Predicate<LivingEntity> NOT_HOLDING_EEL = (entity) -> !entity.getOffHandStack().isOf(BODObjects.EEL) || !entity.getMainHandStack().isOf(BODObjects.EEL);

    public AbstractFlyingDragonEntity(EntityType<? extends AbstractFlyingDragonEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 20, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new FlyGoal(this, 1.0D));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(1, new TemptGoal(this, 1.25D, getIngredient(), false));
        this.goalSelector.add(2, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 15, 1));
        //TODO - fix the eel thing
        this.goalSelector.add(5, new FleeEntityGoal<>(this, EelEntity.class, 8.0F, 1.0D, 1.2D));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    public abstract Ingredient getIngredient();

    @Override
    public boolean isInAir() {
        return isHighEnough((int) stepHeight + 1);
    }

    public boolean isHighEnough(int altitude) {
        return this.getAltitude(altitude) >= altitude;
    }

    public double getAltitude(int limit) {
        BlockPos.Mutable mutable = this.getBlockPos().mutableCopy();

        // limit so we don't do dozens of iterations per tick
        for (int i = 0; i <= limit && mutable.getY() > 0 && !this.world.getBlockState(mutable.move(Direction.DOWN)).getMaterial().blocksMovement(); i++);
        return this.getY() - mutable.getY() - 0.11;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {

    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }
}
