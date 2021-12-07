package net.arathain.bookofdragons;

import net.arathain.bookofdragons.common.init.BODEntities;
import net.arathain.bookofdragons.common.init.BODObjects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

public class BookOfDragons implements ModInitializer {
	public static final String MOD_ID = "bookofdragons";
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, MOD_ID), () -> new ItemStack(BODObjects.GRONCKLE_IRON_BLOCK.asItem()));


	@Override
	public void onInitialize() {
		BODObjects.init();
		BODEntities.init();
		GeckoLib.initialize();

	}
}
