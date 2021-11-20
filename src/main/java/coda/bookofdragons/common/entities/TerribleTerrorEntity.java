package coda.bookofdragons.common.entities;

import coda.bookofdragons.common.entities.util.AbstractFlyingDragonEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

// quite a bit of this is from Wolf, thank you Wolf
public class TerribleTerrorEntity extends AbstractFlyingDragonEntity implements FlyingAnimal, IAnimatable, IAnimationTickable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public TerribleTerrorEntity(EntityType<? extends TamableAnimal> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.225F).add(Attributes.FLYING_SPEED, 0.2F);
    }

    @Override
    public Ingredient getIngredient() {
        return Ingredient.of(ItemTags.FISHES);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (isInWater() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.swim", true));
            return PlayState.CONTINUE;
        }
        else if (isInWater() && !event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.water_idle", true));
            return PlayState.CONTINUE;
        }
        else if (!isInWater() && event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.slither", true));
            return PlayState.CONTINUE;
        }
        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.eel.land_idle", true));
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
