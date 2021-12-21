package net.arathain.bookofdragons.client.model;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.NightFuryEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class NightFurryModel extends AnimatedGeoModel<NightFuryEntity> {

    @Override
    public Identifier getModelLocation(NightFuryEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "geo/entity/night_fury.geo.json");
    }

    @Override
    public Identifier getTextureLocation(NightFuryEntity object) {
        return new Identifier(BookOfDragons.MOD_ID, "textures/entity/night_fury/night_fury.png");
    }

    @Override
    public Identifier getAnimationFileLocation(NightFuryEntity animatable) {
        return new Identifier(BookOfDragons.MOD_ID, "animations/entity/night_fury.animation.json");
    }

    @Override
    public void setLivingAnimations(NightFuryEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone body = this.getAnimationProcessor().getBone("body");

        //head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        //head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        body.setRotationX((float) MathHelper.lerp(0.6f, -entity.getPitch() * ((float) Math.PI / 90F), body.getRotationX()));
        this.getAnimationProcessor().getBone("eyelids").setHidden(true);
        if (entity.isBaby()) {
            body.setScaleX(0.4f);
            body.setScaleY(0.4f);
            body.setScaleZ(0.4f);
            body.setPositionY(-15F);
        }
    }
}
