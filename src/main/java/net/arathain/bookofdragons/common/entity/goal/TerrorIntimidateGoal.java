package net.arathain.bookofdragons.common.entity.goal;

import net.arathain.bookofdragons.common.entity.TerribleTerrorEntity;
import net.arathain.bookofdragons.common.entity.util.AbstractFlyingDragonEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TerrorIntimidateGoal extends Goal {
    public TerribleTerrorEntity entity;
    private static final TargetPredicate SNAP_AT_TARGETTING = TargetPredicate.createNonAttackable().setBaseMaxDistance(15.0D).ignoreVisibility();
    public int coolDown;
    public int timer;
    public boolean canSnap;

    public TerrorIntimidateGoal(TerribleTerrorEntity entity){
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        List<? extends AbstractFlyingDragonEntity> list = this.entity.world.getEntitiesByClass(AbstractFlyingDragonEntity.class, this.entity.getBoundingBox().expand(32.0D), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
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
            List<? extends AbstractFlyingDragonEntity> list = this.entity.world.getEntitiesByClass(AbstractFlyingDragonEntity.class, this.entity.getBoundingBox().expand(32.0D), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
            if(!(list.get(0) instanceof TerribleTerrorEntity)) {
                if(this.entity.squaredDistanceTo(list.get(0)) > 20D && !this.entity.getSnapping()){
                    this.entity.getNavigation().startMovingTo(list.get(0), 1f);
                }
                else {
                    this.entity.getLookControl().lookAt(list.get(0));
                    this.entity.setSnapping(false);
                    if (this.timer < 10) {
                        this.entity.getNavigation().stop();
                        this.timer++;
                        if (this.timer == 4) {
                            Vec3d lookVec = this.entity.getRotationVec(1.0f);
                            this.entity.setVelocity(lookVec.x * -0.3f, 0.2f, lookVec.z * -0.3f);
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
