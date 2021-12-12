package net.arathain.bookofdragons.mixin;

import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    private int tickDelta = 0;
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void ticker(CallbackInfo ci) {
        if (!this.world.isClient && tickDelta > 39) {
            Vec3d pos = this.getPos();
            List<AbstractRideableDragonEntity> entities = Objects.requireNonNull(this.getEntityWorld()).getEntitiesByClass(AbstractRideableDragonEntity.class, new Box(pos.getX() - 32, pos.getY() - 32, pos.getZ() - 32, pos.getX() + 32, pos.getY() + 32, pos.getZ() + 32), (AbstractRideableDragonEntity) -> true);
            for (AbstractRideableDragonEntity nearbyEntity : entities) {
                if (nearbyEntity != null && Objects.equals(nearbyEntity.getOwner(), this)) {
                    if (nearbyEntity.distanceTo(this) > 6.0f) {
                        Path path = nearbyEntity.getNavigation().findPathTo(this, 10);
                        nearbyEntity.getNavigation().startMovingAlong(path, nearbyEntity.getMovementSpeed());
                        nearbyEntity.getNavigation().startMovingTo(this, nearbyEntity.getMovementSpeed());
                        nearbyEntity.setPositionTarget(this.getBlockPos(), 10);
                    }

                }
            }
            tickDelta = 0;
        }
        tickDelta++;
    }
}
