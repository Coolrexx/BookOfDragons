package coda.bookofdragons.init;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.EelEntity;
import coda.bookofdragons.common.entities.TerribleTerrorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BODEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BookOfDragons.MOD_ID);

    public static final RegistryObject<EntityType<EelEntity>> EEL = create("eel", EntityType.Builder.of(EelEntity::new, MobCategory.WATER_AMBIENT).sized(0.6f, 0.3f));
    public static final RegistryObject<EntityType<TerribleTerrorEntity>> TERRIBLE_TERROR = create("terrible_terror", EntityType.Builder.of(TerribleTerrorEntity::new, MobCategory.CREATURE).sized(1.0f, 1.0f));

    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(BookOfDragons.MOD_ID + "." + name));
    }
}
