package coda.bookofdragons.registry;

import coda.bookofdragons.BookOfDragons;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class BODBiomes {

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, BookOfDragons.MOD_ID);

    public static final RegistryObject<Biome> FIREWORM_HIVES = BIOMES.register("fireworm_hives", BODBiomes::firewormHives);

    public static Biome firewormHives() {
        MobSpawnSettings.Builder mobBuilder = new MobSpawnSettings.Builder();
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
        BiomeDefaultFeatures.addDefaultCarversAndLakes(builder);
        BiomeDefaultFeatures.addDefaultCrystalFormations(builder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(builder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(builder);
        BiomeDefaultFeatures.addDefaultSprings(builder);
        BiomeDefaultFeatures.addSurfaceFreezing(builder);
        BiomeDefaultFeatures.addPlainGrass(builder);
        BiomeDefaultFeatures.addDefaultOres(builder, true);
        BiomeDefaultFeatures.addDefaultSoftDisks(builder);
        BiomeDefaultFeatures.addPlainVegetation(builder);
        BiomeDefaultFeatures.addDefaultMushrooms(builder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(builder);
        Music music = Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES);
        return biome(Biome.Precipitation.RAIN, Biome.BiomeCategory.UNDERGROUND, 1.0F, 0.4F, mobBuilder, builder, music);
    }

    private static Biome biome(Biome.Precipitation precipitation, Biome.BiomeCategory category, float temperature, float downFall, MobSpawnSettings.Builder mobBuilder, BiomeGenerationSettings.Builder builder, @Nullable Music music) {
        return biome(precipitation, category, temperature, downFall, 4159204, 329011, mobBuilder, builder, music);
    }

    private static Biome biome(Biome.Precipitation precipitation, Biome.BiomeCategory category, float temperature, float downFall, int waterColor, int waterFogColor, MobSpawnSettings.Builder mobBuilder, BiomeGenerationSettings.Builder builder, @Nullable Music music) {
        return (new Biome.BiomeBuilder()).precipitation(precipitation).biomeCategory(category).temperature(temperature).downfall(downFall).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(waterColor).waterFogColor(waterFogColor).fogColor(12638463).skyColor(calculateSkyColor(temperature)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(music).build()).mobSpawnSettings(mobBuilder.build()).generationSettings(builder.build()).build();
    }

    protected static int calculateSkyColor(float temperature) {
        float $$1 = temperature / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }

}