package net.arathain.bookofdragons.common.entity.goal;

import net.arathain.bookofdragons.common.entity.EelEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class EelLeapAtTargetGoal extends Goal {
    private final EelEntity eel;
    private LivingEntity target;
    private final float yd;

    public EelLeapAtTargetGoal(EelEntity eelEntity, float v) {
        this.eel = eelEntity;
        this.yd = v;
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean shouldContinue() {
        return !this.eel.isOnGround();
    }

    @Override
    public void tick() {
        super.tick();

        eel.lookAtEntity(target, 90.0F, 90.0F);
    }

    @Override
    public boolean canStart() {
        if (!this.eel.isInsideWaterOrBubbleColumn()) {
            return false;
        } else {
            this.target = this.eel.getTarget();
            if (target == null) {
                return false;
            } else {
                double d0 = this.eel.squaredDistanceTo(this.target);
                return !(d0 < 4.0D) && !(d0 > 16.0D);
            }
        }
    }

    public void start() {
        Vec3d vec3 = this.eel.getVelocity();
        Vec3d vec31 = new Vec3d(this.target.getX() - this.eel.getX(), 0.0D, this.target.getZ() - this.eel.getZ());
        if (vec31.getX() > 1.0E-7D) {
            vec31 = vec31.normalize().multiply(0.4D).add(vec3.multiply(0.2D));
        }

        this.eel.setVelocity(vec31.x, this.yd, vec31.z);
    }
}
