package net.arathain.bookofdragons.client.model;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.TerribleTerrorEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class TerribleTerrorModel extends AnimatedGeoModel<TerribleTerrorEntity> {

    @Override
    public Identifier getModelLocation(TerribleTerrorEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "geo/entity/terrible_terror.geo.json");
    }

    @Override
    public Identifier getTextureLocation(TerribleTerrorEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "textures/entity/terrible_terror/terrible_terror.png");
    }

    @Override
    public Identifier getAnimationFileLocation(TerribleTerrorEntity animatable) {
        return new Identifier(BookOfDragons.MOD_ID, "animations/entity/terrible_terror.animation.json");
    }

    @Override
    public void setLivingAnimations(TerribleTerrorEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

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

    }
}
