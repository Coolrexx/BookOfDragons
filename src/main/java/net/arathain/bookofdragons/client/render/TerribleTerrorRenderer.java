package net.arathain.bookofdragons.client.render;

import net.arathain.bookofdragons.client.model.TerribleTerrorModel;
import net.arathain.bookofdragons.common.entity.TerribleTerrorEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class TerribleTerrorRenderer extends GeoEntityRenderer<TerribleTerrorEntity> {

    public TerribleTerrorRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TerribleTerrorModel());
        this.shadowRadius = 0.6F;
    }
}
