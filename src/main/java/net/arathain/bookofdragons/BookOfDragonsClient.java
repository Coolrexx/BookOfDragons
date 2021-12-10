package net.arathain.bookofdragons;

import net.arathain.bookofdragons.client.render.DeadlyNadderRenderer;
import net.arathain.bookofdragons.client.render.EelRenderer;
import net.arathain.bookofdragons.client.render.GronckleRenderer;
import net.arathain.bookofdragons.client.render.TerribleTerrorRenderer;
import net.arathain.bookofdragons.client.screen.DragonInventoryScreen;
import net.arathain.bookofdragons.common.init.BODEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BookOfDragonsClient implements ClientModInitializer {
    private static KeyBinding DRAGON_DESCEND;
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(BODEntities.EEL, EelRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.TERRIBLE_TERROR, TerribleTerrorRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.GRONCKLE, GronckleRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.DEADLY_NADDER, DeadlyNadderRenderer::new);
        ScreenRegistry.register(BookOfDragons.DRAGON_SCREEN_HANDLER_TYPE, DragonInventoryScreen::new);
        DRAGON_DESCEND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bookofdragons.descend", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z, // The keycode of the key
                "category.bookofdragons.keybind"
        ));
    }

    public static double getFlightDelta() {
        return MinecraftClient.getInstance().options.keyJump.isPressed() ? 0.25 : DRAGON_DESCEND.isPressed() ? -0.5 : 0;
    }
    public static double getYawDelta() {
        return MinecraftClient.getInstance().options.keyRight.isPressed() ? 1 : MinecraftClient.getInstance().options.keyLeft.isPressed() ? -1 : 0;
    }
}
