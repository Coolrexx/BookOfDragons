package net.arathain.bookofdragons.client.model;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.EelEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class EelModel extends AnimatedGeoModel<EelEntity> {
    @Override
    public Identifier getModelLocation(EelEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "geo/entity/eel.geo.json");
    }

    @Override
    public Identifier getTextureLocation(EelEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "textures/entity/eel.png");
    }

    @Override
    public Identifier getAnimationFileLocation(EelEntity animatable) {
        return new Identifier(BookOfDragons.MOD_ID, "animations/entity/eel.animation.json");
    }

    @Override
    public void setLivingAnimations(EelEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        IBone head = getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        if (entity.isInsideWaterOrBubbleColumn()) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
        else {
            head.setRotationX(0);
            head.setRotationY(0);
        }
    }
}
