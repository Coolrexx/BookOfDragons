package net.arathain.bookofdragons.common.entity.goal;

import net.arathain.bookofdragons.common.entity.util.AbstractFlyingDragonEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FlyingDragonWanderGoal extends WanderAroundFarGoal {
    private final AbstractFlyingDragonEntity dragon;
    private final int distance;

    public FlyingDragonWanderGoal(AbstractFlyingDragonEntity dragon, int distance) {
        super(dragon, 1.0F);
        this.dragon = dragon;
        this.distance = distance;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canUse() {
        if (this.dragon.hasPassengers()) {
            return false;
        } else {
            if (!this.ignoringChance) {
                if (this.dragon.getLastAttackedTime() >= 100) {
                    return false;
                }

                if (this.dragon.getRandom().nextInt(this.chance) != 0) {
                    return false;
                }
            }

            Vec3d vec3 = this.findPos();
            if (vec3 == null) {
                return false;
            } else {
                this.targetX = vec3.x;
                this.targetY = vec3.y;
                this.targetZ = vec3.z;
                this.ignoringChance = false;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        boolean flag = !this.dragon.getNavigation().isIdle() && !this.dragon.hasPassengers();
        BlockPos pos = getBlockUnder(dragon);

        if (pos != null) {
            if (dragon.world.getBlockState(pos).isOf(Blocks.WATER)) {
                return true;
            } else {
                return flag;
            }
        }
        else {
            return flag;
        }
    }

    public void start() {
        this.dragon.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    public void stop() {
        this.dragon.getNavigation().stop();
        super.stop();
    }

    @Nullable
    private Vec3d findPos() {
        Vec3d vec3;
        if (dragon.distanceTo(dragon) < 22) {
            Vec3d vec31 = Vec3d.ofCenter(dragon.getBlockPos());
            vec3 = vec31.subtract(dragon.getPos()).normalize();
        } else {
            vec3 = dragon.getCameraPosVec(1.5F);
        }

        Vec3d vec32 = AboveGroundTargeting.find(dragon, 8 * distance, 7 * distance, vec3.x * distance, vec3.z * distance, ((float)Math.PI / 2F), 3, 1);
        return vec32 != null ? vec32 : FuzzyTargeting.findFrom(dragon, 8 * distance, 4 * distance, vec3);
    }

    public BlockPos getBlockUnder(PathAwareEntity mob) {
        final BlockPos.Mutable position = mob.getBlockPos().mutableCopy();
        BlockState state = mob.world.getBlockState(position);
        while (state.isAir() && state.getFluidState().isEmpty()) {
            position.move(Direction.DOWN);
            if (position.getY() <= 0) return null;
        }
        return position;
    }
}
