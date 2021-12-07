package net.arathain.bookofdragons.client.render;

import net.arathain.bookofdragons.client.model.EelModel;
import net.arathain.bookofdragons.common.entity.EelEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class EelRenderer extends GeoEntityRenderer<EelEntity> {

    public EelRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new EelModel());
        this.shadowRadius = 0.4F;
    }
}
