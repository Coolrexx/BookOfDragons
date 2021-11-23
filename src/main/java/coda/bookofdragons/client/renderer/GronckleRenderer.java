package coda.bookofdragons.client.renderer;

import coda.bookofdragons.client.model.GronckleModel;
import coda.bookofdragons.client.renderer.layer.GronckleSaddleLayer;
import coda.bookofdragons.common.entities.GronckleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GronckleRenderer extends GeoEntityRenderer<GronckleEntity> {

    public GronckleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GronckleModel());
        this.addLayer(new GronckleSaddleLayer(this));
        this.shadowRadius = 1.1F;
    }
}