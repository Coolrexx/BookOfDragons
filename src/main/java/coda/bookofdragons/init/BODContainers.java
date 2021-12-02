package coda.bookofdragons.init;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.menu.DragonInventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BODContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BookOfDragons.MOD_ID);

    public static final RegistryObject<MenuType<DragonInventoryMenu>> DRAGON_INV = CONTAINERS.register("dragon_inv", () -> new MenuType<>(DragonInventoryMenu::dragonMenu));
}