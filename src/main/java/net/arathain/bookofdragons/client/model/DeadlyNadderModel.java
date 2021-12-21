package net.arathain.bookofdragons.client.model;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.DeadlyNadderEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class DeadlyNadderModel extends AnimatedGeoModel<DeadlyNadderEntity> {

    @Override
    public Identifier getModelLocation(DeadlyNadderEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "geo/entity/deadly_nadder.geo.json");
    }

    @Override
    public Identifier getTextureLocation(DeadlyNadderEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "textures/entity/deadly_nadder/deadly_nadder_" + object.getNadderType().toString().toLowerCase() + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(DeadlyNadderEntity animatable) {
        return new Identifier(BookOfDragons.MOD_ID, "animations/entity/deadly_nadder.animation.json");
    }

    @Override
    public void setLivingAnimations(DeadlyNadderEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone body = this.getAnimationProcessor().getBone("body");

        //head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        //head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        body.setRotationX((float) MathHelper.lerp(body.getRotationX(), (-entity.getPitch() * 0.006), 0.6f));
        if (entity.isBaby()) {
            body.setScaleX(0.4f);
            body.setScaleY(0.4f);
            body.setScaleZ(0.4f);
            body.setPositionY(-15F);
        }
    }
}
