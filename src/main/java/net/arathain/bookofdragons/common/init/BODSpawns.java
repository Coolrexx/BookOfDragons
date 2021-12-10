package net.arathain.bookofdragons.common.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;

public class BODSpawns {
    public static void init() {
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.OCEAN), SpawnGroup.WATER_CREATURE, BODEntities.EEL,  3, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.MESA), SpawnGroup.CREATURE, BODEntities.GRONCKLE,  1, 0, 1);
        BiomeModifications.addSpawn(BiomeSelectors.categories(Biome.Category.FOREST), SpawnGroup.CREATURE, BODEntities.TERRIBLE_TERROR,  10, 0, 1);
    }
}
