package coda.bookofdragons.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class BODSpawnEggItem extends SpawnEggItem {
    public static final List<BODSpawnEggItem> UNADDED_EGGS = new ArrayList<BODSpawnEggItem>();
    private final Lazy<? extends EntityType<?>> entityTypeSupplier;

    public BODSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, final int primaryColor, final int secondaryColor, final Properties properties) {
        super(null, primaryColor, secondaryColor, properties);
        this.entityTypeSupplier = Lazy.of(entityTypeSupplier::get);
        UNADDED_EGGS.add(this);
    }

    @Override
    public EntityType<?> getType(CompoundTag nbt) {
        return this.entityTypeSupplier.get();
    }
}