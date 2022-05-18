package coda.bookofdragons.client.renderer;

import coda.bookofdragons.client.model.NightFuryModel;
import coda.bookofdragons.client.renderer.layer.NightFurySaddleLayer;
import coda.bookofdragons.common.entities.NightFuryEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NightFuryRenderer extends GeoEntityRenderer<NightFuryEntity> {

    public NightFuryRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NightFuryModel());
        this.addLayer(new NightFurySaddleLayer(this));
        this.shadowRadius = 1.1F;
    }
}