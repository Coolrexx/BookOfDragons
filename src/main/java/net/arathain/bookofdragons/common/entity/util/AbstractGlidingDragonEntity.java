package net.arathain.bookofdragons.common.entity.util;

import net.arathain.bookofdragons.BODComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractGlidingDragonEntity extends AbstractRideableDragonEntity{

    public AbstractGlidingDragonEntity(EntityType<? extends AbstractRideableDragonEntity> type, World world) {
        super(type, world);
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
            this.headYaw = passenger.getHeadYaw();
            this.serverHeadYaw = this.headYaw;
            this.serverYaw = this.headYaw;
            boolean isPlayerUpwardsMoving = BODComponents.DRAGON_RIDER_COMPONENT.get(passenger).isPressingUp();
            boolean isPlayerDownwardsMoving = BODComponents.DRAGON_RIDER_COMPONENT.get(passenger).isPressingDown();
            this.serverPitch = passenger.getPitch();
            this.setPitch((float) this.serverPitch);
            this.setYaw((float) this.serverYaw + passenger.sidewaysSpeed);
            this.setRotation(this.getYaw(), this.getPitch());
            this.bodyYaw = (float) this.serverYaw;

            if (!flying && isPlayerUpwardsMoving) this.jump();

            if (this.getControllingPassenger() != null) {
                Vec3d velocity = this.getVelocity();
                Vec3d gravityVector = new Vec3d(0, 98, 0);
                travelVector = velocity.add(passenger.sidewaysSpeed, (-this.getPitch() * 1.5) + Math.cos(roll) * 98 - gravityVector.y, 100 + (passenger.forwardSpeed * Math.cos(getPitch()) * 1000 * 1000));
                this.setMovementSpeed(speed);
                this.stepBobbingAmount = 0;
                if (passenger.forwardSpeed < 0 && passenger.sidewaysSpeed == 0 && !isPlayerUpwardsMoving && !isPlayerDownwardsMoving) {
                    this.setPitch(passenger.getPitch());
                    this.setYaw(passenger.getHeadYaw());
                    this.setRotation(this.getYaw(), this.getPitch());
                    travelVector = new Vec3d(0, 0, 0);
                }
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
}
