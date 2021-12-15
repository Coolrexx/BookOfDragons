package net.arathain.bookofdragons.common.entity.util;

import net.arathain.bookofdragons.BODComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractGlidingDragonEntity extends AbstractRideableDragonEntity{

    public AbstractGlidingDragonEntity(EntityType<? extends AbstractRideableDragonEntity> type, World world) {
        super(type, world);
    }
    private float roll;

   public float getZRoll() {
        return roll;
   }
   public void setRoll(float roll1) {
       roll = roll1;
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
            this.setYaw((float) this.serverYaw);
            this.setRotation(this.getYaw(), this.getPitch());
            this.setRoll(this.getZRoll() - passenger.sidewaysSpeed);
            this.bodyYaw = (float) this.serverYaw;

            if (!flying && isPlayerUpwardsMoving) this.jump();

            if (this.getControllingPassenger() != null) {
                travelVector = new Vec3d(Math.sin(roll), -this.getPitch() * 0.5 + Math.cos(roll), 3.4335 + passenger.forwardSpeed * 3.5);
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
}
