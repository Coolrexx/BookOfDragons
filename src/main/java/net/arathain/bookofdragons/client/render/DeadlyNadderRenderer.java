package net.arathain.bookofdragons.client.render;

import net.arathain.bookofdragons.client.model.DeadlyNadderModel;
import net.arathain.bookofdragons.common.entity.DeadlyNadderEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DeadlyNadderRenderer extends GeoEntityRenderer<DeadlyNadderEntity> {

    public DeadlyNadderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DeadlyNadderModel());
        this.shadowRadius = 1.1F;
    }
}
