package coda.bookofdragons.client;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.client.renderer.DeadlyNadderRenderer;
import coda.bookofdragons.client.renderer.EelRenderer;
import coda.bookofdragons.client.renderer.GronckleRenderer;
import coda.bookofdragons.client.renderer.TerribleTerrorRenderer;
import coda.bookofdragons.client.screen.DragonInventoryScreen;
import coda.bookofdragons.init.BODContainers;
import coda.bookofdragons.init.BODEntities;
import coda.bookofdragons.init.BODKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BookOfDragons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BODEntities.EEL.get(), EelRenderer::new);
        event.registerEntityRenderer(BODEntities.TERRIBLE_TERROR.get(), TerribleTerrorRenderer::new);
        event.registerEntityRenderer(BODEntities.GRONCKLE.get(), GronckleRenderer::new);
        event.registerEntityRenderer(BODEntities.DEADLY_NADDER.get(), DeadlyNadderRenderer::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        BODKeyBindings.LIST.forEach(ClientRegistry::registerKeyBinding);

        MenuScreens.register(BODContainers.DRAGON_INV.get(), DragonInventoryScreen::new);
    }

    public static Minecraft getClient() {
        return Minecraft.getInstance();
    }

    public static double getFlightDelta() {
        return getClient().options.keyJump.isDown() ? 0.25 : BODKeyBindings.DRAGON_DESCEND.isDown() ? -0.5 : 0;
    }
}