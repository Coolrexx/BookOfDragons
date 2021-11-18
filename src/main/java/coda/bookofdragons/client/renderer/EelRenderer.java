package coda.bookofdragons.client.renderer;

import coda.bookofdragons.client.model.EelModel;
import coda.bookofdragons.common.entities.EelEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class EelRenderer extends GeoEntityRenderer<EelEntity> {

    public EelRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new EelModel());
        this.shadowRadius = 0.4F;
    }
}