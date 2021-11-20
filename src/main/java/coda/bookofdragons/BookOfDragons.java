package coda.bookofdragons;

import coda.bookofdragons.common.entities.EelEntity;
import coda.bookofdragons.init.BODEntities;
import coda.bookofdragons.init.BODItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(BookOfDragons.MOD_ID)
public class BookOfDragons {
    public static final String MOD_ID = "bookofdragons";
    public static final Logger LOGGER = LogManager.getLogger();
    public final static CreativeModeTab GROUP = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BODItems.EEL.get());
        }
    };

    public BookOfDragons() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BODItems.ITEMS.register(bus);
        BODEntities.ENTITIES.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::registerAttributes);

        GeckoLib.initialize();
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(BODEntities.EEL.get(), EelEntity.createAttributes().build());
    }
}
