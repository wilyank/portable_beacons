package wilyan_kramer.portable_beacons.setup;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.command.ModCommands;


@Mod.EventBusSubscriber(modid = PortableBeaconsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonSetup {
	
	public static void init(final FMLCommonSetupEvent event) {
	}
	
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
    }
    
    
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
    	ModCommands.register(event.getDispatcher());
    }
}
