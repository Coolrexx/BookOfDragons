package net.arathain.bookofdragons.common.network.packet;

import io.netty.buffer.Unpooled;
import net.arathain.bookofdragons.BODComponents;
import net.arathain.bookofdragons.BookOfDragons;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UpdatePressingUpDownPacket {
    public static final Identifier ID = new Identifier(BookOfDragons.MOD_ID, "toggle_pressing_up_down");

    public static void send(boolean pressingUp, boolean pressingDown) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(pressingUp);
        buf.writeBoolean(pressingDown);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        boolean pressingUp = buf.readBoolean();
        boolean pressingDown = buf.readBoolean();
        server.execute(() -> {
            BODComponents.DRAGON_RIDER_COMPONENT.get(player).setPressingUp(pressingUp);
            BODComponents.DRAGON_RIDER_COMPONENT.get(player).setPressingDown(pressingDown);
        });
    }
}
