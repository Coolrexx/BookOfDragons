package coda.bookofdragons.common.entities.util.goal;

import coda.bookofdragons.common.entities.util.FlyingRideableDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

// Taken from Wolf, thanks Wolf :)
public class FlyingDragonWanderGoal extends RandomStrollGoal {
    private final FlyingRideableDragonEntity dragon;
    private final int distance;

    public FlyingDragonWanderGoal(FlyingRideableDragonEntity dragon, int distance) {
        super(dragon, 1.0F);
        this.dragon = dragon;
        this.distance = distance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (this.dragon.isVehicle()) {
            return false;
        } else {
            if (!this.forceTrigger) {
                if (this.dragon.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.dragon.getRandom().nextInt(this.interval) != 0) {
                    return false;
                }
            }

            Vec3 vec3 = this.findPos();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    public boolean canContinueToUse() {
        boolean flag = !this.dragon.getNavigation().isDone() && !this.dragon.isVehicle();
        BlockPos pos = getBlockUnder(dragon);

        if (pos != null) {
            if (dragon.level.getBlockState(pos).is(Blocks.WATER)) {
                return true;
            } else {
                return flag;
            }
        } else {
            return flag;
        }
    }

    public void start() {
        this.dragon.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    public void stop() {
        this.dragon.getNavigation().stop();
        super.stop();
    }

    @Nullable
    private Vec3 findPos() {
        Vec3 vec3;
        if (dragon.closerThan(dragon, 22)) {
            Vec3 vec31 = Vec3.atCenterOf(dragon.blockPosition());
            vec3 = vec31.subtract(dragon.position()).normalize();
        } else {
            vec3 = dragon.getViewVector(1.5F);
        }

        Vec3 vector3d2 = AirAndWaterRandomPos.getPos(dragon, 8 * distance, 7 * distance, 8 * distance, ((float)Math.PI / 2F), 2 * distance, 1);
        return vector3d2 != null ? vector3d2 : AirRandomPos.getPosTowards(dragon, 8 * distance, 4 * distance, -2 * distance, vec3, (float) Math.PI / 2F);
    }

    public BlockPos getBlockUnder(LivingEntity mob) {
        final BlockPos.MutableBlockPos position = mob.blockPosition().mutable();
        BlockState state = mob.level.getBlockState(position);
        while (state.isAir() && state.getFluidState().isEmpty()) {
            position.move(Direction.DOWN);
            if (position.getY() <= 0) return null;
        }
        return position;
    }
}