package net.arathain.bookofdragons.common.entity.goal;

import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FollowDriverGoal extends Goal {
    private final AbstractRideableDragonEntity user;
    private int time;
    private int timeToRecalcPath;

    public FollowDriverGoal(AbstractRideableDragonEntity user) {
        this.user = user;

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        return this.user.previousDriver != null;
    }

    public boolean shouldContinue() {
        return this.canStart() && !this.user.isLeashed() && this.user.squaredDistanceTo(this.user.previousDriver) < 100 && ++time < 120;
    }

    public void start() {
        this.time = 0;
        this.timeToRecalcPath = 0;
    }

    public void stop() {
        this.user.previousDriver = null;
        this.user.getNavigation().stop();
    }

    public void tick() {
        Entity following = this.user.previousDriver;
        this.user.getLookControl().lookAt(following, 10.0F, (float) this.user.getMaxHeadRotation());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            this.user.getNavigation().startMovingTo(following, 1.0F);
        }
    }
}
