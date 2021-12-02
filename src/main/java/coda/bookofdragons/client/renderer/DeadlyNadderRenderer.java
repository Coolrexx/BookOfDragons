package coda.bookofdragons.client.renderer;

import coda.bookofdragons.client.model.DeadlyNadderModel;
import coda.bookofdragons.common.entities.DeadlyNadderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DeadlyNadderRenderer extends GeoEntityRenderer<DeadlyNadderEntity> {

    public DeadlyNadderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DeadlyNadderModel());
        this.shadowRadius = 1.1F;
    }
}