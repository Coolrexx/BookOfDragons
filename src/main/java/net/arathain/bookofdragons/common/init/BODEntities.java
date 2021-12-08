package net.arathain.bookofdragons.common.init;


import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.EelEntity;
import net.arathain.bookofdragons.common.entity.GronckleEntity;
import net.arathain.bookofdragons.common.entity.TerribleTerrorEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BODEntities {
    private static final Map<EntityType<?>, Identifier> ENTITY_TYPES = new LinkedHashMap<>();

    public static final EntityType<EelEntity> EEL = createEntity("eel", EelEntity.createAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, EelEntity::new).dimensions(EntityDimensions.fixed(0.5F, 0.4F)).build());
    public static final EntityType<TerribleTerrorEntity> TERRIBLE_TERROR = createEntity("terrible_terror", TerribleTerrorEntity.createAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TerribleTerrorEntity::new).dimensions(EntityDimensions.fixed(1.0F, 0.75F)).build());
    public static final EntityType<GronckleEntity> GRONCKLE = createEntity("gronckle", GronckleEntity.createAttributes(), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GronckleEntity::new).dimensions(EntityDimensions.fixed(2.0F, 2.0F)).build());

    private static <T extends Entity> EntityType<T> createEntity(String name, EntityType<T> type) {
        ENTITY_TYPES.put(type, new Identifier(BookOfDragons.MOD_ID, name));
        return type;
    }

    private static <T extends LivingEntity> EntityType<T> createEntity(String name, DefaultAttributeContainer.Builder attributes, EntityType<T> type) {
        FabricDefaultAttributeRegistry.register(type, attributes);
        ENTITY_TYPES.put(type, new Identifier(BookOfDragons.MOD_ID, name));
        return type;
    }

    public static void init() {
        ENTITY_TYPES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITY_TYPES.get(entityType), entityType));
    }
}
