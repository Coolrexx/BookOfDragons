package coda.bookofdragons.common.entities.util;

import coda.bookofdragons.client.ClientEvents;
import coda.bookofdragons.common.entities.util.goal.FollowDriverGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class AbstractRideableDragonEntity extends AbstractFlyingDragonEntity {
    public Entity previousDriver = null;

    public AbstractRideableDragonEntity(EntityType<? extends AbstractRideableDragonEntity> type, Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new FollowDriverGoal(this));
    }

    protected void removePassenger(Entity passenger) {
        if (getControllingPassenger() == passenger) {
            this.previousDriver = passenger;
        }
        super.removePassenger(passenger);
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).is(Items.STICK)) {
            player.startRiding(this);
            this.navigation.stop();
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void travel(Vec3 travelVector) {
        boolean flying = this.isFlying();
        float speed = (float) this.getAttributeValue(flying ? Attributes.FLYING_SPEED : Attributes.MOVEMENT_SPEED);

        if (this.canBeControlledByRider()) {
            LivingEntity passenger = (LivingEntity) this.getControllingPassenger();
            if (passenger != null) {

                this.yRot = passenger.yRot;
                this.yRotO = this.yRot;
                this.xRot = passenger.xRot * 0.5F;
                this.setRot(this.yRot, this.xRot);
                this.yBodyRot = this.yRot;
                this.yHeadRot = this.yRot;

                if (!flying && passenger.jumping) jumpFromGround();

                if (this.isControlledByLocalInstance()) {
                    travelVector = new Vec3(passenger.xxa * 0.25, ClientEvents.getFlightDelta(), passenger.zza * 0.25);
                    this.setSpeed(speed);
                    this.lerpSteps = 0;
                } else if (passenger instanceof Player) {
                    this.calculateEntityAnimation(this, flying);
                    this.setDeltaMovement(Vec3.ZERO);
                    if (!level.isClientSide && !isOnGround())
                        ((ServerPlayer) passenger).connection.aboveGroundVehicleTickCount = 0;
                    return;
                }
            }
        }
        if (flying) {
            this.moveRelative(speed, travelVector);
            this.move(MoverType.SELF, getDeltaMovement());
            this.setDeltaMovement(getDeltaMovement().scale(0.91f));
            this.calculateEntityAnimation(this, true);
        }
        else {
            super.travel(travelVector);
        }
    }
}
