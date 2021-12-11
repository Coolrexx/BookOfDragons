package net.arathain.bookofdragons;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.entity.component.DragonRiderComponent;
import net.minecraft.util.Identifier;

public class BODComponents implements EntityComponentInitializer {
    public static final ComponentKey<DragonRiderComponent> DRAGON_RIDER_COMPONENT = ComponentRegistry.getOrCreate(new Identifier(BookOfDragons.MOD_ID, "dragon_rider"), DragonRiderComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(DRAGON_RIDER_COMPONENT, DragonRiderComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
