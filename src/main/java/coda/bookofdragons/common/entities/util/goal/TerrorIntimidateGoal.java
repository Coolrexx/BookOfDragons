package coda.bookofdragons.common.entities.util.goal;

import coda.bookofdragons.common.entities.TerribleTerrorEntity;
import coda.bookofdragons.common.entities.util.FlyingRideableDragonEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TerrorIntimidateGoal extends Goal {
    public TerribleTerrorEntity entity;
    private static final TargetingConditions SNAP_AT_TARGETTING = TargetingConditions.forNonCombat().range(15.0D).ignoreLineOfSight();
    public int coolDown;
    public int timer;
    public boolean canSnap;

    public TerrorIntimidateGoal(TerribleTerrorEntity entity){
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        List<? extends FlyingRideableDragonEntity> list = this.entity.level.getNearbyEntities(FlyingRideableDragonEntity.class, SNAP_AT_TARGETTING, this.entity, this.entity.getBoundingBox().inflate(32.0D));
        return !list.isEmpty();
    }

    @Override
    public void start() {
        super.start();
        this.timer = 0;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.coolDown < 60){
            this.coolDown++;
            this.entity.setSnapping(false);
        }
        else {
            List<? extends FlyingRideableDragonEntity> list = this.entity.level.getNearbyEntities(FlyingRideableDragonEntity.class, SNAP_AT_TARGETTING, this.entity, this.entity.getBoundingBox().inflate(32.0D));
                if(!(list.get(0) instanceof TerribleTerrorEntity)) {
                    if(this.entity.distanceToSqr(list.get(0)) > 20D && !this.entity.getSnapping()){
                        this.entity.getNavigation().moveTo(list.get(0), 1f);
                    }
                    else {
                        this.entity.getLookControl().setLookAt(list.get(0));
                        //this.entity.getNavigation().moveTo(list.get(0), 1.2f);
                        this.entity.setSnapping(false);
                        if (this.timer < 10) {
                            this.entity.getNavigation().stop();
                            this.timer++;
                            if (this.timer == 4) {
                                Vec3 lookVec = this.entity.getViewVector(1.0f);
                                this.entity.setDeltaMovement(lookVec.x() * -0.3f, 0.2f, lookVec.z() * -0.3f);
                            }
                            this.entity.setSnapping(true);
                        } else {
                            this.entity.setSnapping(false);
                            this.canSnap = false;
                            this.stop();
                        }
                    }
                }
            else{
                this.entity.setSnapping(false);
                this.timer = 0;
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.coolDown = 0;
        this.timer = 0;
    }
}
