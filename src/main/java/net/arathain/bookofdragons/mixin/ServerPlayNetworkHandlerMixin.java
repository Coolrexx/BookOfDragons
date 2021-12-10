package net.arathain.bookofdragons.mixin;

import net.arathain.bookofdragons.common.entity.util.AbstractRideableDragonEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onClientCommand", at = @At("RETURN"))
    private void onClientCommand(ClientCommandC2SPacket packet, CallbackInfo ci) {
        if (packet.getMode() == ClientCommandC2SPacket.Mode.OPEN_INVENTORY && this.player.getVehicle() instanceof AbstractRideableDragonEntity dragon) {
            dragon.openInventory(player);
        }
    }
}
