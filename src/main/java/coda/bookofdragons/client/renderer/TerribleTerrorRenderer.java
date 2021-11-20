package coda.bookofdragons.client.renderer;

import coda.bookofdragons.client.model.TerribleTerrorModel;
import coda.bookofdragons.common.entities.TerribleTerrorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class TerribleTerrorRenderer extends GeoEntityRenderer<TerribleTerrorEntity> {

    public TerribleTerrorRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TerribleTerrorModel());
        this.shadowRadius = 0.6F;
    }
}