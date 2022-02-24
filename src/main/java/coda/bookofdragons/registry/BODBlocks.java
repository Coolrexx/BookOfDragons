package coda.bookofdragons.registry;

import coda.bookofdragons.BookOfDragons;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BODBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BookOfDragons.MOD_ID);

    public static final RegistryObject<Block> GRONCKLE_IRON_BLOCK = register("gronckle_iron_block", () -> new Block(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> RAW_GRONCKLE_IRON_BLOCK = register("raw_gronckle_iron_block", () -> new Block(BlockBehaviour.Properties.of(Material.HEAVY_METAL).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL)));

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return register(name, block, new Item.Properties().tab(BookOfDragons.GROUP));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Item.Properties itemProperties) {
        return register(name, block, BlockItem::new, itemProperties);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, BiFunction<Block, Item.Properties, BlockItem> item, Item.Properties itemProperties) {
        final RegistryObject<T> registryObject = BLOCKS.register(name, block);
        if (itemProperties != null) BODItems.ITEMS.register(name, () -> item == null ? new BlockItem(registryObject.get(), itemProperties) : item.apply(registryObject.get(), itemProperties));
        return registryObject;
    }
}