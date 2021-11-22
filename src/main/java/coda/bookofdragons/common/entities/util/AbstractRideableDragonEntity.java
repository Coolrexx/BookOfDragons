package coda.bookofdragons.common.entities.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public abstract class AbstractRideableDragonEntity extends AbstractFlyingDragonEntity {

    protected AbstractRideableDragonEntity(EntityType<? extends AbstractRideableDragonEntity> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    public abstract Ingredient getIngredient();
}
