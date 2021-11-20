package coda.bookofdragons.client.model;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.TerribleTerrorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class TerribleTerrorModel extends AnimatedTickingGeoModel<TerribleTerrorEntity> {

    @Override
    public ResourceLocation getModelLocation(TerribleTerrorEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "geo/entity/eel.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(TerribleTerrorEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/eel.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TerribleTerrorEntity animatable) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "animations/entity/eel.animation.json");
    }

    @Override
    public void setLivingAnimations(TerribleTerrorEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        if (entity.isBaby()) {
            IBone head = this.getAnimationProcessor().getBone("head");
            head.setScaleX(0.6f);
            head.setScaleY(0.6f);
            head.setScaleZ(0.6f);
        }
    }
}
