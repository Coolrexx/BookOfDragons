package coda.bookofdragons.common.entities.util.goal;

import coda.bookofdragons.common.entities.EelEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EelLeapAtTargetGoal extends Goal {
    private final EelEntity eel;
    private LivingEntity target;
    private final float yd;

    public EelLeapAtTargetGoal(EelEntity eel, float yd) {
        this.eel = eel;
        this.yd = yd;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (this.eel.isVehicle() || this.eel.isInWater()) {
            return false;
        } else {
            this.target = this.eel.getTarget();
            if (target == null) {
                return false;
            } else {
                double d0 = this.eel.distanceToSqr(this.target);
                return !(d0 < 4.0D) && !(d0 > 16.0D);
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !eel.isOnGround();
    }

    @Override
    public void tick() {
        super.tick();

        eel.lookAt(target, 90.0F, 90.0F);
    }

    public void start() {
        Vec3 vec3 = this.eel.getDeltaMovement();
        Vec3 vec31 = new Vec3(this.target.getX() - this.eel.getX(), 0.0D, this.target.getZ() - this.eel.getZ());
        if (vec31.lengthSqr() > 1.0E-7D) {
            vec31 = vec31.normalize().scale(0.4D).add(vec3.scale(0.2D));
        }

        this.eel.setDeltaMovement(vec31.x, this.yd, vec31.z);
    }
}
