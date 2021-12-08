package net.arathain.bookofdragons.client.model;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.GronckleEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class GronckleModel extends AnimatedGeoModel<GronckleEntity> {

    @Override
    public Identifier getModelLocation(GronckleEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "geo/entity/gronckle.geo.json");
    }

    @Override
    public Identifier getTextureLocation(GronckleEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "textures/entity/gronckle/gronckle.png");
    }

    @Override
    public Identifier getAnimationFileLocation(GronckleEntity animatable) {
        return new Identifier(BookOfDragons.MOD_ID, "animations/entity/gronckle.animation.json");
    }

    @Override
    public void setLivingAnimations(GronckleEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone body = this.getAnimationProcessor().getBone("body");

        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));

        if (entity.isBaby()) {
            body.setScaleX(0.4f);
            body.setScaleY(0.4f);
            body.setScaleZ(0.4f);
            body.setPositionY(-15F);
        }
    }
}
