package coda.bookofdragons.client.model;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.EelEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class EelModel extends AnimatedTickingGeoModel<EelEntity> {

    @Override
    public ResourceLocation getModelLocation(EelEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "geo/entity/eel.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EelEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/eel.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EelEntity animatable) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "animations/entity/eel.animation.json");
    }

    @Override
    public void setLivingAnimations(EelEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        IBone head = getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        if (entity.isInWater()) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 350F));
        }
        else {
            head.setRotationX(0);
            head.setRotationY(0);
        }
    }
}
