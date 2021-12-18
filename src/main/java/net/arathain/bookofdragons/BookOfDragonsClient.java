package net.arathain.bookofdragons;

import io.netty.buffer.Unpooled;
import net.arathain.bookofdragons.client.render.*;
import net.arathain.bookofdragons.client.screen.DragonInventoryScreen;
import net.arathain.bookofdragons.common.init.BODEntities;
import net.arathain.bookofdragons.common.network.packet.UpdatePressingUpDownPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class BookOfDragonsClient implements ClientModInitializer {
    private static KeyBinding DRAGON_DESCEND;
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(BODEntities.EEL, EelRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.NIGHT_FURY, NightFuryRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.TERRIBLE_TERROR, TerribleTerrorRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.GRONCKLE, GronckleRenderer::new);
        EntityRendererRegistry.INSTANCE.register(BODEntities.DEADLY_NADDER, DeadlyNadderRenderer::new);
        ScreenRegistry.register(BookOfDragons.DRAGON_SCREEN_HANDLER_TYPE, DragonInventoryScreen::new);
        DRAGON_DESCEND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bookofdragons.descend", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_G, // The keycode of the key
                "category.bookofdragons.keybind"
        ));
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                UpdatePressingUpDownPacket.send(MinecraftClient.getInstance().options.keyJump.isPressed(), DRAGON_DESCEND.isPressed());

            }
        });
    }

}
