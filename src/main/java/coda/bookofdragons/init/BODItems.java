package coda.bookofdragons.init;

import coda.bookofdragons.BookOfDragons;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BODItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BookOfDragons.MOD_ID);

    public static final RegistryObject<Item> EEL = ITEMS.register("eel", () -> new Item(new Item.Properties().tab(BookOfDragons.GROUP).food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).effect(() -> new MobEffectInstance(MobEffects.POISON, 40, 0), 0.5F).build())));
}
