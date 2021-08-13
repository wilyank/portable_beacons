package wilyan_kramer.portable_beacons.setup;

import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.client.gui.DiffuserScreen;
import wilyan_kramer.portable_beacons.common.container.ContainerList;
import wilyan_kramer.portable_beacons.common.item.ItemColorizer;
import wilyan_kramer.portable_beacons.common.item.ItemList;

@Mod.EventBusSubscriber(modid = PortableBeaconsMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
	public static void init(final FMLClientSetupEvent event) {
    	ScreenManager.register(ContainerList.diffuserContainer, DiffuserScreen::new);
	}
	
	@SubscribeEvent
    public static void onColorHandlerEvent(ColorHandlerEvent.Item event)
    {
    	event.getItemColors().register(new ItemColorizer(), ItemList.infused_star, ItemList.potion_necklace, ItemList.infused_dagger);
    }
}
