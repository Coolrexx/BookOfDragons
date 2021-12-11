package net.arathain.bookofdragons;

import net.arathain.bookofdragons.common.init.BODEntities;
import net.arathain.bookofdragons.common.init.BODObjects;
import net.arathain.bookofdragons.common.init.BODSpawns;
import net.arathain.bookofdragons.common.menu.DragonScreenHandler;
import net.arathain.bookofdragons.common.network.packet.UpdatePressingUpDownPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;

public class BookOfDragons implements ModInitializer {
	public static final String MOD_ID = "bookofdragons";
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, MOD_ID), () -> new ItemStack(BODObjects.GRONCKLE_IRON_BLOCK.asItem()));
	public static final ScreenHandlerType<DragonScreenHandler> DRAGON_SCREEN_HANDLER_TYPE =
			ScreenHandlerRegistry.registerExtended(new Identifier(MOD_ID, "dragon"), DragonScreenHandler::new);


	@Override
	public void onInitialize() {
		BODObjects.init();
		BODEntities.init();
		BODSpawns.init();
		GeckoLib.initialize();
		ServerPlayNetworking.registerGlobalReceiver(UpdatePressingUpDownPacket.ID, UpdatePressingUpDownPacket::handle);


	}
}
