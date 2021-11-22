package coda.bookofdragons.common.entities.util.goal;

import coda.bookofdragons.common.entities.util.AbstractFlyingDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

// Taken from Wolf, thanks Wolf :)
public class FlyingDragonWanderGoal extends RandomStrollGoal {
    private final AbstractFlyingDragonEntity dragon;
    private final int distance;

    public FlyingDragonWanderGoal(AbstractFlyingDragonEntity dragon, int distance) {
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
        }
        else {
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
        if (dragon.closerThan(dragon, 1)) {
            Vec3 vec31 = Vec3.atCenterOf(dragon.blockPosition());
            vec3 = vec31.subtract(dragon.position()).normalize();
        } else {
            vec3 = dragon.getViewVector(0.0F);
        }

        Vec3 vec32 = HoverRandomPos.getPos(dragon, 8 * distance, 7 * distance, vec3.x * distance, vec3.z * distance, ((float)Math.PI / 2F), 3, 1);
        return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(dragon, 8 * distance, 4 * distance, -2 * distance, vec3.x * distance, vec3.z * distance, (float)Math.PI / 2F);
    }

    public BlockPos getBlockUnder(PathfinderMob mob) {
        final BlockPos.MutableBlockPos position = mob.blockPosition().mutable();
        BlockState state = mob.level.getBlockState(position);
        while (state.isAir() && state.getFluidState().isEmpty()) {
            position.move(Direction.DOWN);
            if (position.getY() <= 0) return null;
        }
        return position;
    }

    /*@Override
    public boolean canUse() {
        if (dragon.isVehicle()) {
            return false;
        }
        if (dragon.level.random.nextInt(3) == 0) {
            boolean flying = dragon.isFlying();
            boolean grounded = !flying || dragon.tickCount <= 25;
            LivingEntity attackTarget = dragon.getTarget();
            if (attackTarget == null) {
                if (!grounded && ticksAfloat >= flyTime) {
                    target = null;
                }
                if (target == null) {
                    if (!flying || dragon.level.random.nextInt(10) == 0) {
                        if (!grounded) ++ticksAfloat;
                        boolean land = (grounded && dragon.level.random.nextFloat() >= 0.05f) || ticksAfloat >= 300 && dragon.level.random.nextFloat() <= 0.7f;
                        Vec3 target = getTargetPosition(land);
                        if (target != null) {
                            this.target = target;
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void start() {
        move(target, 0.6);
    }

    @Override
    public void stop() {
        target = null;
        flyTime = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && dragon.distanceToSqr(target) > 8 && !dragon.isVehicle();
    }

    @Override
    public void tick() {
        if (canContinueToUse()) {
            final BlockPos position = dragon.blockPosition();
            final double xDistance = dragon.getX() - target.x;
            final double zDistance = dragon.getZ() - target.z;
            if (dragon.isFlying() && xDistance * xDistance + zDistance * zDistance >= 64 && position.getY() - dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, position).getY() < 4) {
                dragon.setDeltaMovement(dragon.getDeltaMovement().x(), 0.4, dragon.getDeltaMovement().z());
            }
            move(target, 0.6);
        }
        if (dragon.isFlying()) {
            dragon.setDeltaMovement(dragon.getDeltaMovement().multiply(1.05, 1.05, 1.05));
        }
    }

    private void move(Vec3 position, double speed) {
        dragon.getMoveControl().setWantedPosition(position.x(), position.y(), position.z(), speed);
        if (target.y() - dragon.getY() < 0) {
            dragon.setDeltaMovement(dragon.getDeltaMovement().add(0, Math.max(target.y() - dragon.getY(), -0.16), 0));
        }
    }

    @Nullable
    private Vec3 getTargetPosition(boolean land) {
        if (dragon.isInWaterOrBubble()) {
            Vec3 vec3d = DefaultRandomPos.getPos(dragon, 15, 7);
            ticksAfloat = 0;
            return vec3d == null ? DefaultRandomPos.getPos(dragon, 10, 7) : vec3d;
        }

        if (!land) {
            flyTime = dragon.level.random.nextInt(1800) + 600;
            return getAirPosition();
        }

        ticksAfloat = 0;
        return DefaultRandomPos.getPos(dragon, 10, 7);
    }

    private Vec3 getAirPosition() {
        BlockPos pos = dragon.blockPosition().above(dragon.getY() > dragon.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) dragon.getX(), (int) dragon.getZ()) + 20 ? 0 : dragon.level.random.nextInt(12) + 12);
        int original = pos.getY();
        while (pos.getY() <= original - 8 && !dragon.level.isEmptyBlock(pos)) {
            pos = pos.below();
        }

        if (pos.getY() == original - 8) {
            pos = pos.above(8);

            while (pos.getY() <= original + 8 && !dragon.level.isEmptyBlock(pos)) {
                pos = pos.above();
            }

            if (pos.getY() == original + 8) return null;
        }

        LivingEntity owner = dragon.getOwner();
        double ownerDistanceX = 0;
        double ownerDistanceZ = 0;
        if (owner != null) {
            ownerDistanceX = owner.getX() - dragon.getX();
            ownerDistanceZ = owner.getZ() - dragon.getZ();
        }
        int xDistance = Math.abs(ownerDistanceX) > 32 ? (int) ownerDistanceX : dragon.level.random.nextInt(32) + 64;
        int zDistance = Math.abs(ownerDistanceZ) > 32 ? (int) ownerDistanceZ : dragon.level.random.nextInt(32) + 64;
        double rotation = Math.toRadians(ownerDistanceX * ownerDistanceX + ownerDistanceZ * ownerDistanceZ > 1024 ? Mth.atan2(ownerDistanceZ, ownerDistanceX) + 90 : dragon.level.random.nextInt(361));
        return new Vec3(pos.getX() + Math.sin(rotation) * xDistance, pos.getY(), pos.getZ() + Math.cos(-rotation) * zDistance);
    }*/
}
