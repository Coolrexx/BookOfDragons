package coda.bookofdragons.common.entities.util;

import coda.bookofdragons.common.entities.EelEntity;
import coda.bookofdragons.init.BODItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public abstract class AbstractFlyingDragonEntity extends TamableAnimal implements FlyingAnimal {
    public static final Predicate<LivingEntity> NOT_HOLDING_EEL = (p_20436_) -> !p_20436_.getOffhandItem().is(BODItems.EEL.get()) || !p_20436_.getMainHandItem().is(BODItems.EEL.get());

    public AbstractFlyingDragonEntity(EntityType<? extends AbstractFlyingDragonEntity> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new TemptGoal(this, 1.25D, getIngredient(), false));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 15, 1));
        //TODO - fix the eel thing
        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, EelEntity.class, 8.0F, 1.0D, 1.2D));
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

        return this.getY() - mutable.getY() - 0.51;
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
}
