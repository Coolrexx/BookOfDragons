package coda.bookofdragons.client;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.client.renderer.*;
import coda.bookofdragons.registry.BODEntities;
import coda.bookofdragons.registry.BODKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BookOfDragons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BODEntities.EEL.get(), EelRenderer::new);
        event.registerEntityRenderer(BODEntities.TERRIBLE_TERROR.get(), TerribleTerrorRenderer::new);
        event.registerEntityRenderer(BODEntities.GRONCKLE.get(), GronckleRenderer::new);
        event.registerEntityRenderer(BODEntities.DEADLY_NADDER.get(), DeadlyNadderRenderer::new);
        event.registerEntityRenderer(BODEntities.NIGHT_FURY.get(), NightFuryRenderer::new);
    }

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    public static double getFlightDelta() {
        return getClient().options.keyJump.isDown() ? 0.4 : BODKeyBindings.DRAGON_DESCEND.isDown() ? -0.5 : 0;
    }
}