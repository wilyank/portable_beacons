package wilyan_kramer.portable_beacons.setup;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wilyan_kramer.portable_beacons.common.command.ModCommands;


public class CommonSetup {    
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
    	//PortableBeaconsMod.LOGGER.info(PortableBeaconsMod.MODID + " Registering Commands");
    	ModCommands.register(event.getDispatcher());
    }
}
