package coda.bookofdragons.common.entities.util.goal;

import coda.bookofdragons.common.entities.util.FlyingRideableDragonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FollowDriverGoal extends Goal {
    private final FlyingRideableDragonEntity user;
    private int time;
    private int timeToRecalcPath;

    public FollowDriverGoal(FlyingRideableDragonEntity user) {
        this.user = user;

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return this.user.previousDriver != null;
    }

    public boolean canContinueToUse() {
        return this.canUse() && !this.user.isLeashed() && !this.user.isPassenger() && this.user.distanceToSqr(this.user.previousDriver) < 100 && ++time < 120;
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
        this.user.getLookControl().setLookAt(following, 10.0F, (float) this.user.getMaxHeadXRot());
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            this.user.getNavigation().moveTo(following, 1.0F);
        }
    }
}