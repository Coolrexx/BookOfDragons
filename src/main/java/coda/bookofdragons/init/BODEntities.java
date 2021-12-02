package coda.bookofdragons.init;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.entities.DeadlyNadderEntity;
import coda.bookofdragons.common.entities.EelEntity;
import coda.bookofdragons.common.entities.GronckleEntity;
import coda.bookofdragons.common.entities.TerribleTerrorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BODEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BookOfDragons.MOD_ID);

    public static final RegistryObject<EntityType<EelEntity>> EEL = create("eel", EntityType.Builder.of(EelEntity::new, MobCategory.WATER_AMBIENT).sized(0.6f, 0.3f));
    public static final RegistryObject<EntityType<TerribleTerrorEntity>> TERRIBLE_TERROR = create("terrible_terror", EntityType.Builder.of(TerribleTerrorEntity::new, MobCategory.CREATURE).sized(1.0f, 0.75f));
    public static final RegistryObject<EntityType<GronckleEntity>> GRONCKLE = create("gronckle", EntityType.Builder.of(GronckleEntity::new, MobCategory.CREATURE).sized(2.0f, 2.0f));
    public static final RegistryObject<EntityType<DeadlyNadderEntity>> DEADLY_NADDER = create("deadly_nadder", EntityType.Builder.of(DeadlyNadderEntity::new, MobCategory.CREATURE).sized(2.0f, 2.3f));

    private static <T extends Entity> RegistryObject<EntityType<T>> create(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(BookOfDragons.MOD_ID + "." + name));
    }
}
