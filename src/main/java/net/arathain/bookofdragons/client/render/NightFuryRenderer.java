package net.arathain.bookofdragons.client.render;

import net.arathain.bookofdragons.client.model.NightFurryModel;
import net.arathain.bookofdragons.common.entity.NightFuryEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NightFuryRenderer extends GeoEntityRenderer<NightFuryEntity> {
    public NightFuryRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new NightFurryModel());
        this.shadowRadius = 1.1F;
    }
}
