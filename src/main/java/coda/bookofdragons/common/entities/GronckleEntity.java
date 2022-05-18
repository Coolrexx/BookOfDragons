package coda.bookofdragons.common.entities;

import coda.bookofdragons.common.entities.util.FlyingRideableDragonEntity;
import coda.bookofdragons.registry.BODEntities;
import coda.bookofdragons.registry.BODItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Arrays;
import java.util.Comparator;

public class GronckleEntity extends FlyingRideableDragonEntity implements IAnimatable, IAnimationTickable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(GronckleEntity.class, EntityDataSerializers.INT);

    public GronckleEntity(EntityType<? extends GronckleEntity> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.MOVEMENT_SPEED, 0.25F).add(Attributes.FLYING_SPEED, 0.2F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredient.of(ItemTags.FISHES);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FISHES);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob ageableMob) {
        return BODEntities.GRONCKLE.get().create(world);
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(BODItems.GRONCKLE_SPAWN_EGG.get());
    }

    @Override
    protected float getStandingEyeHeight(Pose p_21131_, EntityDimensions p_21132_) {
        return 1.2F;
    }

    @Override
    public void positionRider(Entity passenger) {
        Vec3 pos = getYawVec(yBodyRot, 0.0F, -0.35F).add(getX(), getY() + 1.25F, getZ());
        passenger.setPos(pos.x, pos.y, pos.z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant().getId());
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_VARIANT, tag.getInt("Variant"));
    }

    public enum Variant {
        HERO(0),
        BLUE(1),
        GREEN(2),
        PURPLE(3);

        public static final GronckleEntity.Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(GronckleEntity.Variant::getId)).toArray((p_149255_) -> {
            return new GronckleEntity.Variant[p_149255_];
        });

        private final int id;
        public int getId() {
            return this.id;
        }
        private Variant(int p_149239_) {
            this.id = p_149239_;
        }
    }

    public GronckleEntity.Variant getVariant() {
        return GronckleEntity.Variant.BY_ID[this.entityData.get(DATA_VARIANT)];
    }

    private void setVariant(GronckleEntity.Variant p_149118_) {
        this.entityData.set(DATA_VARIANT, p_149118_.getId());
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (isFlying() && event.isMoving()) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.gronckle.fly", true));
            return PlayState.CONTINUE;
        }
        else if (isFlying() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.gronckle.fly_idle", true));
            return PlayState.CONTINUE;
        }
        else if (isOnGround() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.gronckle.walk", true));
            return PlayState.CONTINUE;
        }
        else if (isOnGround() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.gronckle.idle", true));
            return PlayState.CONTINUE;
        }
        else {
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public int tickTimer() {
        return tickCount;
    }
}
