package net.arathain.bookofdragons.common.init;

import net.arathain.bookofdragons.BookOfDragons;
import net.arathain.bookofdragons.common.item.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class BODObjects {
    private static final Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    private static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    public static final Block GRONCKLE_IRON_BLOCK = createBlock("gronckle_iron_block", new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL)), true);
    public static final Block RAW_GRONCKLE_IRON_BLOCK = createBlock("raw_gronckle_iron_block", new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL)), true);
    public static final Item RAW_GRONCKLE_IRON = createItem("raw_gronckle_iron", new Item(new Item.Settings().group(BookOfDragons.GROUP)));
    public static final Item GRONCKLE_IRON_INGOT = createItem("gronckle_iron_ingot", new Item(new Item.Settings().group(BookOfDragons.GROUP)));
    public static final Item GRONCKLE_IRON_NUGGET_ = createItem("gronckle_iron_nugget", new Item(new Item.Settings().group(BookOfDragons.GROUP)));
    public static final Item GRONCKLE_IRON_SWORD = createItem("gronckle_iron_sword", new SwordItem(BODToolMaterial.GRONKLE_IRON, 3, -2.1F, new Item.Settings().group(BookOfDragons.GROUP).maxCount(1)));
    public static final Item GRONCKLE_IRON_PICKAXE = createItem("gronckle_iron_pickaxe", new BODPickaxeItem(BODToolMaterial.GRONKLE_IRON, 1, -2.5F, new Item.Settings().group(BookOfDragons.GROUP).maxCount(1)));
    public static final Item GRONCKLE_IRON_AXE = createItem("gronckle_iron_axe", new BODAxeItem(BODToolMaterial.GRONKLE_IRON, 5, -2.85F, new Item.Settings().group(BookOfDragons.GROUP).maxCount(1)));
    public static final Item GRONCKLE_IRON_SHOVEL = createItem("gronckle_iron_shovel", new ShovelItem(BODToolMaterial.GRONKLE_IRON, 1.5F, -2.8F, new Item.Settings().group(BookOfDragons.GROUP).maxCount(1)));
    public static final Item GRONCKLE_IRON_HOE = createItem("gronckle_iron_hoe", new BODHoeItem(BODToolMaterial.GRONKLE_IRON, 3, -0.0F, new Item.Settings().group(BookOfDragons.GROUP).maxCount(1)));

    public static final Item EEL = createItem("eel", new Item(new Item.Settings().group(BookOfDragons.GROUP).food(new FoodComponent.Builder().saturationModifier(0.1F).statusEffect( new StatusEffectInstance(StatusEffects.POISON, 200, 0), 0.5F).build())));
    public static final Item EEL_BUCKET = createItem("eel_bucket", new EntityBucketItem(BODEntities.EEL, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH, new Item.Settings().group(BookOfDragons.GROUP).maxCount(1)));

    public static final Item EEL_SPAWN_EGG = createItem("eel_spawn_egg", new BODSpawnEggItem(BODEntities.EEL, 0x222123, 0xc8bc15, new Item.Settings().group(BookOfDragons.GROUP)));
    public static final Item TERRIBLE_TERROR_SPAWN_EGG = createItem("terrible_terror_spawn_egg", new BODSpawnEggItem(BODEntities.TERRIBLE_TERROR, 0x6f7930, 0x843917, new Item.Settings().group(BookOfDragons.GROUP)));
    public static final Item GRONCKLE_SPAWN_EGG = createItem("gronckle_spawn_egg", new BODSpawnEggItem(BODEntities.GRONCKLE, 0x9e5b40, 0xb99575, new Item.Settings().group(BookOfDragons.GROUP)));
    public static final Item DEADLY_NADDER_SPAWN_EGG = createItem("deadly_nadder_spawn_egg", new BODSpawnEggItem(BODEntities.DEADLY_NADDER, 0x429ab2, 0xe3a923, new Item.Settings().group(BookOfDragons.GROUP)));



    private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
        BLOCKS.put(block, new Identifier(BookOfDragons.MOD_ID, name));
        if (createItem) {
            ITEMS.put(new BlockItem(block, new Item.Settings().group(BookOfDragons.GROUP)), BLOCKS.get(block));
        }
        return block;
    }
    private static <T extends Item> T createItem(String name, T item) {
        ITEMS.put(item, new Identifier(BookOfDragons.MOD_ID, name));
        return item;
    }

    public static void init() {
        BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
        ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));

    }
}
