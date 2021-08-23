package wilyan_kramer.portable_beacons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import wilyan_kramer.portable_beacons.common.EventListeners;
import wilyan_kramer.portable_beacons.common.command.ModCommands;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.setup.ClientSetup;
import wilyan_kramer.portable_beacons.setup.CommonSetup;

@Mod("portable_beacons")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PortableBeaconsMod {
	public static final String MODID = "portable_beacons";

	// Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public PortableBeaconsMod() {
    	Config.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventListeners());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("curios", "register_type", () -> new SlotTypeMessage.Builder("necklace").build());
    	InterModComms.sendTo("curios", "register_type", () -> new SlotTypeMessage.Builder("back").build());
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.getMessageSupplier().get()).
//                collect(Collectors.toList()));
    }
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
    	ModCommands.register(event.getDispatcher());
    }
}
