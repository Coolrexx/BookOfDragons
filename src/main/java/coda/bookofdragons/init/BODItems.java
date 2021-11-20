package coda.bookofdragons.init;

import coda.bookofdragons.BookOfDragons;
import coda.bookofdragons.common.items.BODBucketItem;
import coda.bookofdragons.common.items.BODItemTier;
import coda.bookofdragons.common.items.BODSpawnEggItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BODItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BookOfDragons.MOD_ID);

    public static final RegistryObject<Item> RAW_GRONCKLE_IRON = ITEMS.register("raw_gronckle_iron", () -> new Item(new Item.Properties().tab(BookOfDragons.GROUP)));
    public static final RegistryObject<Item> GRONCKLE_IRON_INGOT = ITEMS.register("gronckle_iron_ingot", () -> new Item(new Item.Properties().tab(BookOfDragons.GROUP)));
    public static final RegistryObject<Item> GRONCKLE_IRON_SWORD = ITEMS.register("gronckle_iron_sword", () -> new SwordItem(BODItemTier.GRONKLE_IRON, 3, -2.1F, new Item.Properties().tab(BookOfDragons.GROUP).stacksTo(1)));
    public static final RegistryObject<Item> GRONCKLE_IRON_PICKAXE = ITEMS.register("gronckle_iron_pickaxe", () -> new PickaxeItem(BODItemTier.GRONKLE_IRON, 1, -2.5F, new Item.Properties().tab(BookOfDragons.GROUP).stacksTo(1)));
    public static final RegistryObject<Item> GRONCKLE_IRON_AXE = ITEMS.register("gronckle_iron_axe", () -> new AxeItem(BODItemTier.GRONKLE_IRON, 5, -2.85F, new Item.Properties().tab(BookOfDragons.GROUP).stacksTo(1)));
    public static final RegistryObject<Item> GRONCKLE_IRON_SHOVEL = ITEMS.register("gronckle_iron_shovel", () -> new ShovelItem(BODItemTier.GRONKLE_IRON, 1.5F, -2.8F, new Item.Properties().tab(BookOfDragons.GROUP).stacksTo(1)));
    public static final RegistryObject<Item> GRONCKLE_IRON_HOE = ITEMS.register("gronckle_iron_hoe", () -> new HoeItem(BODItemTier.GRONKLE_IRON, 3, -0.0F, new Item.Properties().tab(BookOfDragons.GROUP).stacksTo(1)));

    public static final RegistryObject<Item> EEL = ITEMS.register("eel", () -> new Item(new Item.Properties().tab(BookOfDragons.GROUP).food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1F).effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 0), 0.5F).build())));
    public static final RegistryObject<Item> EEL_BUCKET = ITEMS.register("eel_bucket", () -> new BODBucketItem(BODEntities.EEL, () -> Fluids.WATER, Items.BUCKET, false, new Item.Properties().tab(BookOfDragons.GROUP).stacksTo(1)));
    public static final RegistryObject<Item> EEL_SPAWN_EGG = ITEMS.register("eel_spawn_egg", () -> new BODSpawnEggItem(BODEntities.EEL, 0x222123, 0xc8bc15, new Item.Properties().tab(BookOfDragons.GROUP)));
}
