package coda.bookofdragons.client.model;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.GronckleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class GronckleModel extends AnimatedTickingGeoModel<GronckleEntity> {

    @Override
    public ResourceLocation getModelLocation(GronckleEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "geo/entity/gronckle.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(GronckleEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/gronckle.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(GronckleEntity animatable) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "animations/entity/gronckle.animation.json");
    }

    @Override
    public void setLivingAnimations(GronckleEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
/*
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        IBone neck = this.getAnimationProcessor().getBone("neck");
        IBone body = this.getAnimationProcessor().getBone("body");

        neck.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        neck.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));

        if (entity.isBaby()) {
            body.setScaleX(0.7f);
            body.setScaleY(0.7f);
            body.setScaleZ(0.7f);
            body.setPositionY(-2F);
        }
*/

    }
}