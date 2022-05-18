package coda.bookofdragons.client.model;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.GronckleEntity;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import java.util.Map;

public class GronckleModel extends AnimatedTickingGeoModel<GronckleEntity> {
        public static final Map<GronckleEntity.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (p_173927_) -> {
            p_173927_.put(GronckleEntity.Variant.HERO, new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/gronckle/gronckle_hero.png"));
            p_173927_.put(GronckleEntity.Variant.BLUE, new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/gronckle/gronckle_blue.png"));
            p_173927_.put(GronckleEntity.Variant.GREEN, new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/gronckle/gronckle_green.png"));
            p_173927_.put(GronckleEntity.Variant.PURPLE, new ResourceLocation(BookOfDragons.MOD_ID, "textures/entity/gronckle/gronckle_purple.png"));
        });

    @Override
    public ResourceLocation getModelLocation(GronckleEntity object) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "geo/entity/gronckle.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(GronckleEntity object) {
        return TEXTURE_BY_TYPE.get(object.getVariant());
    }

    @Override
    public ResourceLocation getAnimationFileLocation(GronckleEntity animatable) {
        return new ResourceLocation(BookOfDragons.MOD_ID, "animations/entity/gronckle.animation.json");
    }

    @Override
    public void setLivingAnimations(GronckleEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone body = this.getAnimationProcessor().getBone("root");



        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));

        if (entity.isBaby()) {
            body.setScaleX(0.4f);
            body.setScaleY(0.4f);
            body.setScaleZ(0.4f);
            body.setPositionZ(-9F);
        }
    }
}
