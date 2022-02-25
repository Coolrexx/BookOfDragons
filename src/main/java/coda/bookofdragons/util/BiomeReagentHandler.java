package coda.bookofdragons.util;

import coda.bookofdragons.BookOfDragons;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BiomeReagentHandler {
    public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
    public static final Climate.Parameter CAVE_BIOME_RANGE = Climate.Parameter.span(0.2F, 0.9F);

    public static ResourceKey<Biome> FIREWORM_HIVES;

    public static void init(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {

        if (FIREWORM_HIVES == null)
            FIREWORM_HIVES = registerResourceKey("fireworm_hives");

        consumer.accept(Pair.of(Climate.parameters(Climate.Parameter.span(0.8F, 1.0F), FULL_RANGE, FULL_RANGE, FULL_RANGE, CAVE_BIOME_RANGE, Climate.Parameter.span(-1.0F, 0.0F), 0.0F), FIREWORM_HIVES));
    }

    @NotNull
    private static ResourceKey<Biome> registerResourceKey(String name) {
        return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(BookOfDragons.MOD_ID, name));
    }
}
