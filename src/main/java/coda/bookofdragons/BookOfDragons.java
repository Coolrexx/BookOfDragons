package coda.bookofdragons;

import coda.bookofdragons.init.BODItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        bus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }
}
