package coda.bookofdragons.client.model;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.TerribleTerrorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class TerribleTerrorModel extends AnimatedTickingGeoModel<TerribleTerrorEntity> {

    @Override
    public ResourceLocation getModelLocation(TerribleTerrorEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "geo/entity/terrible_terror.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(TerribleTerrorEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/terrible_terror.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TerribleTerrorEntity animatable) {
        return null;
    }

    @Override
    public void setLivingAnimations(TerribleTerrorEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
//        IBone neck = this.getAnimationProcessor().getBone("neck");

//        neck.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
//        neck.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));


/*        if (entity.isBaby()) {
            head.setScaleX(0.6f);
            head.setScaleY(0.6f);
            head.setScaleZ(0.6f);
        }*/

    }
}
