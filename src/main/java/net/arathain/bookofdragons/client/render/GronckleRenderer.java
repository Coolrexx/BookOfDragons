package net.arathain.bookofdragons.client.render;

import net.arathain.bookofdragons.client.model.GronckleModel;
import net.arathain.bookofdragons.client.render.layer.GronckleSaddleLayer;
import net.arathain.bookofdragons.common.entity.GronckleEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GronckleRenderer extends GeoEntityRenderer<GronckleEntity> {

    public GronckleRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GronckleModel());
        this.addLayer(new GronckleSaddleLayer(this));
        this.shadowRadius = 1.1F;
    }
}
