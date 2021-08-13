package wilyan_kramer.portable_beacons.common.container;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;


@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ContainerRegistry {
	@SubscribeEvent
	public static void onRegisterContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
			BlockPos pos = data.readBlockPos();
			return new DiffuserContainer(windowId, inv.player.level, pos, inv, inv.player);
		}).setRegistryName(PortableBeaconsMod.MODID, "diffusercontainer") );
	}
}
