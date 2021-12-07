package net.arathain.bookofdragons;

import net.arathain.bookofdragons.client.render.EelRenderer;
import net.arathain.bookofdragons.client.render.TerribleTerrorRenderer;
import net.arathain.bookofdragons.common.init.BODEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class BookOfDragonsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(BODEntities.EEL, EelRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.TERRIBLE_TERROR, TerribleTerrorRenderer::new);
    }
}
